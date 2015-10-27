package hzengin.telegrambot.types

case class Result[T](status: Boolean, result: T)
