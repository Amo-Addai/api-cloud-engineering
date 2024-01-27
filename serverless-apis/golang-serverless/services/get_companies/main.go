package main

import (
	"log"

	"bitbucket.org/itshospitality/chatbot-api/services/get_companies/lib"
	"bitbucket.org/itshospitality/chatbot-api/services/models"
	"bitbucket.org/itshospitality/chatbot-api/services/utils"
	"github.com/aws/aws-lambda-go/events"
	"github.com/aws/aws-lambda-go/lambda"
)

// Handler - main handler
func Handler(request events.APIGatewayProxyRequest) (events.APIGatewayProxyResponse, error) {
	env := lib.NewEnvironment()
	if env.Debug {
		log.Print("[get_companies] Handler - start")
	}

	user, parseError := models.ParseUser(request.RequestContext.Authorizer)
	if parseError != nil {
		log.Print("Unable to parse user")
		log.Print(parseError)
		return utils.CreateUnauthorizedResponse()
	}

	var companies []models.Company
	var getErr error
	if user.Superadmin {
		companies, getErr = models.GetAllCompanies(env.Debug, env.Region, env.CompaniesTablename)
	} else {
		phones := user.DestinationPhones
		if len(phones) > 0 {
			companies, getErr = models.GetCompanies(env.Debug, env.Region, env.CompaniesTablename, phones)
		}
	}

	if getErr != nil {
		log.Print("Unable to get companies")
		log.Print(getErr)
		companies = []models.Company{}
	}

	var comps []models.CompanyJSON
	for _, conv := range companies {
		comps = append(comps, *conv.ConvertToCompanyJSON())
	}
	payload := map[string]interface{}{"message": "successful", "companies": comps}
	return utils.CreateSuccessfulResponseWithPayload(payload)
}

func main() {
	lambda.Start(Handler)
}
