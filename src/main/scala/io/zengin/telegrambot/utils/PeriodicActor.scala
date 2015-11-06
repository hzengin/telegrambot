package io.zengin.telegrambot.utils

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext
import akka.actor._

class PeriodicActor(interval: FiniteDuration, action: => Unit)(implicit val system: ActorSystem) extends Actor {
  import context.dispatcher
  val tick = context.system.scheduler.schedule(0 millis, interval, self, "tick")

  override def postStop() = tick.cancel()

  def receive = {
    case "tick" => action
    case _ =>
  }
}
