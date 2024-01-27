package main

import (
	"context"
	"log"
	"time"

	"bitbucket.org/itshospitality/chatbot-api/services/bot_request/lib"
	"bitbucket.org/itshospitality/chatbot-api/services/models"
	"bitbucket.org/itshospitality/chatbot-api/services/utils"
	"github.com/aws/aws-lambda-go/events"
	"github.com/aws/aws-lambda-go/lambda"
	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/guregu/dynamo"
	"github.com/jkeam/boostai"
)

// Request - AWS Request
type Request events.APIGatewayProxyRequest

func openDBConn(env *lib.Environment) (conversationTable dynamo.Table, messageTable dynamo.Table, companiesTable dynamo.Table) {
	db := dynamo.New(session.New(), &aws.Config{Region: aws.String(env.Region)})
	return db.Table(env.ConversationsTablename), db.Table(env.MessagesTablename), db.Table(env.CompaniesTablename)
}

func createClient(boostURL string) *boostai.Client {
	client := boostai.NewClient(boostURL)
	client.TimeoutSeconds = 4
	return client
}

// validBotMessage - message for the bot is only valid if it is short enough
func validBotMessage(message string) bool {
	return len(message) <= 140
}

func callBot(boostConversationID string, boostURL string, filterName string, message string, guestPhone string, debug bool) (string, string, error) {
	convoID := boostConversationID
	var client *boostai.Client

	if debug {
		log.Printf("[callBot] filterName: %s, boostConversationID: %s", filterName, boostConversationID)
	}

	// if not a valid message for the bot, then return immediately
	if !validBotMessage(message) {
		log.Printf("[callBot] not a valid message: '%s'", message)
		tooLongMessage := "Just a moment, please."
		return "", tooLongMessage, nil
	}

	// create boost conversation
	if convoID == "" {
		start := time.Now()
		if debug {
			log.Printf("[callBot] about to call boost for the first time")
		}
		client = createClient(boostURL)
		response, conversationError := client.StartConversationWithFilters([]string{filterName})
		elapsed := time.Since(start)
		if debug {
			log.Printf("[callBot] done calling boost for the first time.  Boost took: %s", elapsed)
		}
		if conversationError != nil {
			log.Printf("[callBot] error while starting conversation with filter")
			log.Print(conversationError)
			return "", "", conversationError
		}
		convoID = response.Conversation.ID
		if debug {
			log.Printf("[callBot] new convoID: %s", convoID)
		}
	}

	start := time.Now()
	if debug {
		log.Printf("[callBot] about to send message to Boost with convoID: %s", convoID)
	}
	client = createClient(boostURL)
	response, sendError := client.SendMessageFromPhone(message, convoID, guestPhone)
	elapsed := time.Since(start)
	if debug {
		log.Printf("[callBot] done sending message to Boost from phone.  Sending took: %s", elapsed)
	}
	if sendError != nil {
		log.Printf("[callBot] error while sending message to Boost from phone.")
		log.Print(sendError)
		return convoID, "", sendError
	}
	return convoID, response.GetMessageText(), nil
}

func ProcessEventMessages(
	eventMessages []models.EventMessage,
	now string,
	companiesTable dynamo.Table,
	conversationsTable dynamo.Table,
	messagesTable dynamo.Table,
	pusherDispatcher *models.EventPublisher,
	topicURL string,
	debug bool,
) {
	for _, eventMessage := range eventMessages {
		guestPhone := eventMessage.FromPhoneNumber
		companyPhone := eventMessage.ToPhoneNumber
		conversationID := eventMessage.ConversationID

		// find conversation
		conversation, findConversationErr := models.GetConversationByID(conversationsTable, conversationID, debug)
		if findConversationErr != nil || conversation == nil {
			log.Print("[ProcessEventMessages] Error while trying to find existing conversation")
			if conversation == nil {
				log.Print("[ProcessEventMessages] Conversation should exist but does not")
			} else {
				log.Print(conversation)
			}

			if findConversationErr != nil {
				log.Print("[ProcessEventMessages] This is the error while trying to find existing conversation")
				log.Print(findConversationErr)
			} else {
				log.Print("[ProcessEventMessages] No error while trying to find conversation")
			}
			continue
		}

		// find company
		company, findCompanyByPhoneErr := models.FindCompanyByPhone(companiesTable, companyPhone, debug)
		if findCompanyByPhoneErr != nil || company == nil {
			log.Print("[ProcessEventMessages] Error while trying to find existing company")
			log.Print(findCompanyByPhoneErr)
			log.Print(company)
			continue
		}

		// conversation exists, call bot and then create message
		boostConversationID, botMessage, callBotErr := callBot(
			conversation.BoostConversationID,
			company.BotURL,
			company.FilterName,
			eventMessage.Body,
			guestPhone,
			debug,
		)
		if callBotErr != nil {
			log.Print("[ProcessEventMessages] Error while trying to call bot")
			log.Print(callBotErr)
			continue
		}

		// update conversation with boost conversation id
		if boostConversationID != "" {
			updateErr := models.SetBoostConversationID(conversationsTable, conversation.ConversationID, boostConversationID, debug)
			if updateErr != nil {
				log.Printf("[ProcessEventMessages] Unable to update conversation (%s) with (%s) Boost conversation id", conversation.ConversationID, boostConversationID)
				log.Print(updateErr)
			} else if debug {
				log.Printf("[ProcessEventMessages] Updated conversation (%s) with (%s) Boost conversation id", conversation.ConversationID, boostConversationID)
			}
		}

		createdMessage, addConversationMessageError := models.AddConversationMessage(messagesTable, now, conversation, botMessage)
		if addConversationMessageError != nil {
			log.Print("[ProcessEventMessages] Error while trying to add bot message")
			log.Print(addConversationMessageError)
			continue
		}
		if debug {
			log.Print("[ProcessEventMessages] New bot message created!")
			log.Print(botMessage)
		}

		// dispatch back to guest
		dispatcher := models.NewTopicEventPublisher(topicURL)
		dispatchErr := dispatcher.PublishMessageTopicEvent(botMessage, conversation.PhoneGuest, conversation.PhoneDestination, conversation.ConversationID, debug)
		if dispatchErr != nil {
			log.Print("[ProcessEventMessages] Error while trying to dispatch event")
			log.Print(dispatchErr)
			continue
		}
		if debug {
			log.Print("[ProcessEventMessages] Event dispatched!")
		}

		// dispatch to ui
		pusherDispatchErr := pusherDispatcher.PublishConversationMessageQueueEvent(createdMessage, debug)
		if pusherDispatchErr != nil {
			log.Print("[ProcessEventMessages] Error while trying to dispatch pusher event")
			log.Print(dispatchErr)
		} else if debug {
			log.Print("[ProcessEventMessages] Pusher event dispatched")
		}
	}
}

// Handler - main handler
func Handler(ctx context.Context, sqsEvent events.SQSEvent) (events.APIGatewayProxyResponse, error) {
	log.Print("[ProcessEventMessages] bot_request start")
	env := lib.NewEnvironment()

	eventMessages := models.ParseMessageEventFromQueue(sqsEvent)
	if env.Debug {
		log.Print(eventMessages)
	}

	conversationsTable, messagesTable, companiesTable := openDBConn(&env)
	ProcessEventMessages(
		eventMessages,
		utils.Now(),
		companiesTable,
		conversationsTable,
		messagesTable,
		models.NewQueueEventPublisher(env.PusherRequestQueueURL),
		env.TopicURL,
		env.Debug,
	)
	return utils.CreateSuccessfulResponse()
}

func main() {
	lambda.Start(Handler)
}
