package example

import com.amazonaws.services.lambda.runtime.LambdaLogger

import java.nio.charset.StandardCharsets

class Logger(val requestId: String) extends LambdaLogger {
  override def log(message: String): Unit = print(message)

  override def log(message: Array[Byte]): Unit = print(new String(message, StandardCharsets.UTF_8))

  private def print(message: String): Unit = {
    println(s"""{"requestId": "$requestId", "message": "$message"}""")
  }
}
