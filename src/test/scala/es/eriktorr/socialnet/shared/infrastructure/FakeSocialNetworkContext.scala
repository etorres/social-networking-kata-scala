package es.eriktorr.socialnet.shared.infrastructure

import cats.effect._
import cats.effect.concurrent.Ref
import es.eriktorr.socialnet.SocialNetworkContext
import es.eriktorr.socialnet.domain.infrastructure.{FakeTimeMaker, FakeTimelines, TimelinesState}

object FakeSocialNetworkContext {
  final case class SocialNetworkState(timelinesState: TimelinesState)

  object SocialNetworkState {
    def emptyTimelines: SocialNetworkState =
      SocialNetworkState(timelinesState = TimelinesState.empty)
  }

  def withSocialNetworkContext[A](
    initialState: SocialNetworkState
  )(runTest: SocialNetworkContext => IO[A]): IO[(SocialNetworkState, A)] =
    for {
      timelinesRef <- refFrom(initialState.timelinesState)
      context = new SocialNetworkContext(
        FakeTimeMaker.impl[IO],
        FakeTimelines.impl[IO](timelinesRef)
      )
      testResult <- runTest(context)
      finalTimelinesState <- timelinesRef.get
    } yield {
      (initialState.copy(timelinesState = finalTimelinesState), testResult)
    }

  private[this] def refFrom[A <: Product](a: A) = Ref.of[IO, A](a)
}
