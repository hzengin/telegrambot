package io.zengin.telegrambot

import com.typesafe.config._

object BotConfig {
  private val config = ConfigFactory.load()
  private val root = config.getConfig("telegram-bot")

  val token = root.getString("token")

  object WebhookConfig {
    private val webhookConfig = config.getConfig("telegram-bot.webhook")
    val interface = webhookConfig.getString("interface")
    val port = webhookConfig.getInt("port")
    val keystore = webhookConfig.getString("keystore")
    val keystorePassword = webhookConfig.getString("keystorePassword")
  }

  object PollingConfig {
    private val pollingConfig = config.getConfig("telegram-bot.polling")
    val interval = pollingConfig.getInt("interval")
  }
}
