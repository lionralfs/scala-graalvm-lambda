package example;

object Handler {
  println("setting up env")

  def main(args: Array[String]): Unit = {
    println("main")
    println(args.mkString("\n"))
  }
}