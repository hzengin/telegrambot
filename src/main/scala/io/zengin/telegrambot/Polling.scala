package io.zengin.telegrambot

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext
import akka.actor._
import io.zengin.telegrambot.types._
import scala.language.postfixOps


class PollingScheduler(val updateHandler: UpdateHandler, val telegramApi: TelegramApi)(implicit val system: ActorSystem) extends Actor {
  case class Tick()
  import context.dispatcher
  def interval = BotConfig.PollingConfig.interval seconds
  private var updateOffset = 0

  val tick = context.system.scheduler.schedule(0 millis, interval, self, Tick())

  def receive = {
    case Tick() => telegramApi.getUpdates(Some(updateOffset)) map {
      case Some(updates) => updates foreach { update =>
        updateOffset = updateOffset max (update.id + 1)
        updateHandler handle update
      }
      case None =>
    }
  }
  override def postStop() = tick.cancel()
}

trait Polling {
  implicit val system: ActorSystem
  val updateHandler: UpdateHandler
  val telegramApi: TelegramApi

  def run() = {
    system.actorOf(Props(new PollingScheduler(updateHandler, telegramApi)), "scheduleactor")
  }
}
