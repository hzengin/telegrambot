package io.zengin.telegrambot

import io.zengin.telegrambot.types.requests._
import io.zengin.telegrambot.types._

import io.zengin.telegrambot.types.requests.RequestsJsonSupport._

import scala.util.{ Success, Failure }
import scala.concurrent.Future
import akka.actor.ActorSystem
import spray.client.pipelining._
import scala.concurrent.ExecutionContext.Implicits.global
import spray.http.{MediaTypes, BodyPart, MultipartFormData, ContentTypes, FormData, HttpHeaders, FormFile, HttpData}
import spray.http.HttpEntity._
import spray.httpx.UnsuccessfulResponseException
import spray.httpx.unmarshalling._
import spray.http._

class TelegramApi(token: String, implicit val system: ActorSystem) {

  case class MarshallingException(message: String) extends Exception

  import io.zengin.telegrambot.types.TypesJsonSupport._

  private val apiUrl = s"https://api.telegram.org/bot$token/"
  private val fileUrl = s"https://api.telegram.org/file/bot$token/"

  private def buildFileBodyPart(key: String, file: InputFile) = {
    val httpData = HttpData(file.bytes)
    val httpEntitiy = HttpEntity(MediaTypes.`multipart/form-data`, httpData).asInstanceOf[HttpEntity.NonEmpty]
    BodyPart(FormFile(file.name, httpEntitiy), key)
  }

  private def buildParameterBodyPart(key: String, value: String) = {
    BodyPart(value, Seq(HttpHeaders.`Content-Disposition`("form-data", Map("name" -> key)) ))
  }

  private def failureAwareUnmarshal[E: FromResponseUnmarshaller, R: FromResponseUnmarshaller]: HttpResponse => Either[E, R] = { response =>
    response.status match {
      case spray.http.StatusCodes.Success(_) => response.as[R] match {
        case Right(value) => Right(value)
        case Left(error) => throw new MarshallingException(error.toString)
        case error => throw new MarshallingException(error.toString)
      }

      case spray.http.StatusCodes.ClientError(_) => response.as[E] match {
        case Right(value) => Left(value)
        case Left(error) => throw new MarshallingException(error.toString)
        case error => throw new MarshallingException(error.toString)
      }

      case error => throw new MarshallingException(error.toString)
    }
  }

