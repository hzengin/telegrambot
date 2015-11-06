package io.zengin.telegrambot.types

case class Audio(id: String, duration: Int, performer: Option[String], title: Option[String], mimeType: Option[String], fileSize: Option[Int])