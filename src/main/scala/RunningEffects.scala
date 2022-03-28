import zio._
import zio.console._
import zio.internal.Platform

object RunningEffects{// extends zio.App {

//  override def run(args: List[String]): URIO[ZEnv,ExitCode] =
//    myAppLogic.exitCode

  def myAppLogic =
    for {
      _ <- putStrLn("Hello! What is youe name?")
      name <- getStrLn
      _ <- putStrLn(s"Hello, ${name}, welcome to ZIO!")
    } yield ()

  val runtime = Runtime.default
  runtime.unsafeRun(ZIO(println("Hello, world")))

  val myRuntime: Runtime[Int] = Runtime(42, Platform.default)
  myRuntime.unsafeRun(square)

  val square: URIO[Int, Int] =
    for {
      env <- ZIO.environment[Int]
    } yield env * env
}
