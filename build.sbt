name := """scala-telegram-bot"""

organization := "hzengin.telegrambot"

licenses += ("MIT", url("http://opensource.org/licenses/MIT"))

scalaVersion := "2.11.7"

resolvers ++= Seq(
  "spray repo" at "http://repo.spray.io/")

libraryDependencies ++={
  val sprayVersion = "1.3.3"
  val sprayJsonVersion = "1.3.2"
  val akkaVersion = "2.4.0"
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
    "io.spray" %% "spray-can" % sprayVersion,
    "io.spray" %% "spray-routing" % sprayVersion,
    "io.spray" %% "spray-json" % sprayJsonVersion,
    "io.spray" %% "spray-client" % sprayJsonVersion,
    "org.json4s" %% "json4s-native" % "3.3.0",
    "com.github.nscala-time" %% "nscala-time" % "2.4.0",
    "org.scalatest" %% "scalatest" % "2.2.4" % "test"
  )
}

scalacOptions := Seq(
  "-unchecked",
  "-deprecation",
  "-encoding", "utf8",
  "-feature",
  "-language:higherKinds",
  "-language:postfixOps",
  "-language:implicitConversions",
  "-language:existentials")

fork in run := true
