package lib

import (
	"os"
	"strconv"
)

// Environment - Environment variables
type Environment struct {
	Region                            string
	ConversationsTablename            string
	MessagesTablename                 string
	CompaniesTablename                string
	BotRequestQueueURL                string
	PusherMessageRequestQueueURL      string
	PusherConversationRequestQueueURL string
	NotifyUsersQueueURL               string
	NewMessageOutTopicURL             string
	Debug                             bool
}

// NewEnvironment creates a data structure to hold env vars
func NewEnvironment() Environment {
	env := Environment{
		os.Getenv("REGION"),
		os.Getenv("CONVERSATIONS_TABLENAME"),
		os.Getenv("MESSAGES_TABLENAME"),
		os.Getenv("COMPANIES_TABLENAME"),
		os.Getenv("BOT_REQUEST_QUEUE_URL"),
		os.Getenv("PUSHER_MESSAGE_REQUEST_QUEUE_URL"),
		os.Getenv("PUSHER_CONVERSATION_REQUEST_QUEUE_URL"),
		os.Getenv("NOTIFY_USERS_QUEUE_URL"),
		os.Getenv("NEW_MESSAGE_OUT_TOPIC_URL"),
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
