package main

import (
	"context"
	"log"

	"bitbucket.org/itshospitality/chattabot-api/services/models"
	"bitbucket.org/itshospitality/chattabot-api/services/new_message_out/lib"
	"bitbucket.org/itshospitality/chattabot-api/services/utils"
	"github.com/aws/aws-lambda-go/events"
	"github.com/aws/aws-lambda-go/lambda"
)

// Text - text a phone number
func text(env *lib.Environment, phone string, companyPhone string, message string) error {
	texter := utils.NewTexter(env.TwilioAccountSid, env.TwilioAuthToken)
	err := texter.Text(companyPhone, phone, message)
	return err
}

// Handler - main handler
func Handler(ctx context.Context, snsEvent events.SNSEvent) (events.APIGatewayProxyResponse, error) {
	log.Print("new_message_out start")
	env := lib.NewEnvironment()
	debug := env.Debug

	for _, record := range snsEvent.Records {
		snsRecord := record.SNS
		message := snsRecord.Message
		body, guestPhone, companyPhone, _, parseErr := models.MessageEventFromJSON(message)
		if debug {
			log.Printf("%s - %s, ", guestPhone, body)
		}
		if parseErr == nil {
			textErr := text(&env, guestPhone, companyPhone, body)
			if textErr != nil {
				log.Print("FAILURE, need to put this into a DLQ")
				log.Print(textErr)
			} else {
				if debug {
					log.Printf("%s - %s", guestPhone, body)
				}
			}
		} else {
			log.Print("FAILURE, need to put this into a DLQ")
			log.Printf("[%s %s] Message = %s \n", record.EventSource, snsRecord.Timestamp, snsRecord.Message)
		}
	}

	return utils.CreateSuccessfulResponse()
}

func main() {
	lambda.Start(Handler)
}
