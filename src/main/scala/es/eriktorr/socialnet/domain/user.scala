package es.eriktorr.socialnet.domain

import eu.timepit.refined.types.string._
import io.estatico.newtype.macros.newtype

object user {
  @newtype case class UserName(unName: NonEmptyString)
}
