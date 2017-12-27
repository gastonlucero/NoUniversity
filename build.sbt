lazy val akkaHttpVersion = "10.0.11"
lazy val akkaVersion = "2.5.8"

lazy val nameJar = "NoUniversity"
lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.stratio.edu",
      scalaVersion := "2.12.3"
    )),
    name := nameJar,
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-stream" % akkaVersion,
      "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
      "org.slf4j" % "slf4j-log4j12" % "1.7.25",
      "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test,
      "com.typesafe.akka" %% "akka-stream-testkit" % "2.5.8" % Test,
      "org.scalatest" %% "scalatest" % "3.0.1" % Test
    )
  )

parallelExecution in Test := false

assemblyJarName in assembly := s"$nameJar.jar"
