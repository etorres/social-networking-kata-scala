package es.eriktorr.socialnet

import eu.timepit.refined._

package object effect extends ResourceSyntax {
  type NonBlank = W.`"""\\A(?!\\s*\\Z).+"""`.T
}
