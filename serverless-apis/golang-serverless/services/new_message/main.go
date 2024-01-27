package main

import (
	"context"
	"fmt"
	"log"
	"time"

	"bitbucket.org/itshospitality/chatbot-api/services/models"
	"bitbucket.org/itshospitality/chatbot-api/services/new_message/lib"
	"bitbucket.org/itshospitality/chatbot-api/services/utils"
	"github.com/aws/aws-lambda-go/events"
	"github.com/aws/aws-lambda-go/lambda"
	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/guregu/dynamo"
)

// Request - AWS Request
type Request events.APIGatewayProxyRequest

func openDBConn(env *lib.Environment) (conversationTable dynamo.Table, messageTable dynamo.Table, companiesTable dynamo.Table) {
	db := dynamo.New(session.New(), &aws.Config{Region: aws.String(env.Region)})
	return db.Table(env.ConversationsTablename), db.Table(env.MessagesTablename), db.Table(env.CompaniesTablename)
}

func isBotEnabled(conversation *models.Conversation, company *models.Company) bool {
	// bot is disabled company wide
	if !company.BotEnabled {
		return false
	}

	// bot is not even enabled
	if !conversation.BotEnabled {
		return false
	}

	// no times set
	if company.OpenTime == "" || company.CloseTime == "" {
		return true
	}

	// get times
	currentHour, currentMinute := utils.CurrentHourMin()
	openHour, openMinute := company.OpenTimeHourMin()
	closeHour, closeMinute := company.CloseTimeHourMin()

	// in the hour zone
	afterOpen := (currentHour > openHour) || ((currentHour == openHour) && (currentMinute > openMinute))
	beforeClose := (currentHour < closeHour) || ((currentHour == closeHour) && (currentMinute < closeMinute))
	return afterOpen && beforeClose
}

