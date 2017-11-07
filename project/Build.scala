import sbt._
import Keys._

object ScalaTrainBuild extends Build {
  val macroVersion = "2.1.0"
  val paradisePlugin = compilerPlugin("org.scalamacros" % "paradise" % macroVersion cross CrossVersion.full)

  val akkaVersion = "2.5.1"
  val akkaDeps = Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
//    "com.typesafe.akka" %% "akka-microkernel" % akkaVersion,
    "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
    "com.typesafe.akka" %% "akka-remote" % akkaVersion,
    "com.typesafe.akka" %% "akka-persistence" % akkaVersion,
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion
  )
  val defaultSettings = Seq(
    version := "1.0",
    scalaVersion := "2.12.4",
    //testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-h", "target/test-reports"),
    scalacOptions     ++= Seq(
      "-encoding", "UTF-8",
      "-feature",
      "-language:implicitConversions", "-language:higherKinds", "-language:postfixOps"
    ),
    libraryDependencies ++= Seq(
      "org.scala-lang.modules" %% "scala-xml" % "1.0.6",
      "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.6",
      "org.scalatest" %% "scalatest" % "3.0.3" % "test",
      "org.scalacheck" %% "scalacheck" % "1.13.5" % "test",
      "org.seleniumhq.selenium" % "selenium-java" % "3.4.0" % "test",
      "org.pegdown" % "pegdown" % "1.6.0" % "test",
      "org.mockito" % "mockito-core" % "2.8.9",
      "org.scalaz" %% "scalaz-core" % "7.2.12",
      "org.hibernate.javax.persistence" % "hibernate-jpa-2.0-api" % "1.0.1.Final"
    ) ++ akkaDeps
  )

  lazy val root: Project = Project(
    "scala-train",
    file("."),
    settings = defaultSettings ++ Seq(
      run <<= run in Compile in macros
    )) aggregate(core, macros)

  lazy val core: Project = Project(
    "scala-train-core",
    file("core"),
    settings = defaultSettings
  ) dependsOn(macros)

  lazy val macros: Project = Project(
    "scala-train-macro",
    file("macro"),
    settings = defaultSettings ++ Seq(
      scalacOptions += "-language:experimental.macros",
      libraryDependencies ++= Seq(
        "org.scala-lang" % "scala-reflect" % scalaVersion.value,
        "org.scala-lang" % "scala-compiler" % scalaVersion.value % "provided"
      ),
      addCompilerPlugin(paradisePlugin)
    )
  )
}
