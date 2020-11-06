package es.eriktorr.socialnet.shared.infrastructure

import cats.effect._
import cats.effect.concurrent.Ref
import cats.implicits._
import es.eriktorr.socialnet.SocialNetworkContext
import es.eriktorr.socialnet.domain.infrastructure.{
  FakeTimeMarker,
  FakeTimelines,
  TimeMarkerState,
  TimelinesState
}
import es.eriktorr.socialnet.domain.message._
import es.eriktorr.socialnet.domain.time._
import es.eriktorr.socialnet.domain.timeline._

import scala.annotation.tailrec

object FakeSocialNetworkContext {
  final case class SocialNetworkState(
    timeMarkerState: TimeMarkerState,
    timelinesState: TimelinesState
  )

  object SocialNetworkState {
    def initialStateFrom(
      initialTimeMark: TimeMark,
      messages: List[Message]
    ): SocialNetworkState = {
      @tailrec
      def timelineEventsFrom(
        messages: List[Message],
        events: List[TimelineEvent]
      ): List[TimelineEvent] =
        messages match {
          case Nil => events
          case ::(head, next) =>
            timelineEventsFrom(
              next,
              TimelineEvent(
                events.headOption.map(_.timeMark).getOrElse(initialTimeMark).plusMinutes(1L),
                head
              ) :: events
            )
        }

      val events: List[TimelineEvent] = timelineEventsFrom(messages, List.empty)

      SocialNetworkState(
        timeMarkerState =
          TimeMarkerState.startingAt(events.map(_.timeMark).headOption.getOrElse(initialTimeMark)),
        timelinesState = TimelinesState(events)
      )
    }

    def expectedStateFrom(
      message: Message,
      initialState: SocialNetworkState,
      finalState: SocialNetworkState
    ): SocialNetworkState = {
      val monotonicEvents =
        finalState.timeMarkerState.marks.sortWith(_ isAfter _).zip(List(message)).map {
          case (timeMark, message) => TimelineEvent(timeMark, message)
        }
      initialState.copy(timelinesState =
        TimelinesState(monotonicEvents ++ initialState.timelinesState.events)
      )
    }
  }

  def withSocialNetworkContext[A](
    initialState: SocialNetworkState
  )(runTest: SocialNetworkContext => IO[A]): IO[(SocialNetworkState, A)] =
    for {
      (timeMakerRef, timelinesRef) <- (
        refFrom(initialState.timeMarkerState),
        refFrom(initialState.timelinesState)
      ).tupled
      context = new SocialNetworkContext(
        FakeTimeMarker.impl[IO](timeMakerRef),
        FakeTimelines.impl[IO](timelinesRef)
      )
      testResult <- runTest(context)
      (finalTimeMakerState, finalTimelinesState) <- (timeMakerRef.get, timelinesRef.get).tupled
    } yield {
      (
        initialState.copy(
          timeMarkerState = finalTimeMakerState,
          timelinesState = finalTimelinesState
        ),
        testResult
      )
    }

  private[this] def refFrom[A <: Product](a: A) = Ref.of[IO, A](a)
}
