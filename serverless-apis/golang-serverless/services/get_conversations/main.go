package main

import (
	"log"
	"strconv"
	"time"

	"bitbucket.org/itshospitality/chatbot-api/services/get_conversations/lib"
	"bitbucket.org/itshospitality/chatbot-api/services/models"
	"bitbucket.org/itshospitality/chatbot-api/services/utils"
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

// Handler - main handler
func Handler(request events.APIGatewayProxyRequest) (events.APIGatewayProxyResponse, error) {
	env := lib.NewEnvironment()
	if env.Debug {
		log.Print("[get_conversations] Handler - start")
	}

	// parse input since
	since := request.QueryStringParameters["since"]
	pageSizeParam := request.QueryStringParameters["pageSize"]

	// page size
	var pageSize int64 = 0
	var pageSizeNumberError error
	if pageSizeParam != "" {
		log.Printf("pageSize is NOT nil, pageSize: %s", pageSizeParam)
		pageSize, pageSizeNumberError = strconv.ParseInt(pageSizeParam, 10, 64)
		if pageSizeNumberError != nil {
			log.Print("Unable to parse pageSize, resetting to nil")
			log.Print(pageSizeNumberError)
		}
	} else {
		log.Print("No pageSize")
	}

	// since
	var sinceTime time.Time
	var sinceParseError error
	getAll := true
	if since != "" {
		log.Printf("since is NOT nil, since: %s", since)
		sinceTime, sinceParseError = utils.ParseDatetime(since)
		if sinceParseError != nil {
			log.Print("Unable to parse since datetime, resetting to nil")
			log.Print(sinceParseError)
			getAll = true
		} else {
			getAll = false
		}
	} else {
		log.Print("No since")
	}

	// check user
	user, parseError := models.ParseUser(request.RequestContext.Authorizer)
	if parseError != nil {
		log.Print("Unable to parse user")
		log.Print(parseError)
		return utils.CreateUnauthorizedResponse()
	}

	// get conversations
	table := openDBConn(&env)
	var conversations []models.Conversation
	var getErr error
	if user.Superadmin {
		if getAll {
			conversations, getErr = models.GetConversations(table, pageSize, env.Debug)
		} else {
			conversations, getErr = models.GetConversationsSince(table, sinceTime, env.Debug)
		}
	} else {
		phones := user.DestinationPhones
		if len(phones) > 0 {
			conversations, getErr = models.GetConversationForPhones(env.Region, env.ConversationsTablename, phones, env.Debug)
		}
	}

	if getErr != nil {
		log.Print("Unable to get conversations")
		log.Print(getErr)
		conversations = []models.Conversation{}
	}

	var convos []models.ConversationJSON
	for _, conv := range conversations {
		convos = append(convos, *conv.ToJSONObject())
	}
	payload := map[string]interface{}{"message": "successful", "conversations": convos}
	return utils.CreateSuccessfulResponseWithPayload(payload)
}

func main() {
	lambda.Start(Handler)
}
