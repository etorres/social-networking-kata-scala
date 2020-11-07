package es.eriktorr.socialnet.domain

import cats._
import eu.timepit.refined.api.Refined.unsafeApply
import eu.timepit.refined.types.string._
import io.estatico.newtype.macros.newtype

object user {
  @newtype case class UserName(unName: NonEmptyString)

  object UserName {
    def fromString(input: String): UserName = UserName(unsafeApply(input))

    implicit val eqUserName: Eq[UserName] = Eq.fromUniversalEquals
    implicit val showUserName: Show[UserName] = Show.show(_.toString)
  }
}
