package es.eriktorr.socialnet.domain

import es.eriktorr.socialnet.domain.user._
import eu.timepit.refined.types.string._
import io.estatico.newtype.macros.newtype

object message {
  @newtype case class MessageBody(unBody: NonEmptyString)

  final case class Message(from: UserName, to: UserName, body: MessageBody)
}
