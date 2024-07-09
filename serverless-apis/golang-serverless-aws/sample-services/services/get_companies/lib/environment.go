package lib

import (
	"os"
	"strconv"
)

// Environment - Environment variables
type Environment struct {
	CompaniesTablename string
	Region             string
	Debug              bool
}

// NewEnvironment creates a data structure to hold env vars
func NewEnvironment() Environment {
	env := Environment{
		os.Getenv("COMPANIES_TABLENAME"),
		os.Getenv("REGION"),
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
