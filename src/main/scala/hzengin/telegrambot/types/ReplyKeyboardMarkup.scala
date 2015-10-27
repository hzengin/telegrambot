package hzengin.telegrambot.types

case class ReplyKeyboardMarkup(keyboard: List[List[String]], resize: Option[Boolean], oneTime: Option[Boolean], selective: Option[Boolean])