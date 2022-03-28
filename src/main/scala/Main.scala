import zio._
import zio.console._
import zio.blocking._

import java.io.IOException
import java.net.ServerSocket
import scala.concurrent._
import scala.io.StdIn
import scala.util.Try

object Main {// extends zio.App {
  def run(args: List[String]) = myAppLogic.exitCode

  val myAppLogic: ZIO[Console, IOException, Unit] =
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

object CreatingEffect {
  val s1            = ZIO.succeed(42)
  val s2: Task[Int] = Task.succeed(42)
  val now           = ZIO.effectTotal(System.currentTimeMillis())

  val f1 = ZIO.fail("Oops")
  val f2 = Task.fail(new Exception("Oops"))

  val zoption: IO[Option[Nothing], Int] = ZIO.fromOption(Some(2))
  val zoption2: IO[String, Int]         = zoption.mapError(_ => "It wasn't here")

  val maybeId: IO[Option[Nothing], String] = ZIO.fromOption(Some("abc123"))

  def getUser(userId: String): IO[Throwable, Option[User]] = ZIO.succeed(Option(User(userId, "9")))
  def getTeam(teamId: String): IO[Throwable, Team]         = ZIO.succeed(Team(teamId))

  case class User(userId: String, teamId: String)
  case class Team(teamId: String)

  val result: IO[Throwable, Option[(User, Team)]] = {
    val r: ZIO[Any, Option[Throwable], (User, Team)] = for {
      id   <- maybeId
      user <- getUser(id).some
      team <- getTeam(user.teamId).asSomeError
    } yield (user, team)
    r.optional
  }

  val zeither = ZIO.fromEither(Right("Success!"))
  val ztry    = ZIO.fromTry(Try(42 / 0))

  lazy val future = Future.successful("Hello")
  val zFuture: Task[String] = ZIO.fromFuture { implicit ec =>
    future.map(_ => "GoogBye")
  }

  val getStrLn: Task[String]             = ZIO.effect(StdIn.readLine())
  val getStrLn2: IO[IOException, String] = ZIO.effect(StdIn.readLine()).refineToOrDie[IOException]

  def putStrLn(line: String): UIO[Unit] = ZIO.effectTotal(println(line))

  case class AuthError()

  object Legacy {
    def login(onSuccess: User => Unit, onFailure: AuthError => Unit): Unit = ???
  }

  val login: IO[AuthError, User] =
    IO.effectAsync[AuthError, User] { callback =>
      Legacy.login(
        user => callback(IO.succeed(user)),
        err => callback(IO.fail(err))
      )
    }

  val sleeping =
    effectBlocking(Thread.sleep(Long.MaxValue))

  def accept(l: ServerSocket) =
    effectBlockingCancelable(l.accept())(UIO.effectTotal(l.close()))
}
