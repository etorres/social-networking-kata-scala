package es.eriktorr.socialnet.domain

import cats._
import cats.derived._
import cats.implicits._
import es.eriktorr.socialnet.domain.error._
import es.eriktorr.socialnet.domain.user.UserName.UserType._
import es.eriktorr.socialnet.domain.user._
import es.eriktorr.socialnet.effect._
import eu.timepit.refined._
import eu.timepit.refined.predicates.all._
import io.estatico.newtype.macros.newtype
import io.estatico.newtype.ops._

object message {
  @newtype class MessageBody(val unBody: String)

  object MessageBody {
    def fromString(str: String): Either[InvalidParameter, MessageBody] =
      refineV[MatchesRegex[NonBlank]](str) match {
        case Left(_) => InvalidParameter("Message body cannot be blank or empty").asLeft
        case Right(refinedStr) => refinedStr.value.trim.coerce.asRight
      }

    implicit val eqMessageBody: Eq[MessageBody] = Eq.fromUniversalEquals
    implicit val showMessageBody: Show[MessageBody] = Show.show(_.toString)
  }

  final case class Message(
    sender: UserName[Sender],
    addressee: UserName[Addressee],
    body: MessageBody
  )

  type Messages = List[Message]

  object Message {
    implicit val eqMessage: Eq[Message] = semiauto.eq
    implicit val eqShow: Show[Message] = semiauto.show
  }
}
