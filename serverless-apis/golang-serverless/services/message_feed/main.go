package main

import (
	"context"
	"fmt"
	"log"

	"bitbucket.org/itshospitality/chattabot-api/services/message_feed/lib"
	"bitbucket.org/itshospitality/chattabot-api/services/models"
	"bitbucket.org/itshospitality/chattabot-api/services/utils"
	"github.com/aws/aws-lambda-go/events"
	"github.com/aws/aws-lambda-go/lambda"
	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/guregu/dynamo"
)

func openDBConn(env *lib.Environment) dynamo.Table {
	db := dynamo.New(session.New(), &aws.Config{Region: aws.String(env.Region)})
	return db.Table(env.ConversationsTablename)
}

func dispatch(env *lib.Environment, data map[string]string, userIDs []string) error {
	channelNames := make([]string, len(userIDs))
	for i, userID := range userIDs {
		channelNames[i] = fmt.Sprintf("private-%s", userID)
	}

	return models.PublishMessageFeedEvent(
		env.PusherAppID,
		env.PusherKey,
		env.PusherSecret,
		env.PusherCluster,
		data,
		channelNames,
		env.PusherEventName,
	)
}

// Handler - main handler
func Handler(ctx context.Context, sqsEvent events.SQSEvent) (events.APIGatewayProxyResponse, error) {
	env := lib.NewEnvironment()
	debug := env.Debug
	if debug {
		log.Print("[message_feed] Handler - start")
	}

	for _, record := range sqsEvent.Records {
		data := make(map[string]string)
		data["body"] = record.Body

		// parse message
		message, parseErr := models.FromJSONToConversationMessageJSON(record.Body)
		if parseErr != nil {
			log.Print("[message_feed] Unable to parse json string back to conversation message")
			log.Print(parseErr)
			continue
		}

		// get conversation
		conversationTable := openDBConn(&env)
		conversation, getConvoErr := models.GetConversationByID(conversationTable, message.ConversationID, debug)
		if getConvoErr != nil {
			log.Print("[message_feed] Unable to get conversation by id")
			log.Print(getConvoErr)
			continue
		}

		// get users
		userIDs, getUsersErr := models.GetUsersOnConversation(env.CognitoUserPoolID, conversation.PhoneDestination)
		if getUsersErr != nil {
			log.Print("[message_feed] Error while fetching users on the conversation")
			log.Print(getUsersErr)
			continue
		}

		// get admins
		adminUserIDs, getAdminErr := models.GetUsersInGroup(env.CognitoUserPoolID, "superadmin")
		if getAdminErr != nil {
			log.Print("[message_feed] Error while fetching admins on the conversation")
			log.Print(getAdminErr)
			continue
		}

		// dispatch
		dispatchErr := dispatch(&env, data, utils.Unique(utils.Concat(userIDs, adminUserIDs)))
		if dispatchErr != nil {
			log.Print("[message_feed] Error while trying to dispatch pusher event")
			log.Print(dispatchErr)
			continue
		}
		if debug {
			log.Print("[message_feed] Pusher event dispatched successfully")
		}
	}

	return utils.CreateSuccessfulResponse()
}

func main() {
	lambda.Start(Handler)
}
