package es.eriktorr.socialnet.shared.infrastructure

import es.eriktorr.socialnet.domain.message.MessageBody
import es.eriktorr.socialnet.domain.user._
import eu.timepit.refined.api.Refined.unsafeApply
import org.scalacheck.Gen

object SocialNetworkGenerators {
  val userNameGen: Gen[UserName] = Gen.alphaNumStr.map(userName => UserName(unsafeApply(userName)))

  val messageBodyGen: Gen[MessageBody] =
    Gen.alphaNumStr.map(messageBody => MessageBody(unsafeApply(messageBody)))
}
