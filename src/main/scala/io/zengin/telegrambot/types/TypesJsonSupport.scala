package io.zengin.telegrambot.types

import spray.httpx.SprayJsonSupport
import spray.json._

object TypesJsonSupport extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val fileFormat = jsonFormat(File, "file_id", "file_size", "file_path")
  implicit val photoSizeFormat = jsonFormat(PhotoSize, "file_id", "width", "height", "file_size")
  implicit val userFormat = jsonFormat(User, "id", "first_name", "last_name", "username")
  implicit val audioFormat = jsonFormat(Audio, "file_id", "duration", "performer", "title", "mime_type", "file_size")
  implicit val documentFormat = jsonFormat(Document, "file_id", "thumb", "file_name", "mime_type", "file_size")
  implicit val stickerFormat = jsonFormat(Sticker, "file_id", "width", "height", "thumb", "file_size")
  implicit val videoFormat = jsonFormat(Video, "file_id", "width", "height", "duration", "thumb", "mime_type", "file_size")
  implicit val voiceFormat = jsonFormat(Voice, "file_id", "duration", "mime_type", "file_size")
  implicit val contactFormat = jsonFormat(Contact, "phone_number", "first_name", "last_name", "user_id")
  implicit val locationFormat = jsonFormat(Location, "longitude", "latitude")
  implicit val userProfilePhotosFormat = jsonFormat(UserProfilePhotos, "total_count", "photos")
  implicit val groupChatFormat = jsonFormat(GroupChat, "id", "title")
  implicit val messageFormat: JsonFormat[Message] = lazyFormat(
    jsonFormat(Message,
      "message_id",
      "from",
      "date",
      "chat",
      "forward_from",
      "reply_to_message",
      "text",
      "audio",
      "document",
      "photo",
      "sticker",
      "video",
      "voice",
      "caption",
      "contact",
      "location",
      "new_chat_participant",
      "left_chat_participant",
      "new_chat_title",
      "new_chat_photo",
      "delete_chat_photo",
      "group_chat_created"))
  implicit val updateFormat = jsonFormat(Update, "update_id", "message")
  implicit val failResultFormat = jsonFormat(FailResult, "ok", "error_code", "description")
  implicit def resultFormat[T: JsonFormat] = jsonFormat(Result.apply[T], "ok", "result")
}
