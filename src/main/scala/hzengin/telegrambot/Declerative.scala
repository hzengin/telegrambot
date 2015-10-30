package hzengin.telegrambot

import hzengin.telegrambot.types.{Update, User, Message}
import hzengin.telegrambot.types.Requests.{SendMessageRequest}
import hzengin.telegrambot.webhook._

trait Declerative {
  val updateHandler: UpdateHandler
  val telegramApi: TelegramApi

  private def actionWrapper(action: Message => Unit) = { update: Update =>
    action(update.message)
  }

  private def conditionWrapper(condition: Message => Boolean) = { update: Update =>
    condition(update.message)
  }

  class Condition(val condition: Message => Boolean) {
    def perform(action: Message => Unit) = {
      updateHandler.registerCommand(conditionWrapper(condition), actionWrapper(action))
    }
  }

  def when(condition: Message => Boolean) = new Condition(condition)

  def on(command: String)(action: (Message) => Unit) = {
    updateHandler.registerCommand(
      conditionWrapper(
        (message: Message) => {
          message.text match {
            case Some(text) if text == command => true
            case _ => false
          }
        }
      ),
      actionWrapper(action)
    )
  }

  def send(text: String)(implicit message: Message) = {
    telegramApi.sendMessage(
      message.chat match {
        case Left(user) => SendMessageRequest(Right(user.id), text)
        case Right(groupChat) =>SendMessageRequest(Left(groupChat.id), text)
      }
    )
  }

  def reply(text: String)(implicit message: Message) = {
    telegramApi.sendMessage(
      message.chat match {
        case Left(user) => SendMessageRequest(Right(user.id), text, replyTo = Some(message.id))
        case Right(groupChat) => SendMessageRequest(Left(groupChat.id), text, replyTo = Some(message.id))
      }
    )
  }

}
