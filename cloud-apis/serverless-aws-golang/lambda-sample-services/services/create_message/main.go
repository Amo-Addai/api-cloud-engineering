package main

import (
	"log"

	"github.com/Amo-Addai/api-feature-development/serverless-apis/golang-serverless/services/create_message/lib"
	"github.com/Amo-Addai/api-feature-development/serverless-apis/golang-serverless/services/models"
	"github.com/Amo-Addai/api-feature-development/serverless-apis/golang-serverless/services/utils"
	"github.com/aws/aws-lambda-go/events"
	"github.com/aws/aws-lambda-go/lambda"
	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/guregu/dynamo"
)

func openDBConn(env *lib.Environment) (dynamo.Table, dynamo.Table) {
	db := dynamo.New(session.New(), &aws.Config{Region: aws.String(env.Region)})
	return db.Table(env.ConversationsTablename), db.Table(env.MessagesTablename)
}

// Handler - main handler
func Handler(request events.APIGatewayProxyRequest) (events.APIGatewayProxyResponse, error) {
	env := lib.NewEnvironment()
	if env.Debug {
		log.Print("[create_message] Handler - start")
	}

	// check user
	user, parseError := models.ParseUser(request.RequestContext.Authorizer)
	if parseError != nil || user == nil {
		log.Print("Unable to parse user")
		log.Print(parseError)
		return utils.CreateUnauthorizedResponse()
	}

	conversationGUID := request.PathParameters["conversation_id"]
	if env.Debug {
		log.Printf("conversation id: %s", conversationGUID)
	}
	if conversationGUID == "" {
		return utils.CreateFailureResponse()
	}

	// parse message
	body, _, _, _, parseErr := models.MessageEventFromJSON(request.Body)
	if parseErr != nil {
		log.Print("Unable to parse body")
		log.Print(parseErr)
		return utils.CreateFailureResponse()
	}
	if env.Debug {
		log.Printf("body: %s", body)
	}

	// Get conversation
	conversationsTable, messagesTable := openDBConn(&env)
	conversation, getConversationErr := models.GetConversationByID(conversationsTable, conversationGUID, env.Debug)
	if getConversationErr != nil {
		log.Print("Unable to get conversation by id")
		log.Print(getConversationErr)
		return utils.CreateFailureResponse()
	}

	// see if allowed
	allowed := models.IsUserOnConversation(conversation, user, env.Debug)
	if !allowed {
		log.Print("Unable is not allowed for this conversation")
		return utils.CreateUnauthorizedResponse()
	}

	// Create new message
	message, createMessageError := models.AddConversationMessage(messagesTable, utils.Now(), conversation, body)
	if createMessageError != nil {
		log.Print("Unable to create conversation message")
		log.Print(createMessageError)
		return utils.CreateFailureResponse()
	}

	// dispatch event so that the text gets back to the user
	dispatcher := models.NewTopicEventPublisher(env.TopicURL)
	dispatchErr := dispatcher.PublishMessageTopicEvent(body, conversation.PhoneGuest, conversation.PhoneDestination, conversation.ConversationID, env.Debug)
	if dispatchErr != nil {
		log.Print("Error while trying to dispatch to user event")
		log.Print(dispatchErr)
		return utils.CreateFailureResponse()
	}
	if env.Debug {
		log.Print("To User event dispatched")
	}

	// dispatch to ui
	pusherDispatcher := models.NewQueueEventPublisher(env.PusherRequestQueueURL)
	pusherDispatchErr := pusherDispatcher.PublishConversationMessageQueueEvent(message, env.Debug)
	if pusherDispatchErr != nil {
		log.Print("Error while trying to dispatch pusher event")
		log.Print(dispatchErr)
	} else if env.Debug {
		log.Print("Pusher event dispatched")
	}

	payload := map[string]interface{}{"message": "successful", "conversation_message": message.ConvertToJSONObject()}
	return utils.CreateSuccessfulResponseWithPayload(payload)
}

func main() {
	lambda.Start(Handler)
}
