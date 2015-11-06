package io.zengin.telegrambot.webhook

import spray.routing._
import spray.http._
import akka.actor._
import io.zengin.telegrambot._
import io.zengin.telegrambot.types._
import io.zengin.telegrambot.types.TypesJsonSupport._

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
