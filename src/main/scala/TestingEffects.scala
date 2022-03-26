import zio._

object TestingEffects {

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
