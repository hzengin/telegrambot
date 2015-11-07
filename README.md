# scala-telegram-bot
[![Build Status](https://magnum.travis-ci.com/hzengin/telegrambot.svg?token=Eu31bmPEzUsSvufqwvjh&branch=master)](https://magnum.travis-ci.com/hzengin/telegrambot)
"Batteries Included" Telegram Bot API wrapper for Scala

### Features
- Fully asynchronous
- [spray.io](spray.io) powered.
- Declerative DSL for simple bot features
- Strongly-typed

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

#### When You Need More Power
```scala
object GreeterBot extends TelegramBot with Polling with Declerative {
  when { message =>
    message.photo match {
      case Some(photo) => true
      case None => false
    }
  } perform { implicit message =>
    reply("Nice Smile!")
  }
}
```
#### "Cuckoo Clock?" Why Not?
```scala
object GreeterBot extends TelegramBot with Polling with Declerative {
  every(1 hours) {
    
  }
}
```
