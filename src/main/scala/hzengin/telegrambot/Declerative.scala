package hzengin.telegrambot

import hzengin.telegrambot.types.Update
import hzengin.telegrambot.webhook._

trait Declerative {
  val updateHandler: UpdateHandler
  // Declerative way to add a simple text command
  def on(command: String)(action: Update => Unit) {
    updateHandler.registerCommand((u: Update) => u.message.text.getOrElse("") == command, action)
  }

}
