# scala-telegram-bot
![Travis CI Build Status](https://magnum.travis-ci.com/hzengin/scala-telegram-bot.svg?token=Eu31bmPEzUsSvufqwvjh&branch=master)

"Batteries Included" Telegram Bot API wrapper for Scala

### Features
- Fully asynchronous
- [spray.io](spray.io) powered.
- Declerative DSL for simple bot features

## Supported Methods
- getMe
- sendMessage
- getUpdates
- forwardMessage
- sendPhoto
- sendAudio
- sendVoice (coming soon)
- sendDocument
- sendSticker
- sendVideo (coming soon)
- sendLocation
- sendChatAction
- getUserProfilePhotos
- getUpdates
- Custom keyboard markups
- Webhook with a SSL reverse proxy

## TODO
 - Built-in Webhook support

## Usage
#### Simple commands + simple answers
```scala
object GreeterBot extends TelegramBot with Polling with Declerative {
  on("/start") { implicit message: Message =>
    reply("Welcome")
  }
}
```
#### Using received message
```scala
object GreeterBot extends TelegramBot with Polling with Declerative {
  on("/start") { implicit message: Message =>
    message.from match {
      case Some(user) => reply("Welcome, " + user.firstName)
      case _ =>
    }
  }
}
```
#### Sending photos
```scala
object GreeterBot extends TelegramBot with Polling with Declerative {
  on("/start") { implicit message: Message =>
    sendPhoto("~/image.jpg")
  }
}
```
