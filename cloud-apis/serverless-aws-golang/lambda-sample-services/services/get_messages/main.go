package main

import (
	"log"

	"github.com/Amo-Addai/api-feature-development/serverless-apis/golang-serverless/services/get_messages/lib"
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
		log.Print("[get_messages] Handler start")
	}

	// get user
	user, parseError := models.ParseUser(request.RequestContext.Authorizer)
	if parseError != nil {
		log.Print("Unable to parse user")
		log.Print(parseError)
		return utils.CreateUnauthorizedResponse()
	}

	// pull out conversation id
	conversationGUID := request.PathParameters["conversation_id"]
	if env.Debug {
		log.Printf("conversation id: %s", conversationGUID)
	}
	if conversationGUID == "" {
		return utils.CreateBadRequestResponse()
	}

	// see if user is allowed
	conversationTable, messagesTable := openDBConn(&env)
	allowed, userErr := models.IsUserOnConversationByID(conversationTable, conversationGUID, user, env.Debug)
	if userErr != nil {
		log.Print("Unable to get conversation conversation")
		return utils.CreateServerErrorResponse()
	}
	if !allowed {
		log.Print("Unable is not allowed for this conversation")
		return utils.CreateUnauthorizedResponse()
	}

	// get messages
	if env.Debug {
		log.Print("User is allowed.  Now trying to get messages.")
	}
	messages, err := models.GetConversationMessagesByConversationGUID(messagesTable, conversationGUID, env.Debug)
	if err != nil {
		log.Print("Unable to get conversation messages")
		log.Print(err)
	}
	if env.Debug {
		log.Print("Messages retrieved.  Will now convert to JSON.")
	}

	// return json
	var messJSON []models.ConversationMessageJSON
	for _, mess := range messages {
		messJSON = append(messJSON, *mess.ConvertToJSONObject())
	}
	payload := map[string]interface{}{"message": "successful", "messages": messJSON}
	if env.Debug {
		log.Print("Done!  About to return JSON.")
	}
	return utils.CreateSuccessfulResponseWithPayload(payload)
}

func main() {
	lambda.Start(Handler)
}
