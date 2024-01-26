package main

import (
	"bytes"
	"encoding/json"
	"log"
	"net/url"
	"regexp"

	"bitbucket.org/itshospitality/chattabot-api/services/models"
	"bitbucket.org/itshospitality/chattabot-api/services/websocket_auth/lib"
	"github.com/aws/aws-lambda-go/events"
	"github.com/aws/aws-lambda-go/lambda"
	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/guregu/dynamo"
)

// Response - AWS Response
type Response events.APIGatewayProxyResponse

// Websocket Auth Event - request for pusher to auth before connected to websocket
type websocketAuthEvent struct {
	ChannelName string `json:"channel_name"`
	SocketID    string `json:"socket_id"`
}

func createUnauthorizedResponse() (Response, error) {
	var buf bytes.Buffer
	body, err := json.Marshal(map[string]interface{}{"message": "unauthorized"})
	if err != nil {
		return Response{StatusCode: 500}, err
	}
	json.HTMLEscape(&buf, body)
	resp := Response{
		StatusCode:      401,
		IsBase64Encoded: false,
		Body:            buf.String(),
		Headers: map[string]string{
			"Content-Type":                     "application/json",
			"Access-Control-Allow-Origin":      "*",
			"Access-Control-Allow-Credentials": "true",
		},
	}

	return resp, nil
}

func createSuccessfulResponse(env *lib.Environment, requestBody []byte) (Response, error) {
	response, err := models.AuthenticatePrivateChannel(
		env.PusherAppID,
		env.PusherKey,
		env.PusherSecret,
		env.PusherCluster,
		requestBody)

	if err != nil {
		return Response{StatusCode: 500, Body: err.Error()}, err
	}
	resp := Response{
		StatusCode:      200,
		IsBase64Encoded: false,
		Body:            string(response),
		Headers: map[string]string{
			"Access-Control-Allow-Origin":      "*",
			"Access-Control-Allow-Credentials": "true",
		},
	}

	return resp, nil
}

func createFailureResponse() (Response, error) {
	return Response{
		StatusCode:      503,
		IsBase64Encoded: false,
		Headers: map[string]string{
			"Content-Type":                     "application/json",
			"Access-Control-Allow-Origin":      "*",
			"Access-Control-Allow-Credentials": "true",
		},
	}, nil
}

func openDBConn(env *lib.Environment) (dynamo.Table, dynamo.Table) {
	db := dynamo.New(session.New(), &aws.Config{Region: aws.String(env.Region)})
	return db.Table(env.ConversationsTablename), db.Table(env.MessagesTablename)
}

func getChannelName(channelName string) string {
	re := regexp.MustCompile(`^private-(.+)$`)
	match := re.FindStringSubmatch(channelName)
	resp := channelName
	if len(match) == 2 {
		resp = match[1]
	}
	return resp
}

func parseEvent(body string) (*websocketAuthEvent, error) {
	urlencoded, err := url.ParseQuery(body)
	if err != nil {
		return nil, err
	}
	return &websocketAuthEvent{
		ChannelName: urlencoded.Get("channel_name"),
		SocketID:    urlencoded.Get("socket_id"),
	}, nil
}

func userIsAllowed(payload *websocketAuthEvent, user *models.User, debug bool) bool {
	channelName := getChannelName(payload.ChannelName)
	if debug {
		log.Printf("channelName: %s, id: %s", channelName, user.ID)
	}
	return user.ID == channelName
}

// Handler - main handler
func Handler(request events.APIGatewayProxyRequest) (Response, error) {
	env := lib.NewEnvironment()
	debug := env.Debug
	if debug {
		log.Print("[websocket_can_subscribe] Handler - start")
	}

	// check user
	user, parseError := models.ParseUser(request.RequestContext.Authorizer)
	if parseError != nil {
		log.Print("Unable to parse user")
		log.Print(parseError)
		return createUnauthorizedResponse()
	}

	// parse message
	payload, parseErr := parseEvent(request.Body)
	if parseErr != nil {
		log.Print("Error while parsing query from request body")
		log.Print(parseErr)
		return createFailureResponse()
	}

	allowed := userIsAllowed(payload, user, debug)
	if debug {
		log.Print("Params:")
		log.Print(payload)
		log.Print(user)
		log.Printf("allowed: %t", allowed)
	}

	if allowed {
		return createSuccessfulResponse(&env, []byte(request.Body))
	}
	return createUnauthorizedResponse()
}

func main() {
	lambda.Start(Handler)
}
