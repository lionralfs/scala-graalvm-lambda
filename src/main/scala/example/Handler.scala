package example

import java.time.Instant;

class Handler {
  println(Instant.now(), "setting up env")

  def handle(request: String): String = {
    println(Instant.now(), "handle")
    println(Instant.now(), request)
    "It worked"
  }
}