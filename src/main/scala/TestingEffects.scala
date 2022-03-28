import zio._
import zio.console._

object TestingEffects{// extends zio.App {

  def run(args: List[String]) = {
    val c = Config("HTTP Server", 8080)
    val r = for {
      n <- square.provide(4)
      _ <- putStrLn(n.toString())
      m <- configString.provide(c)
      _ <- putStrLn(m)
    } yield ()
    r.exitCode
  }

  val square: URIO[Int, Int] =
    for {
      env <- ZIO.environment[Int]
    } yield env * env

  val result: UIO[Int] = square.provide(42)

  val r =
    for {
      env <- ZIO.environment[Int]
      _   <- CreatingEffect.putStrLn(s"env: ${env}")
    } yield env

  final case class Config(server: String, port: Int)

  val configString: URIO[Config, String] =
    for {
      server <- ZIO.access[Config](_.server)
      port   <- ZIO.access[Config](_.port)
    } yield s"Server: $server, Port: $port"

  trait DatabaseOps {
    def getTableNames: Task[List[String]]
    def getColumnNames(table: String): Task[List[String]]
  }

  val tablesAndColumns: ZIO[DatabaseOps,Throwable,(List[String], List[String])] =
    for {
      tables  <- ZIO.accessM[DatabaseOps](_.getTableNames)
      columns <- ZIO.accessM[DatabaseOps](_.getColumnNames("table"))
    } yield (tables, columns)
}

object Database {
  trait Service {
    def lookup(id: UserID): Task[UserProfile]
    def update(id: UserID, profile: UserProfile): Task[Unit]
  }
}

trait Database {
  def database: Database.Service
}

object db {
  def lookup(id: UserID): RIO[Database,UserProfile]=
    ZIO.accessM(_.database.lookup(id))
  def update(id: UserID, profile: UserProfile): RIO[Database, Unit] =
    ZIO.accessM(_.database.update(id, profile))
}

case class UserID(id: Long)
case class UserProfile(userId: UserID, userName: String)
