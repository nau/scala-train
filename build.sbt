name := "scala-train"

version := "1.0"

scalaVersion := "2.11.6"

libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "1.0.2"

libraryDependencies += "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.2"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.4" % "test"

libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.12.2" % "test"

libraryDependencies += "org.seleniumhq.selenium" % "selenium-java" % "2.35.0" % "test"

libraryDependencies += "org.pegdown" % "pegdown" % "1.4.2" % "test"

libraryDependencies += "org.mockito" % "mockito-core" % "2.0.7-beta"

libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.1.1"

//testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-h", "target/test-reports")
