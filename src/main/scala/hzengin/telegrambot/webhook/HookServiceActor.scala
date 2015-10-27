package hzengin.telegrambot.webhook

import spray.routing._
import spray.http._
import akka.actor._
import hzengin.telegrambot._
import hzengin.telegrambot.types._
import hzengin.telegrambot.types.TypesJsonSupport._

trait HookService extends HttpService {
  def updateHandler: UpdateHandler
  def hookUrl: String
  val hookRoute = path(hookUrl) {
    post {
      entity(as[Update]) { update =>
        updateHandler.handle(update)
        complete(StatusCodes.OK)
      }
    }
  }
}

class HookServiceActor(val hookUrl: String, val updateHandler: UpdateHandler) extends Actor with HookService {
  def actorRefFactory = context
  def receive = runRoute(hookRoute)
}
