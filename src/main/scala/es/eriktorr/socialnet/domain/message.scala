package es.eriktorr.socialnet.domain

import cats._
import cats.derived._
import cats.implicits.catsSyntaxEitherId
import es.eriktorr.socialnet.domain.error._
import es.eriktorr.socialnet.domain.user._
import io.estatico.newtype.macros.newtype
import io.estatico.newtype.ops._

object message {
  @newtype case class Addressee(unAddressee: UserName)
  @newtype class MessageBody(val unBody: String)
  @newtype case class Sender(unSender: UserName)

  object Addressee {
    implicit val eqAddressee: Eq[Addressee] = Eq.fromUniversalEquals
    implicit val showAddressee: Show[Addressee] = Show.show(_.toString)
  }

  object MessageBody {
    def fromString(str: String): Either[InvalidParameter, MessageBody] =
      if (str.isBlank) InvalidParameter("Message body cannot be empty").asLeft
      else str.trim.coerce.asRight

    implicit val eqMessageBody: Eq[MessageBody] = Eq.fromUniversalEquals
    implicit val showMessageBody: Show[MessageBody] = Show.show(_.toString)
  }

  object Sender {
    implicit val eqSender: Eq[Sender] = Eq.fromUniversalEquals
    implicit val showSender: Show[Sender] = Show.show(_.toString)
  }

  final case class Message(sender: Sender, addressee: Addressee, body: MessageBody)

  type Messages = List[Message]

  object Message {
    implicit val eqMessage: Eq[Message] = semiauto.eq
    implicit val eqShow: Show[Message] = semiauto.show
  }
}
