package es.eriktorr.socialnet.domain

import cats.implicits._
import es.eriktorr.socialnet.domain.error._
import es.eriktorr.socialnet.domain.message._
import es.eriktorr.socialnet.domain.user._

import scala.util.matching.Regex

object command {
  sealed trait Command

  final case class PostCommand(to: UserName, messageBody: MessageBody) extends Command

  object Command {
    val posting: Regex = "(.+) -> (.+)".r

    def fromString(input: String): Either[RequestError, Command] = input match {
      case posting(user, message) =>
        for {
          userName <- UserName.fromString(user)
          messageBody <- MessageBody.fromString(message)
        } yield PostCommand(userName, messageBody)
      case _ => UnknownCommand.asLeft
    }
  }
}
