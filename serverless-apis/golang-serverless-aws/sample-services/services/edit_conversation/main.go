package main

import (
	"encoding/json"
	"log"

	"github.com/Amo-Addai/api-feature-development/serverless-apis/golang-serverless/services/edit_conversation/lib"
	"github.com/Amo-Addai/api-feature-development/serverless-apis/golang-serverless/services/models"
	"github.com/Amo-Addai/api-feature-development/serverless-apis/golang-serverless/services/utils"
	"github.com/aws/aws-lambda-go/events"
	"github.com/aws/aws-lambda-go/lambda"
	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/guregu/dynamo"
)

func parseRequest(jsonString string, conversation *models.Conversation) error {
	message := struct {
		BotEnabled bool   `json:"bot_enabled"`
		GuestName  string `json:"guest_name"`
		GuestNotes string `json:"guest_notes"`
		CheckedAt  string `json:"checked_at"`
	}{
		BotEnabled: conversation.BotEnabled,
		GuestName:  conversation.GuestName,
		GuestNotes: conversation.GuestNotes,
		CheckedAt:  conversation.CheckedAt,
	}
	parseErr := json.Unmarshal([]byte(jsonString), &message)
	if parseErr != nil {
		return parseErr
	}

	conversation.BotEnabled = message.BotEnabled
	conversation.GuestName = message.GuestName
	conversation.GuestNotes = message.GuestNotes
	conversation.CheckedAt = message.CheckedAt

	checkedAt, parseErr1 := utils.ParseDatetime(conversation.CheckedAt)
	if parseErr1 != nil {
		log.Printf("Unable to parse %s", conversation.CheckedAt)
		return parseErr1
	}

	if conversation.LastMessageCreatedAt == "" {
		conversation.Read = true
	} else {
		lastMessageCreatedAt, parseErr2 := utils.ParseDatetime(conversation.LastMessageCreatedAt)
		if parseErr1 != nil {
			log.Printf("Unable to parse %s", conversation.LastMessageCreatedAt)
			return parseErr2
		}
		if checkedAt.After(lastMessageCreatedAt) || checkedAt.Equal(lastMessageCreatedAt) {
			conversation.Read = true
		}
	}

	return nil
}

func openDBConn(env *lib.Environment) dynamo.Table {
	db := dynamo.New(session.New(), &aws.Config{Region: aws.String(env.Region)})
	return db.Table(env.ConversationsTablename)
}

// Handler - main handler
func Handler(request events.APIGatewayProxyRequest) (events.APIGatewayProxyResponse, error) {
	env := lib.NewEnvironment()
	if env.Debug {
		log.Print("[edit_conversation] Handler - start")
	}

	// check user
	user, parseError := models.ParseUser(request.RequestContext.Authorizer)
	if parseError != nil {
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

	// Get conversation
	conversationsTable := openDBConn(&env)
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

	// parse convo
	if env.Debug {
		log.Print("Input")
		log.Print(request.Body)
	}
	parseErr := parseRequest(request.Body, conversation)
	if parseErr != nil {
		log.Print("Unable to parse body")
		log.Print(parseErr)
		return utils.CreateFailureResponse()
	}

	// update
	if env.Debug {
		log.Print("Saving conversation")
		log.Print(conversation)
	}
	_, saveErr := models.SaveConversation(conversationsTable, conversation)
	if saveErr != nil {
		log.Print("Unable to update conversation")
		log.Print(saveErr)
		return utils.CreateFailureResponse()
	}

	// send to ui
	pusherDispatcher := models.NewQueueEventPublisher(env.PusherConversationRequestQueueURL)
	pusherDispatchErr := pusherDispatcher.PublishConversationQueueEvent(conversation, env.Debug)
	if pusherDispatchErr != nil {
		log.Print("Error while trying to dispatch pusher event")
		log.Print(pusherDispatchErr)
	} else if env.Debug {
		log.Print("Pusher event dispatched")
	}

	payload := map[string]interface{}{"message": "successful", "conversation": conversation.ToJSONObject()}
	return utils.CreateSuccessfulResponseWithPayload(payload)
}

func main() {
	lambda.Start(Handler)
}
