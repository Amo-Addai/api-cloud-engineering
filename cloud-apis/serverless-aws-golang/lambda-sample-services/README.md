# Golang Serverless - ChatBot Microservice APIs

## Prerequisites
1.  Golang v1.14
2.  Serverless v1.61
3.  Node v15.5.1

## Setup

### Serverless Config

Update the following in the `serverless.yaml` config

#### General

1.  Replace `SUPERUSER_PHONES` with your phone number
2.  Ensure `CallbackURLs` is set to your Cloudfront distribution

#### Twilio

1.  Ensure `TWILIO_ACCOUNT_SID` is correct
2.  Ensure `TWILIO_AUTH_TOKEN` is correct
3.  Ensure `FROM_PHONE` is correct

#### Pusher

Create a new account and use the pusher information to fill out the following from `serverless.yaml`.

```yaml
  pusherAppId: 'xxx'
  pusherKey: 'xxx'
  pusherSecret: 'xxx'
  pusherCluster: 'xxx'
```

### Makefile

1.  Ensure `Makefile` is using the right AWS profile and stage/environment (currently dev)

## Deploy

### Dev

```shell
make deploy_dev
```

### Prod

```shell
make deploy_prod
```
