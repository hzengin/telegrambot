package hzengin.telegrambot.types

case class User(
  id: Int,
  firstName: String,
  lastName: Option[String],
  username: Option[String])
