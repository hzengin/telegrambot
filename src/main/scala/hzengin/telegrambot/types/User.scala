package hzengin.telegrambot.types

case class User(
  id: Long,
  firstName: String,
  lastName: Option[String],
  username: Option[String])
