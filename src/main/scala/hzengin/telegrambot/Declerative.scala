package hzengin.telegrambot

import hzengin.telegrambot.types.{Update, User, Message, InputFile}
import hzengin.telegrambot.types.requests.{SendMessageRequest, SendPhotoRequest}
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

  def sendPhoto(path: String, caption: Option[String])(implicit message: Message) = {
    telegramApi.sendPhoto(
      message.chat match {
        case Left(user) => SendPhotoRequest(Right(user.id), Left(InputFile(path)), caption)
        case Right(groupChat) =>SendPhotoRequest(Left(groupChat.id), Left(InputFile(path)), caption)
      }
    )
  }

  def replyWithPhoto(path: String, caption: Option[String])(implicit message: Message) = {
    telegramApi.sendPhoto(
      message.chat match {
        case Left(user) => SendPhotoRequest(Right(user.id), Left(InputFile(path)), caption, Some(message.id))
        case Right(groupChat) =>SendPhotoRequest(Left(groupChat.id), Left(InputFile(path)), caption, Some(message.id))
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
