package hzengin.telegrambot

import hzengin.telegrambot.types.Update
import hzengin.telegrambot.webhook._
import akka.actor.{ ActorSystem, Props }
import spray.can.Http
import akka.io.IO

import java.security.{SecureRandom, KeyStore}
import javax.net.ssl.{KeyManagerFactory, SSLContext, TrustManagerFactory}
import spray.io._

trait SecureWebhook {
  implicit def sslContext: SSLContext = {
    val keyStoreResource = BotConfig.WebhookConfig.keystore
    val password = BotConfig.WebhookConfig.keystorePassword
    val keyStore = KeyStore.getInstance("jks")
    keyStore.load(getClass.getResourceAsStream(keyStoreResource), password.toCharArray)
    val keyManagerFactory = KeyManagerFactory.getInstance("SunX509")
    keyManagerFactory.init(keyStore, password.toCharArray)
    val trustManagerFactory = TrustManagerFactory.getInstance("SunX509")
    trustManagerFactory.init(keyStore)
    val context = SSLContext.getInstance("TLS")
    context.init(keyManagerFactory.getKeyManagers, trustManagerFactory.getTrustManagers, new SecureRandom)
    context
  }

  implicit def sslEngineProvider: ServerSSLEngineProvider = {
    ServerSSLEngineProvider { engine =>
      engine.setEnabledCipherSuites(Array("TLS_RSA_WITH_AES_256_CBC_SHA"))
      engine.setEnabledProtocols(Array("SSLv3", "TLSv1"))
      engine
    }
  }

  def updateHandler: UpdateHandler
  implicit val system: ActorSystem
  val token: String
  val serviceActor = system.actorOf(Props(new HookServiceActor(token, updateHandler)))

  def run() = {
    IO(Http) ! Http.Bind(serviceActor, interface = BotConfig.WebhookConfig.interface, port = BotConfig.WebhookConfig.port)
  }

}


trait Webhook {
  def updateHandler: UpdateHandler
  implicit val system: ActorSystem
  val token: String
  val serviceActor = system.actorOf(Props(new HookServiceActor(token, updateHandler)))

  def run() = {
    IO(Http) ! Http.Bind(serviceActor, interface = BotConfig.WebhookConfig.interface, port = BotConfig.WebhookConfig.port)
  }
}
