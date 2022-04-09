val scala3Version = "3.1.1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "Function-Files",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,
    libraryDependencies += "org.typelevel" %% "cats-core" % "2.7.0",
    libraryDependencies += "org.typelevel" %% "cats-kernel" % "2.7.0",
    libraryDependencies += "org.scalameta" %% "munit" % "0.7.29" % Test
  )
