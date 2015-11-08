package io.zengin.telegrambot.types

import java.io.{FileInputStream, InputStream, File => JFile}

trait InputFile {
  val name: String
  val mimeType: String = "application/octet-stream"
  val bytes: Array[Byte]
}

object InputFile {

  def apply(filePath: String): InputFile = apply(new JFile(filePath))

  def apply(file: JFile): InputFile = {
    apply(file.getName, new FileInputStream(file))
  }

  def apply(fileName: String, inputStream: InputStream): InputFile = new InputFile {
    val name = fileName
    val bytes = Iterator.continually(inputStream.read()) takeWhile (-1 !=) map (_.toByte) toArray
  }
}
