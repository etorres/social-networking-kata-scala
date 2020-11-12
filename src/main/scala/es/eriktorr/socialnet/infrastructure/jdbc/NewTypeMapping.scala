package es.eriktorr.socialnet.infrastructure.jdbc

import doobie._
import es.eriktorr.socialnet.domain.message._
import es.eriktorr.socialnet.domain.user._
import io.estatico.newtype._

trait NewTypeMapping {
  implicit def userNamePut[A <: UserName.UserType]: Put[UserName[A]] =
    Put[String].contramap(_.unUserName.value)

  @SuppressWarnings(Array("org.wartremover.warts.OptionPartial"))
  implicit def userNameRead[A <: UserName.UserType]: Read[UserName[A]] =
    Read[String].map(UserName.fromString[A](_).toOption.get)

  implicit val messageBodyPut: Put[MessageBody] = Put[String].contramap(_.unBody.value)

  @SuppressWarnings(Array("org.wartremover.warts.OptionPartial"))
  implicit val messageBodyRead: Read[MessageBody] =
    Read[String].map(MessageBody.fromString(_).toOption.get)

  implicit def newTypePut[R, N](implicit ev: Coercible[Put[R], Put[N]], R: Put[R]): Put[N] = ev(R)
  implicit def newTypeRead[R, N](implicit ev: Coercible[Read[R], Read[N]], R: Read[R]): Read[N] =
    ev(R)
}
