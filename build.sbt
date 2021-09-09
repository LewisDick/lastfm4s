lazy val root = (project in file("."))
  .settings(
    organization := "com.lewisdick",
    name := "lastfm4s",
    version := "0.1",
    scalaVersion := "2.13.5",
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-circe"        % "0.21.7",
      "org.http4s" %% "http4s-client"        % "0.21.7",
      "org.http4s" %% "http4s-blaze-client" % "0.21.7",
      "io.circe" %% "circe-generic" % "0.13.0",
      "org.typelevel" %% "cats-core" % "2.1.1",
      "org.typelevel" %% "cats-effect" % "2.1.4",
      "com.beachape" %% "enumeratum" % "1.6.1",
      "com.beachape" %% "enumeratum-circe" % "1.6.1"
    )
  )