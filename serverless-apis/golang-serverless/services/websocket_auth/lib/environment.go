package lib

import (
	"os"
	"strconv"
)

// Environment - Environment variables
type Environment struct {
	ConversationsTablename string
	MessagesTablename      string
	Region                 string
	PusherRequestQueueURL  string
	PusherAppID            string
	PusherKey              string
	PusherSecret           string
	PusherCluster          string
	Debug                  bool
}

// NewEnvironment creates a data structure to hold env vars
func NewEnvironment() Environment {
	env := Environment{
		os.Getenv("CONVERSATIONS_TABLENAME"),
		os.Getenv("MESSAGES_TABLENAME"),
		os.Getenv("REGION"),
		os.Getenv("PUSHER_REQUEST_QUEUE_URL"),
		os.Getenv("PUSHER_APP_ID"),
		os.Getenv("PUSHER_KEY"),
		os.Getenv("PUSHER_SECRET"),
		os.Getenv("PUSHER_CLUSTER"),
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
