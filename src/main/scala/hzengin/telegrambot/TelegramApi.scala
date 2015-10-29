package hzengin.telegrambot

import hzengin.telegrambot.types._
import hzengin.telegrambot.types.TypesJsonSupport._
import hzengin.telegrambot.types.Requests._

import scala.util.{ Success, Failure }
import scala.concurrent.Future
import akka.actor.ActorSystem
import spray.client.pipelining._
import scala.concurrent.ExecutionContext.Implicits.global
import spray.http.{MediaTypes, BodyPart, MultipartFormData, ContentTypes, FormData, HttpHeaders, FormFile, HttpData}
import spray.http.HttpEntity._
import spray.http.HttpEntity

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

  def sendMessage(request: SendMessageRequest): Future[Option[Message]] = {
    import hzengin.telegrambot.types.Requests.JsonSupport._
    val pipeline = sendReceive ~> unmarshal[Result[Message]]
    pipeline (Post(apiUrl + "sendMessage", request)) map {
      case Result(true, message) => Some(message)
    } recover {
      case e => None
    }
  }

  def forwardMessage(request: ForwardMessageRequest): Future[Option[Message]] = {
    import hzengin.telegrambot.types.Requests.JsonSupport._
    val pipeline = sendReceive ~> unmarshal[Result[Message]]
    pipeline (Post(apiUrl + "forwardMessage", request)) map {
      case Result(true, message) => Some(message)
    } recover {
      case e => None
    }
  }

  def sendPhoto(request: SendPhotoRequest): Future[Option[Message]] = {
    import hzengin.telegrambot.types.Requests.JsonSupport._

    val pipeline = sendReceive ~> unmarshal[Result[Message]]
    request match {
      case SendPhotoRequest(chatId, Left(photo), caption, replyTo, _) => // we need to upload file
        val httpData = HttpData(photo.bytes)
        val httpEntitiy = HttpEntity(MediaTypes.`multipart/form-data`, httpData).asInstanceOf[HttpEntity.NonEmpty]
        val fileBodyPart = BodyPart(FormFile(photo.name, httpEntitiy), "photo")
        var formData = Seq(
          fileBodyPart,
          chatId match {
            case Right(chatId) => BodyPart(chatId.toString, Seq(HttpHeaders.`Content-Disposition`("form-data", Map("name" -> "chat_id")) ))
            case Left(chatId) =>  BodyPart(chatId, Seq(HttpHeaders.`Content-Disposition`("form-data", Map("name" -> "chat_id")) ))
          }
        )
        caption match {
          case Some(caption) => formData = formData ++ Seq(BodyPart(caption, Seq(HttpHeaders.`Content-Disposition`("form-data", Map("name" -> "caption")) )))
          case _ =>
        }
        replyTo match {
          case Some(replyTo) => formData = formData ++ Seq(BodyPart(replyTo.toString, Seq(HttpHeaders.`Content-Disposition`("form-data", Map("name" -> "reply_to_message_id")) )))
          case _ =>
        }
        pipeline(Post(apiUrl + "sendPhoto", MultipartFormData(formData))) map {
          case Result(true, message) => Some(message)
        } recover {
          case e => println(e); None
        }

      case SendPhotoRequest(_, Right(fileId), _, _, _) => // file must be already saved in telegram servers
        import hzengin.telegrambot.types.Requests.JsonSupport.sendPhotoRequestFormat
        pipeline(Post(apiUrl + "sendPhoto", sendPhotoRequestFormat.write(request))) map {
          case Result(true, message) => Some(message)
        } recover {
          case e => None
        }
      }
  }

}
