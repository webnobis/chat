# Chat

Chat is a web based html chat. It contains both server and client.

## Quick Start

The chat server is based on [Vert.x framework](https://vertx.io/).

A Web Browser could be used as chat client.

### Requirements

- Java JRE >= 17
- Web Browser

### Installation

Extract the checked in archive.

The chat server is started by the start script, either ```start.sh``` or ```start.bat```.

### Web Browser

A running chat server is getting by:
```http(s)://[server]:[port]/register|chat```

Example:
http://localhost:8080/register

#### Chat Flow

1. Register a nickname (user): ```/register```
2. Call the chat page, it shows all messages: ```/chat```
3. Write a message
4. Submits the written message: ```[Send]```
5. After sent, the chat page is showing

