package io.zengin.telegrambot.types

//TODO: Support forward_date 
case class Message(
  id: Int,
  from: Option[User] = None,
  date: Int,
  chat: Either[User, GroupChat],
  forward_from: Option[User] = None,
  replyTo: Option[Message] = None,
  text: Option[String] = None,
  audio: Option[Audio] = None,
  document: Option[Document] = None,
  photo: Option[List[PhotoSize]] = None,
  sticker: Option[Sticker] = None,
  video: Option[Video] = None,
  voice: Option[Voice] = None,
  caption: Option[String] = None,
  contact: Option[Contact] = None,
  location: Option[Location] = None,
  newChatParticipant: Option[User] = None,
  leftChatParticipant: Option[User] = None,
  newChatTitle: Option[String] = None,
  newChatPhoto: Option[List[PhotoSize]] = None,
  deleteChatPhoto: Option[Boolean] = None,
  groupChatCreated: Option[Boolean] = None)
