import org.scalafmt.sbt.ScalafmtPlugin.autoImport.scalafmtOnCompile
import sbt.Keys._
import sbt._
import wartremover.Wart
import wartremover.WartRemover.autoImport._

organization := "es.eriktorr"
name := "social-networking-kata-scala"
version := (version in ThisBuild).value

scalaVersion := "2.13.3"

val catsCoreVersion = "2.2.0"
val catsEffectsVersion = "2.2.0"
val catsScalacheckVersion = "0.3.0"
val console4catsVersion = "0.8.1"
val doobieVersion = "0.9.2"
val kittensVersion = "2.2.0"
val log4catsVersion = "1.1.1"
val log4jVersion = "2.13.3"
val newtypeVersion = "0.4.4"
val squantsVersion = "1.7.0"
val weaverVersion = "0.5.0"

libraryDependencies ++= Seq(
  compilerPlugin("org.typelevel" %% "kind-projector" % "0.11.0" cross CrossVersion.full),
  compilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1" cross CrossVersion.binary),
  "org.typelevel" %% "cats-core" % catsCoreVersion,
  "org.typelevel" %% "cats-effect" % catsEffectsVersion,
  "io.chrisdavenport" %% "cats-scalacheck" % catsScalacheckVersion % Test,
  "dev.profunktor" %% "console4cats" % console4catsVersion,
  "org.tpolecat" %% "doobie-hikari" % doobieVersion,
  "org.tpolecat" %% "doobie-postgres" % doobieVersion,
  "org.typelevel" %% "kittens" % kittensVersion,
  "io.chrisdavenport" %% "log4cats-core" % log4catsVersion,
  "org.apache.logging.log4j" % "log4j-core" % log4jVersion,
  "org.apache.logging.log4j" % "log4j-slf4j-impl" % log4jVersion,
  "io.chrisdavenport" %% "log4cats-slf4j" % log4catsVersion,
  "io.estatico" %% "newtype" % newtypeVersion,
  "org.typelevel" %% "squants" % squantsVersion,
  "com.disneystreaming" %% "weaver-framework" % weaverVersion % Test,
  "com.disneystreaming" %% "weaver-scalacheck" % weaverVersion % Test
)

dependencyOverrides += "org.typelevel" %% "cats-core" % catsCoreVersion

scalacOptions ++= Seq(
  "-encoding",
  "utf8",
  "-Xfatal-warnings",
  "-Xlint",
  "-Xlint:-byname-implicit",
  "-Ymacro-annotations",
  "-deprecation",
  "-unchecked",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-feature"
)

javacOptions ++= Seq(
  "-g:none",
  "-source",
  "11",
  "-target",
  "11",
  "-encoding",
  "UTF-8"
)

scalafmtOnCompile := true

val warts: Seq[Wart] = Warts.allBut(
  Wart.Any,
  Wart.Nothing,
  Wart.Equals,
  Wart.DefaultArguments,
  Wart.Overloading,
  Wart.ToString,
  Wart.ImplicitParameter,
  Wart.ImplicitConversion // @newtype
)

wartremoverErrors in (Compile, compile) ++= warts
wartremoverErrors in (Test, compile) ++= warts

testFrameworks += new TestFramework("weaver.framework.TestFramework")

parallelExecution in Test := false
