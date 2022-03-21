scalaVersion := "2.13.8"

name         := "zio-exercise"
organization := ""
version      := "1.0"

scalacOptions ++= Seq(
  "-deprecation",
  "-Wunused:imports",
  "-Xlint:deprecation",
  "-Xlint:unused"
)

libraryDependencies += "dev.zio" %% "zio"         % "1.0.12"
libraryDependencies += "dev.zio" %% "zio-streams" % "1.0.12"
