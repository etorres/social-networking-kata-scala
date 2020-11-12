package es.eriktorr.socialnet.application

import cats._
import cats.derived._
import cats.implicits._
import es.eriktorr.socialnet.domain.message._
import es.eriktorr.socialnet.domain.subscription._
import es.eriktorr.socialnet.domain.time._
import es.eriktorr.socialnet.domain.user.UserName.UserType._
import es.eriktorr.socialnet.domain.user._
import es.eriktorr.socialnet.shared.infrastructure.FakeSocialNetworkContext.SocialNetworkState.initialStateFrom
import es.eriktorr.socialnet.shared.infrastructure.FakeSocialNetworkContext.withSocialNetworkContext
import es.eriktorr.socialnet.shared.infrastructure.GeneratorSyntax._
import es.eriktorr.socialnet.shared.infrastructure.SocialNetworkGenerators.{messageGen, userNameGen}
import es.eriktorr.socialnet.shared.infrastructure.TimeGenerators.timeMarkGen
import org.scalacheck._
import weaver._
import weaver.scalacheck._

object ViewPostsFromWallAndSubscriptionsSuite extends SimpleIOSuite with IOCheckers {
  simpleTest("Subscribe to timelines, and view an aggregated list of all subscriptions") {
    final case class TestCase(
      initialTimeMark: TimeMark,
      follower: UserName[Follower],
      followees: Followees,
      allMessages: Messages,
      wallAndSubscriptionMessages: Messages
    )

    object TestCase {
      implicit val showTestCase: Show[TestCase] = semiauto.show
    }

    val gen = (for {
      initialTimeMark <- timeMarkGen
      follower <- userNameGen[Follower]
      otherUsers <- Gen.containerOfN[List, UserName[Followee]](4, userNameGen[Followee])
      (followees, notFollowees) = otherUsers.splitAt(2)
      allUsers = (follower :: (followees ++ notFollowees)).map(_.asUserName[Sender])
      wallMessages <- Gen.containerOf[List, Message](
        messageGen(
          senderGen = Gen.oneOf(allUsers),
          addresseeGen = Gen.const(follower.asUserName[Addressee])
        )
      )
      subscribedWalls <- Gen.containerOf[List, Message](
        messageGen(
          senderGen = Gen.oneOf(allUsers),
          addresseeGen = Gen.oneOf(followees.map(_.asUserName[Addressee]))
        )
      )
      notSubscribedWalls <- Gen.containerOf[List, Message](
        messageGen(
          senderGen = Gen.oneOf(allUsers),
          addresseeGen = Gen.oneOf(notFollowees.map(_.asUserName[Addressee]))
        )
      )
      wallAndSubscriptionMessages = wallMessages ++ subscribedWalls
      allMessages = wallAndSubscriptionMessages ++ notSubscribedWalls
    } yield TestCase(
      initialTimeMark,
      follower,
      followees,
      allMessages,
      wallAndSubscriptionMessages
    )).sampleWithSeed("ViewPostsFromWallAndSubscriptionsSuite")

    forall(gen) {
      case TestCase(
          initialTimeMark,
          follower,
          followees,
          allMessages,
          wallAndSubscriptionMessages
          ) =>
        val initialState =
          initialStateFrom(
            initialTimeMark,
            allMessages,
            Map(follower -> followees)
          )
        withSocialNetworkContext(initialState)(
          _.viewPostsFromAllSubscriptions.viewAllPostsFor(follower.asUserName[Addressee])
        ) map {
          case (finalState, testResult) =>
            expect(testResult === finalState.timelinesState.events.filter { e =>
              wallAndSubscriptionMessages.contains_(e.message)
            })
        }
    }
  }
}
