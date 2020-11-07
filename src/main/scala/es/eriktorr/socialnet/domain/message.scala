package es.eriktorr.socialnet.domain

import cats._
import cats.derived._
import es.eriktorr.socialnet.domain.user._
import eu.timepit.refined.api.Refined.unsafeApply
import eu.timepit.refined.types.string._
import io.estatico.newtype.macros.newtype

object message {
  @newtype case class MessageBody(private val unBody: NonEmptyString)

  object MessageBody {
    def fromString(input: String): MessageBody = MessageBody(unsafeApply(input))

    implicit val eqMessageBody: Eq[MessageBody] = Eq.fromUniversalEquals
    implicit val showMessageBody: Show[MessageBody] = Show.show(_.toString)
  }

  final case class Message(from: UserName, to: UserName, body: MessageBody)

  object Message {
    implicit val eqMessage: Eq[Message] = semiauto.eq
    implicit val eqShow: Show[Message] = semiauto.show
  }
}
