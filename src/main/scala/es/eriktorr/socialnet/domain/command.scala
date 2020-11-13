package es.eriktorr.socialnet.domain

import cats.implicits._
import es.eriktorr.socialnet.domain.error._
import es.eriktorr.socialnet.domain.message._
import es.eriktorr.socialnet.domain.user.UserName.UserType._
import es.eriktorr.socialnet.domain.user._

import scala.util.matching.Regex

object command {
  sealed trait Command

  final case class PostCommand(addressee: UserName[Addressee], messageBody: MessageBody)
      extends Command

  object Command {
    val posting: Regex = "(.+) \\*> (.+)".r

    def fromString(input: String): Either[RequestError, Command] = input match {
      case posting(user, message) =>
        for {
          addressee <- UserName.fromString[Addressee](user)
          messageBody <- MessageBody.fromString(message)
        } yield PostCommand(addressee, messageBody)
      case _ => UnknownCommand.asLeft
    }
  }
}
