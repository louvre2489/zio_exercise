import zio._
import java.io.IOException
import java.io.FileNotFoundException

object HandlingErrors {
  val zeither: URIO[Any, Either[String, Nothing]] =
    IO.fail("fail!!").either

  def sqrt(io: UIO[Double]): ZIO[Any, String, Double] =
    ZIO.absolve(
      io.map { value =>
        if (value < 0.0) Left("Value must be >= 0.0")
        else Right(Math.sqrt(value))
      }
    )

  def openFile(s: String): IO[IOException, Array[Byte]] = ???

  val z: IO[IOException, Array[Byte]] =
    openFile("primary.json").catchAll(_ => openFile("backup.json"))

  val data: IO[IOException, Array[Byte]] =
    openFile("primary.data").catchSome { case _: FileNotFoundException =>
      openFile("backup.data")
    }

  val pimaryOrBackupData: IO[IOException, Array[Byte]] =
    openFile("primary.data").orElse(openFile("backup.data"))

  lazy val defaultData: Array[Byte] = Array(0, 0)
  val primaryOrDefaultData: UIO[Array[Byte]] =
    openFile("primary.data").fold(
      _ => defaultData,
      data => data
    )

  val primaryOrSecondaryData: IO[IOException, Array[Byte]] =
    openFile("primary.data").foldM(
      _ => openFile("secondary.data"),
      data => ZIO.succeed(data)
    )


}
