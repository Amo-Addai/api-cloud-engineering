package main

import (
	"context"
	"github.com/aws/aws-lambda-go/events"
	"github.com/stretchr/testify/assert"

	"testing"
)

func TestHandler(t *testing.T) {
	ctx := context.Background()
	event := events.SQSEvent{}
	response, err := Handler(ctx, event)

	assert.Nil(t, err)
	assert.NotNil(t, response)
}
