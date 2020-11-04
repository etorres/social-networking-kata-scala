package es.eriktorr.socialnet

import cats.effect._
import cats.effect.Console.io._

object SocialNetworkApp extends IOApp {
  override def run(args: List[String]): IO[ExitCode] =
    for {
      _ <- putStrLn("Welcome to yet another social network!")
      _ <- putStrLn("Please enter your name: ")
      userName <- readLn
      _ <- putStrLn(s"Hi $userName, now you can post to someone's timeline:")
      _ <- putStrLn(s"<her/his name> -> <message>")
    } yield ExitCode.Success
}
