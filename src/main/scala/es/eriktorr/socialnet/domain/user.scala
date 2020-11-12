package es.eriktorr.socialnet.domain

import cats._
import cats.implicits._
import es.eriktorr.socialnet.domain.error._
import io.estatico.newtype.macros.newtype

object user {
  @newtype class UserName[A <: UserName.UserType](val unUserName: String) {
    def mkString(separator: String): String = unUserName.mkString(separator)

    def <(other: UserName[A]): Boolean = unUserName < other.unUserName

    @SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf"))
    def asUserName[B <: UserName.UserType]: UserName[B] = unUserName.asInstanceOf[UserName[B]]
  }

  object UserName {
    sealed trait UserType

    object UserType {
      sealed trait Addressee extends UserType
      sealed trait Followee extends UserType
      sealed trait Follower extends UserType
      sealed trait Sender extends UserType
    }

    @SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf"))
    def fromString[A <: UserName.UserType](str: String): Either[InvalidParameter, UserName[A]] =
      if (str.isBlank) InvalidParameter("User name cannot be empty").asLeft
      else str.trim.asInstanceOf[UserName[A]].asRight
  }

  implicit def eqUserName[A <: UserName.UserType]: Eq[UserName[A]] = Eq.fromUniversalEquals
  implicit def showUserName[A <: UserName.UserType]: Show[UserName[A]] = Show.show(_.toString)
}
