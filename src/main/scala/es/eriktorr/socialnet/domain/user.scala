package es.eriktorr.socialnet.domain

import cats._
import cats.implicits._
import es.eriktorr.socialnet.domain.error._
import es.eriktorr.socialnet.effect._
import eu.timepit.refined._
import eu.timepit.refined.predicates.all._
import io.estatico.newtype.macros.newtype
import io.estatico.newtype.ops._

object user {
  @newtype class UserName[A <: UserName.UserType](val unUserName: String) {
    def mkString(separator: String): String = unUserName.mkString(separator)

    def <(other: UserName[A]): Boolean = unUserName < other.unUserName

    def asUserName[B <: UserName.UserType]: UserName[B] = unUserName.coerce[UserName[B]]
  }

  object UserName {
    sealed trait UserType

    object UserType {
      sealed trait Addressee extends UserType
      sealed trait Followee extends UserType
      sealed trait Follower extends UserType
      sealed trait Sender extends UserType
    }

    // TODO: this only needed for IntelliJ to compile
    implicit def ev[A <: UserName.UserType, B]: io.estatico.newtype.Coercible[B, UserName[A]] =
      io.estatico.newtype.Coercible.instance[B, UserName[A]]

    def fromString[A <: UserName.UserType](str: String): Either[InvalidParameter, UserName[A]] =
      refineV[MatchesRegex[NonBlank]](str) match {
        case Left(_) => InvalidParameter("User name cannot be blank or empty").asLeft
        case Right(refinedStr) => refinedStr.value.trim.coerce[UserName[A]].asRight
      }
  }

  implicit def eqUserName[A <: UserName.UserType]: Eq[UserName[A]] = Eq.fromUniversalEquals
  implicit def showUserName[A <: UserName.UserType]: Show[UserName[A]] = Show.show(_.toString)
}
