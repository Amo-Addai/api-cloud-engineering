package main

import (
	"context"
	"github.com/aws/aws-lambda-go/events"
	"github.com/stretchr/testify/assert"

	"testing"
)

func TestHandler(t *testing.T) {
	ctx := context.Background()
	sqsEvent := events.SQSEvent{}
	response, err := Handler(ctx, sqsEvent)

	assert.Nil(t, err)
	assert.NotNil(t, response)
}
