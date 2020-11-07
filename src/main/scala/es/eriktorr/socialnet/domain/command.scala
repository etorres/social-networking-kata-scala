package es.eriktorr.socialnet.domain

import cats.implicits._
import es.eriktorr.socialnet.domain.message._
import es.eriktorr.socialnet.domain.user._

import scala.util.control.NoStackTrace
import scala.util.matching.Regex

object command {
  sealed trait RequestError extends NoStackTrace

  case object UnknownCommand extends RequestError

  sealed trait Command

  final case class PostCommand(to: UserName, messageBody: MessageBody) extends Command

  object Command {
    val posting: Regex = "(.+) -> (.+)".r

    def fromString(input: String): Either[RequestError, Command] = input match {
      case posting(user, message) =>
        PostCommand(UserName.fromString(user), MessageBody.fromString(message)).asRight
      case _ => UnknownCommand.asLeft
    }
  }
}
