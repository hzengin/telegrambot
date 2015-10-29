package hzengin.telegrambot

import hzengin.telegrambot.types.{Update, User, Message}
import hzengin.telegrambot.types.Requests.{SendMessageRequest}
import hzengin.telegrambot.webhook._

trait Declerative {
  val updateHandler: UpdateHandler
  val telegramApi: TelegramApi

  private def actionWrapper(action: (Message,User) => Unit) = {
    update: Update =>
      action(update.message, update.message.from.get)
  }

  private def actionWrapper(action: Message => Unit) = {
    update: Update =>
      action(update.message)
  }

  def on(condition: Message => Boolean)(action: Message => Unit) = {
    updateHandler.registerCommand( (u: Update) => condition(u.message),actionWrapper(action))
  }

  // Declerative way to add a simple text command that must be coming from a user
  def on(command: String)(action: (Message, User) => Unit ) = {
    updateHandler.registerCommand((u: Update) => u.message.text match {
      case Some(text) if text == command && !u.message.from.isEmpty => true
      case _ => false
    }, actionWrapper(action))
  }

  def send(to: User)(text: String) = {
    telegramApi.sendMessage(
      SendMessageRequest(Right(to.id), text)
    )
  }

  def reply(message: Message)(text: String) = message.from match {
    case Some(user) => message.chat match {
      case Left(_) => telegramApi.sendMessage(
        SendMessageRequest(
          Right(user.id),
          text,
          replyTo = Some(message.id)
        )
      )
      case Right(groupChat) => telegramApi.sendMessage(
        SendMessageRequest(
          Left(groupChat.id),
          text,
          replyTo = Some(message.id)
        )
      )
    }
    case _ =>
  }
}
