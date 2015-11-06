package io.zengin.telegrambot

import io.zengin.telegrambot.types.{Update, User, Message, InputFile}
import io.zengin.telegrambot.types.requests.{SendMessageRequest, SendPhotoRequest}
import io.zengin.telegrambot.webhook._
import io.zengin.telegrambot.utils._

import akka.actor.{ActorSystem, Props}
import scala.concurrent.duration.FiniteDuration

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

  def sendTo(text: String, userId: Int) = {
    telegramApi.sendMessage(
      SendMessageRequest(Right(userId), text)
    )
  }

  def sendTo(text: String, groupChat: String) = {
    telegramApi.sendMessage(
      SendMessageRequest(Left(groupChat), text)
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

  def every(interval: FiniteDuration)(action: => Unit)(implicit system: ActorSystem) = {
    system.actorOf(Props(new PeriodicActor(interval, action)))
  }

}
