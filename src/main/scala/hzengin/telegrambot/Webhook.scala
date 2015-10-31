package hzengin.telegrambot
import akka.actor.{ ActorSystem, Props }
import spray.can.Http
import akka.io.IO

import hzengin.telegrambot.types.Update
import hzengin.telegrambot.webhook._

trait Webhook {
  def updateHandler: UpdateHandler
  implicit val system: ActorSystem
  val token: String
  val serviceActor = system.actorOf(Props(new HookServiceActor(token, updateHandler)))

  def run(interface: String = "0.0.0.0", port: Int = 8080) = {
    IO(Http) ! Http.Bind(serviceActor, interface = interface, port = port)
  }
}
