package es.eriktorr.socialnet

import cats.effect.Console.io._
import cats.effect._
import cats.implicits._
import es.eriktorr.socialnet.domain.command._
import es.eriktorr.socialnet.domain.error._
import es.eriktorr.socialnet.domain.message._
import es.eriktorr.socialnet.domain.user._
import io.chrisdavenport.log4cats.Logger
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger

object SocialNetworkApp extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    def programResource(config: SocialNetworkConfig): Resource[IO, SocialNetworkContext] =
      SocialNetworkContext.impl(config)

    def program(logger: Logger[IO]): IO[ExitCode] =
      (for {
        config <- SocialNetworkConfig.from(args)
        _ <- putStrLn("Welcome to yet another social network!")
        _ <- putStrLn("Please enter your name: ")
        name <- readLn
        userName <- IO.fromEither(UserName.fromString(name))
        _ <- putStrLn(s"Hi $name, now you can post to someone's timeline:")
        _ <- putStrLn(s"<her/his name> -> <message>")
        request <- readLn
        command = Command.fromString(request)
        result <- IO
          .fromEither(command)
          .map {
            case PostCommand(to, messageBody) =>
              programResource(config).use(
                _.postMessageToPersonalTimeline
                  .post(Message(userName, to, messageBody))
              )
          }
          .as(ExitCode.Success)
      } yield result)
        .onError {
          case e: RequestError => logger.error(e)(e.getMessage)
          case unhandledError => logger.error(unhandledError)("Unhandled error found")
        }
        .handleError(_ => ExitCode.Error)

    for {
      logger <- Slf4jLogger.create[IO]
      result <- program(logger)
    } yield result
  }
}
