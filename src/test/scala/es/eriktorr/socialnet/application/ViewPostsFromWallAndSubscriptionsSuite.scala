package es.eriktorr.socialnet.application

import java.time.LocalDateTime

import cats._
import cats.derived._
import cats.implicits._
import es.eriktorr.socialnet.domain.message._
import es.eriktorr.socialnet.domain.subscription._
import es.eriktorr.socialnet.domain.time._
import es.eriktorr.socialnet.domain.user._
import es.eriktorr.socialnet.shared.infrastructure.FakeSocialNetworkContext.SocialNetworkState.initialStateFrom
import es.eriktorr.socialnet.shared.infrastructure.FakeSocialNetworkContext.withSocialNetworkContext
import es.eriktorr.socialnet.shared.infrastructure.GeneratorSyntax._
import es.eriktorr.socialnet.shared.infrastructure.SocialNetworkGenerators.{messageGen, userNameGen}
import es.eriktorr.socialnet.shared.infrastructure.TimeGenerators._
import org.scalacheck._
import weaver._
import weaver.scalacheck._

object ViewPostsFromWallAndSubscriptionsSuite extends SimpleIOSuite with IOCheckers {
  simpleTest("Subscribe to timelines, and view an aggregated list of all subscriptions") {
    final case class TestCase(
      initialTimeMark: TimeMark,
      subscriber: UserName,
      subscribedTo: List[UserName],
      allMessages: List[Message],
      wallAndSubscriptionMessages: List[Message]
    )

    object TestCase {
      implicit val showTestCase: Show[TestCase] = semiauto.show
    }

    val gen = (for {
      initialTimeMark <- Arbitrary.arbitrary[LocalDateTime].map(a => TimeMark(a))
      subscriber <- userNameGen
      otherUsers <- Gen.containerOfN[List, UserName](4, userNameGen)
      (subscribedTo, notSubscribedTo) = otherUsers.splitAt(2)
      allUsers = subscriber :: (subscribedTo ++ notSubscribedTo)
      wallMessages <- Gen.containerOf[List, Message](
        messageGen(senderGen = Gen.oneOf(allUsers), addresseeGen = Gen.const(subscriber))
      )
      subscribedWalls <- Gen.containerOf[List, Message](
        messageGen(senderGen = Gen.oneOf(allUsers), addresseeGen = Gen.oneOf(subscribedTo))
      )
      notSubscribedWalls <- Gen.containerOf[List, Message](
        messageGen(
          senderGen = Gen.oneOf(allUsers),
          addresseeGen = Gen.oneOf(notSubscribedTo)
        )
      )
      wallAndSubscriptionMessages = wallMessages ++ subscribedWalls
      allMessages = wallAndSubscriptionMessages ++ notSubscribedWalls
    } yield TestCase(
      initialTimeMark,
      subscriber,
      subscribedTo,
      allMessages,
      wallAndSubscriptionMessages
    )).sampleWithSeed("ViewPostsFromWallAndSubscriptionsSuite")

    forall(gen) {
      case TestCase(
          initialTimeMark,
          subscriber,
          subscribedTo,
          allMessages,
          wallAndSubscriptionMessages
          ) =>
        val initialState =
          initialStateFrom(
            initialTimeMark,
            allMessages,
            Map(Subscriber(subscriber) -> subscribedTo.map(a => TimelineSubscription(a)))
          )
        withSocialNetworkContext(initialState)(
          _.viewPostsFromAllSubscriptions.viewAllPostsFor(subscriber)
        ) map {
          case (finalState, testResult) =>
            expect(testResult === finalState.timelinesState.events.filter { e =>
              wallAndSubscriptionMessages.contains_(e.message)
            })
        }
    }
  }
}
