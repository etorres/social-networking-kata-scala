package es.eriktorr.socialnet.application

import cats._
import cats.data._
import cats.derived._
import cats.implicits._
import es.eriktorr.socialnet.domain.message._
import es.eriktorr.socialnet.domain.time._
import es.eriktorr.socialnet.shared.infrastructure.FakeSocialNetworkContext.SocialNetworkState.{
  expectedStateFrom,
  initialStateFrom
}
import es.eriktorr.socialnet.shared.infrastructure.FakeSocialNetworkContext.withSocialNetworkContext
import es.eriktorr.socialnet.shared.infrastructure.GeneratorSyntax._
import es.eriktorr.socialnet.shared.infrastructure.SocialNetworkGenerators.messageGen
import es.eriktorr.socialnet.shared.infrastructure.TimeGenerators.timeMarkGen
import org.scalacheck._
import weaver._
import weaver.scalacheck._

object PostMessageToTimelineSuite extends SimpleIOSuite with IOCheckers {
  simpleTest("Post messages to a personal timeline") {
    final case class TestCase(initialTimeMark: TimeMark, messages: NonEmptyList[Message])

    object TestCase {
      implicit val showTestCase: Show[TestCase] = semiauto.show
    }

    val gen = (for {
      initialTimeMark <- timeMarkGen
      last <- messageGen()
      init <- Gen.containerOf[List, Message](messageGen())
    } yield TestCase(initialTimeMark, NonEmptyList.ofInitLast(init, last)))
      .sampleWithSeed("PostMessageToPersonalTimelineSuite")

    forall(gen) {
      case TestCase(initialTimeMark, messages) =>
        val initialState = initialStateFrom(initialTimeMark, messages.tail, Map.empty)
        withSocialNetworkContext(initialState)(
          _.postMessageToPersonalTimeline.post(messages.head)
        ) map {
          case (finalState, _) =>
            expect(
              finalState.timelinesState === expectedStateFrom(
                messages.head,
                initialState,
                finalState
              ).timelinesState
            )
        }
    }
  }
}
