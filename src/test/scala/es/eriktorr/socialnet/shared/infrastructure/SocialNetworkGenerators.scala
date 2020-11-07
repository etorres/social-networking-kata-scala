package es.eriktorr.socialnet.shared.infrastructure

import cats.implicits._
import es.eriktorr.socialnet.domain.message._
import es.eriktorr.socialnet.domain.user._
import eu.timepit.refined.api.Refined.unsafeApply
import org.scalacheck.Gen
import org.scalacheck.cats.implicits._

object SocialNetworkGenerators {
  val nonBlankStringGen: Gen[String] = Gen.alphaNumStr.suchThat(_.nonEmpty).retryUntil(_ => true)

  val maybeUserNameGen: Gen[Either[InvalidUserName, UserName]] =
    nonBlankStringGen.map(userName => UserName.fromString(userName))

  @SuppressWarnings(Array("org.wartremover.warts.OptionPartial"))
  val userNameGen: Gen[UserName] = maybeUserNameGen.map(_.toOption.get)

  val messageBodyGen: Gen[MessageBody] =
    nonBlankStringGen.map(messageBody => MessageBody(unsafeApply(messageBody)))

  val messageGen: Gen[Message] = (userNameGen, userNameGen, messageBodyGen).tupled.map {
    case (from, to, body) => Message(from, to, body)
  }
}
