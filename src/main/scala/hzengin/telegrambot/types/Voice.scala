package hzengin.telegrambot.types

case class Voice(id: String, duration: Int, mimeType: Option[String], fileSize: Option[Int])