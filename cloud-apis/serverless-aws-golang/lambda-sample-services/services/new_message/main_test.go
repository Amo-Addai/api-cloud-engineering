package main

import (
	"context"
	"github.com/aws/aws-lambda-go/events"
	"github.com/stretchr/testify/assert"

	"testing"
)

func TestHandler(t *testing.T) {
	ctx := context.Background()
	snsEvent := events.SNSEvent{}
	response, err := Handler(ctx, snsEvent)

	assert.Nil(t, err)
	assert.NotNil(t, response)
}
