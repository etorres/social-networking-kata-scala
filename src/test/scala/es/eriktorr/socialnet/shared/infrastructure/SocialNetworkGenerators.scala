package es.eriktorr.socialnet.shared.infrastructure

import cats.implicits._
import es.eriktorr.socialnet.domain.error._
import es.eriktorr.socialnet.domain.message._
import es.eriktorr.socialnet.domain.user.UserName.UserType._
import es.eriktorr.socialnet.domain.user._
import org.scalacheck._
import org.scalacheck.cats.implicits._

object SocialNetworkGenerators {
  def stringOfAtMost(maxLength: Int, charGen: Gen[Char]): Gen[String] =
    for {
      length <- Gen.choose(1, maxLength)
      string <- Gen.listOfN(length, charGen).map(_.mkString)
    } yield string

  def nonBlankStringOfAtMost(maxLength: Int): Gen[String] =
    stringOfAtMost(maxLength, Gen.alphaNumChar)

  def printableAsciiStringOfAtMost(maxLength: Int): Gen[String] =
    stringOfAtMost(maxLength, Gen.asciiPrintableChar)

  def maybeUserNameGen[A <: UserName.UserType]: Gen[Either[InvalidParameter, UserName[A]]] =
    nonBlankStringOfAtMost(24).map(userName => UserName.fromString[A](userName))

  @SuppressWarnings(Array("org.wartremover.warts.OptionPartial"))
  def userNameGen[A <: UserName.UserType]: Gen[UserName[A]] =
    maybeUserNameGen[A].map(_.toOption.get)

  val maybeMessageBodyGen: Gen[Either[InvalidParameter, MessageBody]] =
    printableAsciiStringOfAtMost(256).map(messageBody => MessageBody.fromString(messageBody))

  @SuppressWarnings(Array("org.wartremover.warts.OptionPartial"))
  val messageBodyGen: Gen[MessageBody] = maybeMessageBodyGen.map(_.toOption.get)

  def messageGen(
    senderGen: Gen[UserName[Sender]] = userNameGen[Sender],
    addresseeGen: Gen[UserName[Addressee]] = userNameGen[Addressee]
  ): Gen[Message] =
    (senderGen, addresseeGen, messageBodyGen).tupled.map {
      case (sender, addressee, body) => Message(sender, addressee, body)
    }
}
