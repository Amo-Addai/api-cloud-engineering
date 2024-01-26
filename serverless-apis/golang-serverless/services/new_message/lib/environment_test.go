package lib

import (
	"github.com/stretchr/testify/assert"

	"testing"
)

func TestGetEnvironment(t *testing.T) {
	environment := NewEnvironment()
	assert.Equal(t, false, environment.Debug)
	assert.Equal(t, "", environment.Region)
	assert.Equal(t, "", environment.ConversationsTablename)
	assert.Equal(t, "", environment.MessagesTablename)
	assert.Equal(t, "", environment.CompaniesTablename)
	assert.Equal(t, "", environment.BotRequestQueueURL)
	assert.Equal(t, "", environment.PusherMessageRequestQueueURL)
	assert.Equal(t, "", environment.PusherConversationRequestQueueURL)
	assert.Equal(t, "", environment.NotifyUsersQueueURL)
	assert.Equal(t, "", environment.NewMessageOutTopicURL)
}
