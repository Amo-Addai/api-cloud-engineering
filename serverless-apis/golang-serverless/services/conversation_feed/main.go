package main

import (
	"context"
	"fmt"
	"log"

	"bitbucket.org/itshospitality/chatbot-api/services/conversation_feed/lib"
	"bitbucket.org/itshospitality/chatbot-api/services/models"
	"bitbucket.org/itshospitality/chatbot-api/services/utils"
	"github.com/aws/aws-lambda-go/events"
	"github.com/aws/aws-lambda-go/lambda"
)

// Request - AWS Request
type Request events.APIGatewayProxyRequest

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
		log.Print("[conversation_feed] Handler - start")
	}

	for _, record := range sqsEvent.Records {
		data := make(map[string]string)
		data["body"] = record.Body

		// parse
		conversation := models.NewConversationFromJSON(record.Body)

		// get users
		var userIDs []string
		var getUsersErr error
		userIDs, getUsersErr = models.GetUsersOnConversation(env.CognitoUserPoolID, conversation.PhoneDestination)
		if getUsersErr != nil {
			log.Print("[conversation_feed] Error while fetching users on the conversation")
			log.Print(getUsersErr)
			userIDs = make([]string, 0)
		}

		// get admins
		var adminUserIDs []string
		var getAdminErr error
		adminUserIDs, getAdminErr = models.GetUsersInGroup(env.CognitoUserPoolID, "superadmin")
		if getAdminErr != nil {
			log.Print("[conversation_feed] Error while fetching admins on the conversation")
			log.Print(getAdminErr)
			adminUserIDs = make([]string, 0)
		}

		// dispatch
		dispatchErr := dispatch(&env, data, utils.Unique(utils.Concat(userIDs, adminUserIDs)))
		if dispatchErr != nil {
			log.Print("[conversation_feed] Error while trying to dispatch pusher event")
			log.Print(dispatchErr)
			continue
		}
		if debug {
			log.Print("[conversation_feed] Pusher event dispatched successfully")
		}
	}

	return utils.CreateSuccessfulResponse()
}

func main() {
	lambda.Start(Handler)
}
