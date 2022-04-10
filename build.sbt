val scala3Version = "3.1.1"
val circeVersion = "0.14.1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "Function-Files",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,
    libraryDependencies += "org.typelevel" %% "cats-core" % "2.7.0",
    libraryDependencies += "org.typelevel" %% "cats-kernel" % "2.7.0",
    libraryDependencies += "org.scalameta" %% "munit" % "0.7.29" % Test,

    libraryDependencies ++= Seq(
        "io.circe" %% "circe-core",
        "io.circe" %% "circe-parser"
    ).map(_ % circeVersion),

    libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.11",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.11" % "test"

)
