package es.eriktorr.socialnet

import eu.timepit.refined._
import eu.timepit.refined.api.Refined
import eu.timepit.refined.predicates.all._
import io.estatico.newtype.Coercible

package object effect extends ResourceSyntax {
  type NonBlank = W.`"""\\A(?!\\s*\\Z).+"""`.T

  type NonBlankString = String Refined NonBlank

  implicit def evNonBlankString[A]: Coercible[String Refined MatchesRegex[NonBlank], A] =
    Coercible.instance[String Refined MatchesRegex[NonBlank], A]
}
