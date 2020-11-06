package es.eriktorr.socialnet.shared.infrastructure

import cats.implicits._
import es.eriktorr.socialnet.domain.message._
import es.eriktorr.socialnet.domain.user._
import eu.timepit.refined.api.Refined.unsafeApply
import org.scalacheck.Gen
import org.scalacheck.cats.implicits._

object SocialNetworkGenerators {
  val userNameGen: Gen[UserName] = Gen.alphaNumStr.map(userName => UserName(unsafeApply(userName)))

  val messageBodyGen: Gen[MessageBody] =
    Gen.alphaNumStr.map(messageBody => MessageBody(unsafeApply(messageBody)))

  val messageGen: Gen[Message] = (userNameGen, userNameGen, messageBodyGen).tupled.map {
    case (from, to, body) => Message(from, to, body)
  }
}
