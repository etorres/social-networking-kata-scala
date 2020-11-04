package es.eriktorr.socialnet.domain

import es.eriktorr.socialnet.domain.user._
import eu.timepit.refined.api.Refined.unsafeApply
import eu.timepit.refined.types.string._
import io.estatico.newtype.macros.newtype

object message {
  @newtype case class MessageBody(unBody: NonEmptyString)

  object MessageBody {
    def fromString(input: String): MessageBody = MessageBody(unsafeApply(input))
  }

  final case class Message(from: UserName, to: UserName, body: MessageBody)
}
