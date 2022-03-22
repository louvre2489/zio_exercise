import zio._

object BasicOperation {
  val succeeded: UIO[Int] = IO.succeed(21).map(_ * 2)

  val failed: IO[Exception, Nothing] =
    IO.fail("Error").mapError(msg => new Exception(msg))

  // comment
  val sequenced =
    CreatingEffect.getStrLn.flatMap { input =>
      CreatingEffect.putStrLn(s"You entered: ${input}")
    }

  val zipped: UIO[(String, Int)] =
    ZIO.succeed("42").zip(ZIO.succeed(2))

  val zipRight1: ZIO[Any, Throwable, String] =
    CreatingEffect
      .putStrLn("What is your name?")
      .zipRight(CreatingEffect.getStrLn)

  val zipRgith2: ZIO[Any, Throwable, String] =
    CreatingEffect
      .putStrLn("What is your name?") *> CreatingEffect.getStrLn
}
