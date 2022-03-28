import zio._
import zio.console._
import zio.clock._
import zio.duration._

object EtcExample extends App {

  def run(args: List[String]): URIO[Any with Console,ExitCode] = {
    val r =  checkAge(10).validate(checkName("Tom")).validate(checkSize(3)).parallelErrors.either

    val runtime = Runtime.default
    val r1 = runtime.unsafeRun(r)

    r1 match {
      case Left(e) => println(e)
      case Right(v) => println(v)
    }

    ZIO.succeed(()).exitCode
  }

  val a =
    for {
      fiber <- (sleep(3.seconds) *>
        putStrLn("Hello, after 3 seconds") *>
        ZIO.succeed(10)).fork
      _   <- putStrLn(s"Hello, world!")
      res <- fiber.join
      _   <- putStrLn(s"Our fiber succeeded with ${res}")
    } yield ()

//  val runtime = Runtime.default
//  runtime.unsafeRun(a)

  val inner =
    putStrLn("inner job is running")
      .delay(1.seconds)
      .forever
      .onInterrupt(putStrLn("inner job interruped").orDie)

  val outer = (
    for {
      f <- inner.forkDaemon
      _ <- putStrLn("outer job is running").delay(1.seconds).forever
      _ <- f.join
    } yield ()
  ).onInterrupt(putStrLn("outer job interruped").orDie)

  val mainApp =
    for {
      fiber <- outer.fork
      _ <- fiber.interrupt.delay(3.seconds)
      _ <- ZIO.never
    } yield ()

  def checkAge(age: Int): IO[String, Int] =
    if (age < 18) ZIO.fail("Age Error")
    else ZIO.succeed(age)

  def checkName(name: String): IO[String, String] =
    if (name.size < 10) ZIO.fail("Name Error")
    else ZIO.succeed(name)

  def checkSize(size: Int): IO[String, Int] =
    if (size < 5 ) ZIO.fail("Size Error")
    else ZIO.succeed(size)


}