// Handler - main handler
func Handler(ctx context.Context, snsEvent events.SNSEvent) (events.APIGatewayProxyResponse, error) {
	log.Print("[Handler] new_message start")
	env := lib.NewEnvironment()
	debug := env.Debug

	messages := models.ParseEventMessage(snsEvent)
	if debug {
		log.Print(messages)
	}

	conversationsTable, messagesTable, companiesTable := openDBConn(&env)
	now := utils.Now()
	botDispatcher := models.NewQueueEventPublisher(env.BotRequestQueueURL)
	userAckDispatcher := models.NewTopicEventPublisher(env.NewMessageOutTopicURL)
	messagePusherDispatcher := models.NewQueueEventPublisher(env.PusherMessageRequestQueueURL)
	notifyUsersDispatcher := models.NewQueueEventPublisher(env.NotifyUsersQueueURL)
	conversationPusherDispatcher := models.NewQueueEventPublisher(env.PusherConversationRequestQueueURL)
	for _, message := range messages {
		companyPhone := message.ToPhoneNumber
		newConversation := false

		// look up company
		company, findCompanyErr := models.FindCompanyByPhone(companiesTable, companyPhone, debug)
		if findCompanyErr != nil || company == nil {
			log.Print("[Handler] Error while trying to find company")
			if company == nil {
				log.Print("[Handler] Company should not be nil but is")
			}
			if findCompanyErr != nil {
				log.Print("[Handler] Exact Error while finding company")
				log.Print(findCompanyErr)
			}
			continue
		}
		if debug {
			log.Print("[Handler] Company found")
			log.Print(company)
		}

		// look up conversation
		var c *models.Conversation
		conversation, findConversationErr := models.FindConversation(conversationsTable, message.FromPhoneNumber, companyPhone, debug)
		c = conversation

		// error while finding
		if findConversationErr != nil {
			log.Print("[Handler] Error while trying to find existing conversation")
			log.Print(findConversationErr)
			continue
		}

		var startSave time.Time
		var doneSaving time.Duration
		// unable to find conversation
		if conversation == nil {
			newConversation = true
			startSave = time.Now()
			conversation, addConversationError := models.AddConversation(conversationsTable, &message, company.CompanyID)
			doneSaving = time.Since(startSave)
			if debug {
				log.Printf("[Handler] Adding new conversation took: %s", doneSaving)
			}

			c = conversation
			// send to ui
			dispatchErr := conversationPusherDispatcher.PublishConversationQueueEvent(c, debug)
			if dispatchErr != nil {
				log.Print("[Handler] Error while trying to dispatch new conversation creation to pusher")
				log.Print(dispatchErr)
			}

			if addConversationError != nil || findConversationErr != nil {
				log.Print("[Handler] Error while trying to add conversation")
				log.Print(addConversationError)
				continue
			}
			if debug {
				log.Print("[Handler] New conversation created!")
			}
		} else if c.LastMessageCreatedAt < message.CreatedAt {
			// set last message info on conversation
			c.LastMessageCreatedAt = message.CreatedAt
			c.LastMessage = message.Body
			c.Read = false

			startSave = time.Now()
			_, saveErr := models.SaveConversation(conversationsTable, c)
			doneSaving := time.Since(startSave)
			if debug {
				log.Printf("[Handler] Updating conversation took: %s", doneSaving)
			}

			if saveErr != nil {
				log.Print("[Handler] Error while trying to update last message on conversation")
				log.Print(saveErr)
			}
		}

		// conversation exists, create message from guest
		start := time.Now()
		createdMessage, addConversationMessageError := models.AddConversationMessageFromGuest(messagesTable, now, c, message.Body)
		if addConversationMessageError != nil {
			log.Print("[Handler] Error while trying to add message")
			log.Print(addConversationMessageError)
			continue
		}
		elapsed := time.Since(start)
		if debug {
			log.Print("[Handler] New message created!")
			log.Printf("[Handler] Message creation took: %s", elapsed)
			log.Print(createdMessage)
		}

		// dispatch to super users
		notifyUsersDispatchErr := notifyUsersDispatcher.PublishConversationMessageQueueEvent(createdMessage, debug)
		if notifyUsersDispatchErr != nil {
			log.Print("[Handler] Error while trying to dispatch to notify users")
			log.Print(notifyUsersDispatchErr)
		} else if debug {
			log.Print("[Handler] Notify users event dispatched")
		}

		// dispatch to bot
		if isBotEnabled(c, company) {
			dispatchErr := botDispatcher.PublishMessageQueueEvent(message.Body, c.PhoneGuest, c.PhoneDestination, c.ConversationID, debug)
			if dispatchErr != nil {
				log.Print("[Handler] Error while trying to dispatch bot event")
				log.Print(dispatchErr)
			} else if debug {
				log.Print("[Handler] Bot event dispatched")
			}
		} else {
			if env.Debug {
				log.Print("[Handler] Bot event not dispatched as bot is off")
			}
		}

		// dispatch to ui
		dispatchErr := messagePusherDispatcher.PublishConversationMessageQueueEvent(createdMessage, debug)
		if dispatchErr != nil {
			log.Print("[Handler] Error while trying to dispatch pusher event")
			log.Print(dispatchErr)
		} else if debug {
			log.Print("[Handler] Pusher event dispatched")
		}

		// create special ack message
		if newConversation {
			ackMessage := fmt.Sprintf("Hi, I'm AVVI, your virtual assistant at %s. Give me just a moment to assist you.", company.Name)

			// save ack message in system
			companyAckMessage, createMessageError := models.AddConversationMessage(messagesTable, utils.Now(), c, ackMessage)
			if createMessageError != nil {
				log.Print("[Handler] Error while trying to create ack message")
				log.Print(dispatchErr)
			} else if debug {
				log.Print("[Handler] Ack message successfully created")
			}

			// dispatch ack message to UI
			dispatchErr := messagePusherDispatcher.PublishConversationMessageQueueEvent(companyAckMessage, debug)
			if dispatchErr != nil {
				log.Print("[Handler] Error while trying to dispatch pusher event for company ack message")
				log.Print(dispatchErr)
			} else if debug {
				log.Print("[Handler] Pusher event dispatched for ack message")
			}

			// send ack response back to user
			dispatchErr = userAckDispatcher.PublishMessageTopicEvent(ackMessage, c.PhoneGuest, c.PhoneDestination, c.ConversationID, debug)
			if dispatchErr != nil {
				log.Print("[Handler] Error while trying to dispatch ack back to user")
				log.Print(dispatchErr)
			} else if env.Debug {
				log.Print("[Handler] Ack message back to user successfully dispatched")
			}
		}
	}

	return utils.CreateSuccessfulResponse()
}

func main() {
	lambda.Start(Handler)
}
