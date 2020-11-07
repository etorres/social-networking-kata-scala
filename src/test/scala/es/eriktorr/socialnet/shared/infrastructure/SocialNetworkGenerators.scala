package es.eriktorr.socialnet.shared.infrastructure

import cats.implicits._
import es.eriktorr.socialnet.domain.error._
import es.eriktorr.socialnet.domain.message._
import es.eriktorr.socialnet.domain.user._
import org.scalacheck.Gen
import org.scalacheck.cats.implicits._

object SocialNetworkGenerators {
  val nonBlankStringGen: Gen[String] = Gen.alphaNumStr.suchThat(_.nonEmpty).retryUntil(_ => true)

  val maybeUserNameGen: Gen[Either[InvalidParameter, UserName]] =
    nonBlankStringGen.map(userName => UserName.fromString(userName))

  @SuppressWarnings(Array("org.wartremover.warts.OptionPartial"))
  val userNameGen: Gen[UserName] = maybeUserNameGen.map(_.toOption.get)

  val maybeMessageBodyGen: Gen[Either[InvalidParameter, MessageBody]] =
    nonBlankStringGen.map(messageBody => MessageBody.fromString(messageBody))

  @SuppressWarnings(Array("org.wartremover.warts.OptionPartial"))
  val messageBodyGen: Gen[MessageBody] = maybeMessageBodyGen.map(_.toOption.get)

  def messageGen(fromGen: Gen[UserName]): Gen[Message] =
    (fromGen, userNameGen, messageBodyGen).tupled.map {
      case (from, to, body) => Message(from, to, body)
    }
}
