package es.eriktorr.socialnet.domain

import cats._
import cats.implicits._
import es.eriktorr.socialnet.domain.error._
import io.estatico.newtype.macros.newtype
import io.estatico.newtype.ops._

object user {
  @newtype case class UserName(private val unUserName: String)

  object UserName {
    def fromString(str: String): Either[InvalidParameter, UserName] =
      if (str.isBlank) InvalidParameter("User name cannot be empty").asLeft
      else str.trim.coerce.asRight

    implicit val eqUserName: Eq[UserName] = Eq.fromUniversalEquals
    implicit val showUserName: Show[UserName] = Show.show(_.toString)
  }
}
