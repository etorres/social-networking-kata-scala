package es.eriktorr.socialnet.application

import cats._
import cats.implicits._
import cats.kernel.Eq
import es.eriktorr.socialnet.domain.infrastructure.TimelinesState
import es.eriktorr.socialnet.domain.message._
import es.eriktorr.socialnet.domain.timeline.TimelineEvent
import es.eriktorr.socialnet.domain.user._
import es.eriktorr.socialnet.shared.infrastructure.FakeSocialNetworkContext.SocialNetworkState.emptyTimelines
import es.eriktorr.socialnet.shared.infrastructure.FakeSocialNetworkContext.{
  withSocialNetworkContext,
  SocialNetworkState
}
import es.eriktorr.socialnet.shared.infrastructure.SocialNetworkGenerators.{
  messageBodyGen,
  userNameGen
}
import org.scalacheck.cats.implicits._
import weaver._
import weaver.scalacheck._

object PostMessageToPersonalTimelineSuite extends SimpleIOSuite with IOCheckers {
  simpleTest("Post messages to a personal timeline") {
    implicit val showMessage: Show[Message] = Show.show(_.toString)
    implicit val eqSocialNetworkState: Eq[SocialNetworkState] = Eq.fromUniversalEquals

    val gen = (userNameGen, userNameGen, messageBodyGen).tupled.map {
      case (from, to, body) => Message(from, to, body)
    }

    forall(gen) { message =>
      withSocialNetworkContext(initialState = emptyTimelines)(
        _.postMessageToPersonalTimeline.post(message)
      ) map {
        case (state, _) =>
          expect(
            state === SocialNetworkState(timelinesState =
              TimelinesState(events = List(TimelineEvent(???, message)))
            )
          )
      }
    }
  }
}

/*
Posting: Alice can publish messages to a personal timeline

> Alice -> I love the weather today
> Bob -> Damn! We lost!
> Bob -> Good game though.
 */
