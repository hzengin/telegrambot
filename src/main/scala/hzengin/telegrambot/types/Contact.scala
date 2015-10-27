package hzengin.telegrambot.types

case class Contact(phoneNumber: String, firstName: String, lastName: Option[String], userId: Option[Int])