import zio._

object BasicConcurrency {

  def fib(n: Long): UIO[Long] = UIO {
    if (n <= 1) UIO.succeed(n)
    else fib(n - 1).zipWith(fib(n - 2))(_ + _)
  }.flatten

  val fib100Fiber: UIO[Fiber[Nothing, Long]] =
    for {
      fiber <- fib(100).fork
    } yield fiber

  val fib100FiberJoin: ZIO[Any, Nothing, String] =
    for {
      fiber   <- IO.succeed("Hi!!").fork
      message <- fiber.join
    } yield message

  val interrupt =
    for {
      fiber <- IO.succeed(42).forever.fork
      exit  <- fiber.interrupt
    } yield exit

  val composeFiber =
    for {
      fiber1 <- IO.succeed("Hi!!").fork
      fiber2 <- IO.succeed("Bye").fork
      fiber = fiber1.zip(fiber2)
      tuple <- fiber.join
    } yield tuple
}
