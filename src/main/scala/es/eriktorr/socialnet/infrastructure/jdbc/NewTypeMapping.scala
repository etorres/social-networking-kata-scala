package es.eriktorr.socialnet.infrastructure.jdbc

import doobie._
import es.eriktorr.socialnet.domain.user._
import io.estatico.newtype._

trait NewTypeMapping {
  implicit def addresseePut[A <: UserName.UserType]: Put[UserName[A]] =
    Put[String].contramap(_.unUserName)

  implicit def newTypePut[R, N](implicit ev: Coercible[Put[R], Put[N]], R: Put[R]): Put[N] = ev(R)
  implicit def newTypeRead[R, N](implicit ev: Coercible[Read[R], Read[N]], R: Read[R]): Read[N] =
    ev(R)
}
