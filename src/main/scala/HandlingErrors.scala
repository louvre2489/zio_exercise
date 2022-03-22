import zio._

object HandlingErrors {
  val zeither: URIO[Any, Either[String, Nothing]] =
    IO.fail("fail!!").either

  def sqrt(io: UIO[Double]): ZIO[Any,String,Double] =
    ZIO.absolve(
      io.map { value =>
        if (value < 0.0) Left("Value must be >= 0.0")
        else Right(Math.sqrt(value))
      }
    )
}
