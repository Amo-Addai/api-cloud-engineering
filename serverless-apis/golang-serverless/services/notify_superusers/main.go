package main

import (
	"context"
	"fmt"
	"log"

	"bitbucket.org/itshospitality/chatbot-api/services/models"
	"bitbucket.org/itshospitality/chatbot-api/services/notify_superusers/lib"
	"bitbucket.org/itshospitality/chatbot-api/services/utils"
	"github.com/aws/aws-lambda-go/events"
	"github.com/aws/aws-lambda-go/lambda"
	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/guregu/dynamo"
)

func openDBConn(env *lib.Environment) (dynamo.Table, dynamo.Table) {
	db := dynamo.New(session.New(), &aws.Config{Region: aws.String(env.Region)})
	return db.Table(env.ConversationsTablename), db.Table(env.NotifiesTablename)
}

func createMessage(env *lib.Environment, phone string, messageBody string, conversation *models.Conversation) string {
	guestPhone := conversation.PhoneGuest
	appURL := "https://app.chatbot.ai"
	body := fmt.Sprintf("%s: %s %s", guestPhone, messageBody, appURL)
	if conversation.GuestName != "" {
		body = fmt.Sprintf("%s (%s): %s %s", conversation.GuestName, guestPhone, messageBody, appURL)
	}
	if env.Debug {
		log.Printf("Trying to send %s from %s: %s", phone, env.FromPhone, body)
	}
	return body
}

func textUsers(env *lib.Environment, message *models.ConversationMessageJSON, conversation *models.Conversation, notifies []models.Notify) {
	texter := utils.NewTexter(env.TwilioAccountSid, env.TwilioAuthToken)
	adminPhones := []string{}
	for _, notify := range notifies {
		adminPhones = append(adminPhones, notify.PhoneAdmin)
	}

	for _, phone := range utils.Unique(utils.Concat(env.SuperuserPhones, adminPhones)) {
		err := texter.Text(env.FromPhone, phone, createMessage(env, phone, message.Body, conversation))
		if err != nil {
			log.Printf("Error while texting phone - %s", phone)
			log.Print(err)
		}
	}
}

// Handler - main handler
func Handler(ctx context.Context, sqsEvent events.SQSEvent) (events.APIGatewayProxyResponse, error) {
	log.Print("[notify_superusers] Handler - start")
	env := lib.NewEnvironment()
	debug := env.Debug

	conversationsTable, notifyTable := openDBConn(&env)
	for _, record := range sqsEvent.Records {
		data := make(map[string]string)
		data["body"] = record.Body

		// parse message
		message, parseErr := models.FromJSONToConversationMessageJSON(record.Body)
		if parseErr != nil {
			log.Print("[notify_superusers] Unable to parse json string back to conversation message")
			log.Print(parseErr)
			continue
		}

		// get conversation
		conversation, getConvoErr := models.GetConversationByID(conversationsTable, message.ConversationID, debug)
		if getConvoErr != nil {
			log.Print("[notify_superusers] Unable to get conversation by id")
			log.Print(getConvoErr)
			continue
		}

		if conversation == nil {
			log.Print("[notify_superusers] No conversation found")
			continue
		}

		// fetch notifies
		notifies, fetchNotifyErr := models.GetNotifiesForConversation(notifyTable, conversation.DestinationGUID)
		if fetchNotifyErr != nil {
			log.Print("[notify_superusers] Error while trying to fetch notifies")
			log.Print(fetchNotifyErr)
			continue
		}

		// look up superusers and text them all
		textUsers(&env, message, conversation, notifies)
	}

	return utils.CreateSuccessfulResponse()
}

func main() {
	lambda.Start(Handler)
}
