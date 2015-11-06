package io.zengin.telegrambot.types

case class Video(id: String, width: Int, height: Int, duration: Int, thumb: Option[PhotoSize], mimeType: Option[String], fileSize: Option[Int])