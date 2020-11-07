package es.eriktorr.socialnet.domain

import cats._
import cats.implicits._
import io.estatico.newtype.macros.newtype
import io.estatico.newtype.ops._

import scala.util.control.NoStackTrace

object user {
  @newtype case class UserName(private val unUserName: String)

  val DEFAULT_USER_NAME: UserName = UserName("me")

  final case class InvalidUserName(error: String) extends NoStackTrace

  object UserName {
    def fromString(str: String): Either[InvalidUserName, UserName] =
      if (str.isBlank) InvalidUserName("User name cannot be empty").asLeft
      else str.trim.coerce.asRight

    implicit val eqUserName: Eq[UserName] = Eq.fromUniversalEquals
    implicit val showUserName: Show[UserName] = Show.show(_.toString)
  }
}
