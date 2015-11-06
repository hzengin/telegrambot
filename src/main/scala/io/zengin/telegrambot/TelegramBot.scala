package io.zengin.telegrambot

import akka.actor.{ ActorSystem, Props }
import spray.can.Http
import akka.io.IO

import io.zengin.telegrambot.types.Update
import io.zengin.telegrambot.webhook._

class TelegramBot {
  val token = BotConfig.token
  val updateHandler: UpdateHandler = new UpdateHandler()
  implicit val system = ActorSystem("telegrambot-system")
  val telegramApi:TelegramApi = new TelegramApi(token, system)
}
