package io.zengin.telegrambot.types.requests

import spray.httpx.SprayJsonSupport
import spray.json._
import ReplyMarkups._
import io.zengin.telegrambot.types._


object RequestsJsonSupport extends DefaultJsonProtocol with SprayJsonSupport {

  implicit object replyMarkupFormat extends JsonFormat[ReplyMarkup] {
    def write(r: ReplyMarkup) = r match {
      case ReplyKeyboardMarkup(keyboard, resize, oneTime, selective) => JsObject(
        "keyboard" -> keyboard.toJson,
        "resize_keyboard" -> resize.toJson,
        "one_time_keyboard" -> oneTime.toJson,
        "selective" -> selective.toJson
      )
      case ForceReply(selective) => JsObject(
        "force_reply" -> JsBoolean(true),
        "selective" -> selective.toJson
      )

      case ReplyKeyboardHide(selective) => JsObject(
        "hide_keyboard" -> JsBoolean(true),
        "selective" -> selective.toJson
      )

      case _ => JsObject()
    }

    def read(v: JsValue) = ForceReply() // TODO: implement this unnecessary feature
  }

  implicit object sendPhotoRequestFormat extends JsonFormat[SendPhotoRequest] {
    def write(r: SendPhotoRequest) = {
      JsObject(
        "chat_id" -> r.chatId.toJson,
        "photo" -> r.photo.right.getOrElse("").toJson,
        "caption" -> r.caption.toJson,
        "reply_to_message_id" -> r.replyTo.toJson,
        "reply_markup" -> r.replyMarkup.toJson
      )
    }

    def read(v: JsValue) = SendPhotoRequest(Left(""), Right("")) // TODO: implement this unnecessary feature
  }

  implicit object sendAudioRequestFormat extends JsonFormat[SendAudioRequest] {
    def write(r: SendAudioRequest) = {
      JsObject(
        "chat_id" -> r.chatId.toJson,
        "audio" -> r.audio.right.getOrElse("").toJson,
        "duration" -> r.duration.toJson,
        "performer" -> r.performer.toJson,
        "title" -> r.title.toJson,
        "reply_to_message_id" -> r.replyTo.toJson,
        "reply_markup" -> r.replyMarkup.toJson
      )
    }

    def read(v: JsValue) = SendAudioRequest(Left(""), Right("")) // TODO: implement this unnecessary feature
  }

  implicit object sendDocumentRequestFormat extends JsonFormat[SendDocumentRequest] {
    def write(r: SendDocumentRequest) = {
      JsObject(
        "chat_id" -> r.chatId.toJson,
        "document" -> r.document.right.getOrElse("").toJson,
        "reply_to_message_id" -> r.replyTo.toJson,
        "reply_markup" -> r.replyMarkup.toJson
      )
    }
    def read(v: JsValue) = SendDocumentRequest(Left(""), Right("")) // TODO: implement this unnecessary feature
  }

  implicit object sendStickerRequestFormat extends JsonFormat[SendStickerRequest] {
    def write(r: SendStickerRequest) = {
      JsObject(
        "chat_id" -> r.chatId.toJson,
        "sticker" -> r.sticker.right.getOrElse("").toJson,
        "reply_to_message_id" -> r.replyTo.toJson,
        "reply_markup" -> r.replyMarkup.toJson
      )
    }
    def read(v: JsValue) = SendStickerRequest(Left(""), Right("")) // TODO: implement this unnecessary feature
  }

  implicit val sendMessageRequestFormat= jsonFormat(SendMessageRequest, "chat_id", "text", "parse_mode", "disable_web_page_preview", "reply_to_message_id", "reply_markup")
  implicit val forwardMessageRequestFormat = jsonFormat(ForwardMessageRequest, "chat_id", "from_chat_id", "message_id")
  implicit val sendChatActionRequestFormat = jsonFormat(SendChatActionRequest, "chat_id", "action")
  implicit val sendLocationRequestFormat = jsonFormat(SendLocationRequest, "chat_id", "latitude", "longitude", "reply_to_message_id", "reply_markup")
}

object ReplyMarkups{

  sealed trait ReplyMarkup
  case class ReplyKeyboardMarkup(keyboard: List[List[String]], resize: Option[Boolean], oneTime: Option[Boolean], selective: Option[Boolean]) extends ReplyMarkup

  case class ForceReply(selective: Option[Boolean] = None) extends ReplyMarkup
  case class ReplyKeyboardHide(selective: Option[Boolean] = None) extends ReplyMarkup
}

case class SendMessageRequest(
  chatId: Either[String, Int],
  text: String,
  parseMode: Option[String] = None,
  disablePreview: Option[Boolean] = None,
  replyTo: Option[Int] = None,
  replyMarkup: Option[ReplyMarkup] = None
)

case class ForwardMessageRequest(
  chatId: Either[String, Int],
  fromChatId: Either[String, Int],
  messageId: Int
)

case class SendPhotoRequest(
  chatId: Either[String, Int],
  photo: Either[InputFile, String],
  caption: Option[String] = None,
  replyTo: Option[Int] = None,
  replyMarkup: Option[ReplyMarkup] = None
)

case class SendAudioRequest(
  chatId: Either[String, Int],
  audio: Either[InputFile, String],
  duration: Option[Int] = None,
  performer: Option[String] = None,
  title: Option[String] = None,
  replyTo: Option[Int] = None,
  replyMarkup: Option[ReplyMarkup] = None
)

case class SendDocumentRequest(
  chatId: Either[String, Int],
  document: Either[InputFile, String],
  replyTo: Option[Int] = None,
  replyMarkup: Option[ReplyMarkup] = None
)

case class SendStickerRequest(
  chatId: Either[String, Int],
  sticker: Either[InputFile, String],
  replyTo: Option[Int] = None,
  replyMarkup: Option[ReplyMarkup] = None
)

case class SendChatActionRequest(
  chatId: Either[String, Int],
  action: String
)

case class SendLocationRequest(
  chatId: Either[String, Int],
  latitude: Float,
  longitude: Float,
  replyTo: Option[Int] = None,
  replyMarkup: Option[ReplyMarkup] = None
)
