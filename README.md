# Self-Destructing Message Service

## Intro

A micro-service providing an API to post messages which expire after a configurable amount of time.

## API Example

\# Post a message which expires after 60 seconds

**Request**: POST /message?message=This%20is%20a%20message&expires=60

**Response**: {"messageId":"abcdefg123536"}

\# Get the message

**Request**: GET /message/abcdefg123536?json=true

**Response**: {"message":"This is a message", "expired":false}
