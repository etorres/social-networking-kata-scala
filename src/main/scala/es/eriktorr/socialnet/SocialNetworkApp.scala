package es.eriktorr.socialnet

import cats.effect._
import cats.effect.Console.io._

object SocialNetworkApp extends IOApp {
  override def run(args: List[String]): IO[ExitCode] =
    for {
      _ <- putStrLn("Welcome to yet another social network!")
    } yield ExitCode.Success
}
