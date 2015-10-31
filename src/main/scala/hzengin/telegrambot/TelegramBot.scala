package hzengin.telegrambot

import akka.actor.{ ActorSystem, Props }
import spray.can.Http
import akka.io.IO

import hzengin.telegrambot.types.Update
import hzengin.telegrambot.webhook._

class TelegramBot {
  val token: String = BotConfig.token
  val updateHandler: UpdateHandler = new UpdateHandler()
  implicit val system = ActorSystem("telegrambot-system")
  val telegramApi:TelegramApi = new TelegramApi(token, system)
}
