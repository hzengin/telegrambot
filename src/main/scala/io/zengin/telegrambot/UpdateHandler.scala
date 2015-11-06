package io.zengin.telegrambot

import scala.collection.mutable
import io.zengin.telegrambot.types.Update

class UpdateHandler() {
  private val commands = mutable.MutableList[(Update => Boolean, Update => Unit)]()

  def registerCommand(condition: Update => Boolean, action: Update => Unit) = {
    commands += ((condition, action))
  }

  def handle(update: Update) = {
    commands.find(c => c._1(update)).map { c=>
      c._2(update)
    }
  }
}
