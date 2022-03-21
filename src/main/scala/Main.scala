import zio._
import zio.console._

object Main extends zio.App {
  def run(args: List[String]) = myAppLogic.exitCode

  val myAppLogic =
    for {
      _    <- putStrLn("Hello!What is your name?")
      name <- getStrLn
      _    <- putStrLn(s"Hello, ${name}, welcome to ZIO!")
    } yield ()
}

object IntegrationExample {
  val runtime = Runtime.default

  runtime.unsafeRun(Task(println("Hello, World!")))
}