  def getMe(): Future[Either[FailResult, User]] = {
    val pipeline = sendReceive ~> failureAwareUnmarshal[FailResult, Result[User]]
    pipeline (Get(apiUrl + "getMe")) map {
      case Right(Result(true, user)) => Right(user)
      case Left(failResult) => Left(failResult)
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

  def sendMessage(request: SendMessageRequest): Future[Either[FailResult, Message]] = {
    val pipeline = sendReceive ~> failureAwareUnmarshal[FailResult, Result[Message]]
    pipeline (Post(apiUrl + "sendMessage", request)) map {
      case Right(Result(true, message)) => Right(message)
      case Left(failResult) => Left(failResult)
    }
  }

  def sendChatAction(request: SendChatActionRequest): Future[Either[FailResult, Boolean]] = {
    val pipeline = sendReceive ~> failureAwareUnmarshal[FailResult, Result[Boolean]]
    pipeline (Post(apiUrl + "sendChatAction", request)) map {
      case Right(Result(true, true)) => Right(true) // yes this is ugly
      case Left(failResult) => Left(failResult)
    }
  }

  def sendLocation(request: SendLocationRequest): Future[Either[FailResult, Message]] = {
    val pipeline = sendReceive ~> failureAwareUnmarshal[FailResult, Result[Message]]
    pipeline (Post(apiUrl + "sendLocation", request)) map {
      case Right(Result(true, message)) => Right(message)
      case Left(failResult) => Left(failResult)
    }
  }


  def getFile(id: String): Future[Either[FailResult, File]] = {
    val pipeline = sendReceive  ~> failureAwareUnmarshal[FailResult, Result[File]]
    pipeline(Get(apiUrl + "getFile?file_id=" + id)) map {
      case Right(Result(true, file)) => Right(file)
      case Left(failResult) => Left(failResult)
    }
  }

  def getUserProfilePhotos(userId: Int): Future[Either[FailResult, UserProfilePhotos]] = {
    val pipeline = sendReceive  ~> failureAwareUnmarshal[FailResult, Result[UserProfilePhotos]]
    pipeline(Get(apiUrl + s"getUserProfilePhotos?user_id=$userId")) map {
      case Right(Result(true, userProfilePhotos)) => Right(userProfilePhotos)
      case Left(failResult) => Left(failResult)
    }
  }

  def forwardMessage(request: ForwardMessageRequest): Future[Either[FailResult, Message]] = {
    val pipeline = sendReceive ~> failureAwareUnmarshal[FailResult, Result[Message]]
    pipeline (Post(apiUrl + "forwardMessage", request)) map {
      case Right(Result(true, message)) => Right(message)
      case Left(failResult) => Left(failResult)
    }
  }

  def sendAudio(request: SendAudioRequest): Future[Option[Message]] = {
    val pipeline = sendReceive ~> unmarshal[Result[Message]]
    request match {
      case SendAudioRequest(chatId, Left(audio), duration, performer, title, replyTo, _) =>
        val fileBodyPart = buildFileBodyPart("audio", audio)
        var formData = Seq(fileBodyPart)
        formData = formData ++ Seq(chatId match {
          case Right(chatId) => buildParameterBodyPart("chat_id", chatId.toString)
          case Left(chatId) => buildParameterBodyPart("chat_id", chatId)
        })

        performer match {
          case Some(performer) => formData = formData ++ Seq(buildParameterBodyPart("performer", performer))
          case None =>
        }

        title match {
          case Some(title) => formData = formData ++ Seq(buildParameterBodyPart("title", title))
          case None =>
        }

        replyTo match {
          case Some(replyTo) => formData = formData ++ Seq(buildParameterBodyPart("reply_to_message_id", replyTo.toString))
          case None =>
        }

        pipeline(Post(apiUrl + "sendAudio", MultipartFormData(formData))) map {
          case Result(true, message) => Some(message)
        } recover {
          case e => None
        }

      case SendAudioRequest(chatId, Right(fileId), _, _, _, _, _) =>
        import io.zengin.telegrambot.types.requests.RequestsJsonSupport.sendAudioRequestFormat
        pipeline(Post(apiUrl + "sendAudio", sendAudioRequestFormat.write(request))) map {
          case Result(true, message) => Some(message)
        } recover {
          case e => None
        }

      case _ => Future { None }
    }
  }

  def sendPhoto(request: SendPhotoRequest): Future[Option[Message]] = {
    val pipeline = sendReceive ~> unmarshal[Result[Message]]
    request match {
      case SendPhotoRequest(chatId, Left(photo), caption, replyTo, _) => // we need to upload file
        val fileBodyPart = buildFileBodyPart("photo", photo)
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
          case e => None
        }

      case SendPhotoRequest(_, Right(fileId), _, _, _) => // file must be already saved in telegram servers
        pipeline(Post(apiUrl + "sendPhoto", sendPhotoRequestFormat.write(request))) map {
          case Result(true, message) => Some(message)
        } recover {
          case e => None
        }

      case _ => Future { None }
      }
  }

  def sendDocument(request: SendDocumentRequest): Future[Option[Message]] = {
    val pipeline = sendReceive ~> unmarshal[Result[Message]]
    request match {
      case SendDocumentRequest(chatId, Left(document), replyTo, _) =>
        val fileBodyPart = buildFileBodyPart("document", document)
        var formData = Seq(fileBodyPart)
        formData = formData ++ Seq(chatId match {
          case Right(chatId) => buildParameterBodyPart("chat_id", chatId.toString)
          case Left(chatId) => buildParameterBodyPart("chat_id", chatId)
        })

        replyTo match {
          case Some(replyTo) => formData = formData ++ Seq(buildParameterBodyPart("reply_to_message_id", replyTo.toString))
          case None =>
        }

        pipeline(Post(apiUrl + "sendDocument", MultipartFormData(formData))) map {
          case Result(true, message) => Some(message)
        } recover {
          case e => None
        }

      case SendDocumentRequest(_, Right(fileId), _, _) =>
        pipeline(Post(apiUrl + "sendDocument", sendDocumentRequestFormat.write(request))) map {
          case Result(true, message) => Some(message)
        } recover {
          case e => None
        }
    }
  }

  def sendSticker(request: SendStickerRequest): Future[Option[Message]] = {
    val pipeline = sendReceive ~> unmarshal[Result[Message]]
    request match {
      case SendStickerRequest(chatId, Left(sticker), replyTo, _) =>
        val fileBodyPart = buildFileBodyPart("sticker", sticker)
        var formData = Seq(fileBodyPart)
        formData = formData ++ Seq(chatId match {
          case Right(chatId) => buildParameterBodyPart("chat_id", chatId.toString)
          case Left(chatId) => buildParameterBodyPart("chat_id", chatId)
        })

        replyTo match {
          case Some(replyTo) => formData = formData ++ Seq(buildParameterBodyPart("reply_to_message_id", replyTo.toString))
          case None =>
        }

        pipeline(Post(apiUrl + "sendSticker", MultipartFormData(formData))) map {
          case Result(true, message) => Some(message)
        } recover {
          case e => None
        }

      case SendStickerRequest(_, Right(fileId), _, _) =>
        pipeline(Post(apiUrl + "sendSticker", sendStickerRequestFormat.write(request))) map {
          case Result(true, message) => Some(message)
        } recover {
          case e => None
        }
    }
  }

}
