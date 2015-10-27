package hzengin.telegrambot

import hzengin.telegrambot.types._
import hzengin.telegrambot.types.TypesJsonSupport._

import scala.util.{ Success, Failure }
import scala.concurrent.Future
import akka.actor.ActorSystem
import spray.client.pipelining._
import scala.concurrent.ExecutionContext.Implicits.global

class TelegramApi(token: String, implicit val system: ActorSystem) {
  private val apiUrl = s"https://api.telegram.org/bot$token/"

  def getMe(): Future[Option[User]] = {
    val pipeline = sendReceive ~> unmarshal[Result[User]]
    pipeline (Get(apiUrl + "getMe")) map {
      case Result(true, result: User) => Some(result)
      case Result(false, _) => None
      case _ => None
    } recover {
      case e => None
    }
  }

  def getUpdates(offset: Option[Int] = None): Future[Option[List[Update]]] = {
    val pipeline = sendReceive  ~> unmarshal[Result[List[Update]]]
    pipeline (Get(apiUrl + "getUpdates?offset=" + offset.getOrElse(0))) map {
      case Result(true, result) => Some(result)
    } recover {
      case e => None
    }
  }


}
