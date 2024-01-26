package lib

import (
	"os"
	"strconv"
	"strings"
)

// Environment - Environment variables
type Environment struct {
	Region                 string
	ConversationsTablename string
	SuperuserPhones        []string
	TwilioAccountSid       string
	TwilioAuthToken        string
	FromPhone              string
	NotifiesTablename      string
	Debug                  bool
}

// NewEnvironment creates a data structure to hold env vars
func NewEnvironment() Environment {
	var phones []string
	for _, phone := range strings.Split(os.Getenv("SUPERUSER_PHONES"), ",") {
		cleaned := strings.TrimSpace(phone)
		if cleaned != "" {
			phones = append(phones, cleaned)
		}
	}
	env := Environment{
		os.Getenv("REGION"),
		os.Getenv("CONVERSATIONS_TABLENAME"),
		phones,
		os.Getenv("TWILIO_ACCOUNT_SID"),
		os.Getenv("TWILIO_AUTH_TOKEN"),
		os.Getenv("FROM_PHONE"),
		os.Getenv("NOTIFIES_TABLENAME"),
		false}
	env.setDebug()
	return env
}

func (env *Environment) setDebug() {
	if debug, debugError := strconv.ParseBool(getEnv("DEBUG", "false")); debugError == nil {
		env.Debug = debug
	}
}

func getEnv(key, fallback string) string {
	if value, ok := os.LookupEnv(key); ok {
		return value
	}
	return fallback
}
