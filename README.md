# telegrambot
[![Build Status](https://travis-ci.org/hzengin/telegrambot.svg)](https://travis-ci.org/hzengin/telegrambot) [![License](https://img.shields.io/badge/license-MIT-blue.svg)](https://opensource.org/licenses/MIT)

"Batteries Included" Telegram Bot API wrapper for Scala

## Features
- Fully asynchronous
- [spray.io](spray.io) powered.
- Declarative DSL for simple bot features
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
 - Better error handling
 - Documentation

## Configuration
Check [reference.conf](https://github.com/hzengin/telegrambot/blob/master/src/main/resources/reference.conf) for configuration.

## Installation
There is no release yet; project still lack too much improvement. Wait for updates.

## Usage
#### Simple commands + simple answers
```scala
object GreeterBot extends TelegramBot with Polling with Declarative {
  on("/start") { implicit message: Message =>
    reply("Welcome")
  }
}
```
#### Using received message
```scala
object GreeterBot extends TelegramBot with Polling with Declarative {
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
object GreeterBot extends TelegramBot with Polling with Declarative {
  on("/start") { implicit message: Message =>
    sendPhoto("~/image.jpg")
  }
}
```

#### When You Need More Power
```scala
object GreeterBot extends TelegramBot with Polling with Declarative {
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
object GreeterBot extends TelegramBot with Polling with Declarative {
  every(1 hours) {
    sendTo("Ping!", 31415926535)
  }
}
```

#### Sending Messages Not Always Successful
```scala
object TestBot extends TelegramBot with Polling with Declarative {
  sendTo("test", 1093654812) map {
    case Right(message) =>
      println(message);
    case Left(error) if error.code == 403 =>
      println("No, we are not allowed to send messages to this chat");
    case Left(error) if error.code == 400 =>
      println("No, chat doesn't exists");
  }
}
```

#### Keep user informed!
```scala
on("Welcome") { implicit message =>
  typing; reply("typed.")
  }
}
```
