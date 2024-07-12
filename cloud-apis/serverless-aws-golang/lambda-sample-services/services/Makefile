build:
	env GOOS=linux go build -ldflags="-s -w" -o bin/new_sms new_sms/main.go
	env GOOS=linux go build -ldflags="-s -w" -o bin/new_message new_message/main.go
	env GOOS=linux go build -ldflags="-s -w" -o bin/bot_request bot_request/main.go
	env GOOS=linux go build -ldflags="-s -w" -o bin/new_message_out new_message_out/main.go
	env GOOS=linux go build -ldflags="-s -w" -o bin/get_conversations get_conversations/main.go
	env GOOS=linux go build -ldflags="-s -w" -o bin/get_messages get_messages/main.go
	env GOOS=linux go build -ldflags="-s -w" -o bin/create_message create_message/main.go
	env GOOS=linux go build -ldflags="-s -w" -o bin/message_feed message_feed/main.go
	env GOOS=linux go build -ldflags="-s -w" -o bin/conversation_feed conversation_feed/main.go
	env GOOS=linux go build -ldflags="-s -w" -o bin/edit_conversation edit_conversation/main.go
	env GOOS=linux go build -ldflags="-s -w" -o bin/notify_superusers notify_superusers/main.go
	env GOOS=linux go build -ldflags="-s -w" -o bin/websocket_auth websocket_auth/main.go
	env GOOS=linux go build -ldflags="-s -w" -o bin/get_companies get_companies/main.go

.PHONY: test
test:
	go test ./new_sms
	go test ./new_sms/lib
	go test ./new_message
	go test ./new_message/lib
	go test ./new_message_out
	go test ./new_message_out/lib
	go test ./bot_request
	go test ./bot_request/lib
	go test ./get_messages
	go test ./get_messages/lib
	go test ./get_conversations
	go test ./get_conversations/lib
	go test ./create_message
	go test ./create_message/lib
	go test ./message_feed
	go test ./message_feed/lib
	go test ./conversation_feed
	go test ./conversation_feed/lib
	go test ./edit_conversation
	go test ./edit_conversation/lib
	go test ./notify_superusers
	go test ./notify_superusers/lib
	go test ./websocket_auth
	go test ./websocket_auth/lib
	go test ./get_companies
	go test ./get_companies/lib

.PHONY: clean
clean:
	rm -rf ./bin ./vendor Gopkg.lock

.PHONY: deploy
deploy: clean build
	sls deploy --verbose --aws-profile chatbot -r us-east-1 --force

.PHONY:
deploy_dev: clean build
	sls deploy -s dev --verbose --aws-profile chatbot -r us-east-1 --force

.PHONY:
deploy_prod: clean build
	sls deploy -s prod --verbose --aws-profile chatbot -r us-east-1 --force
