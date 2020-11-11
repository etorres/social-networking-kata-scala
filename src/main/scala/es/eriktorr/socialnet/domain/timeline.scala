package es.eriktorr.socialnet.domain

import cats._
import cats.data._
import cats.derived._
import es.eriktorr.socialnet.domain.message._
import es.eriktorr.socialnet.domain.time._
import es.eriktorr.socialnet.domain.user._

object timeline {
  final case class TimelineEvent(timeMark: TimeMark, message: Message) {
    def isAfter(other: TimelineEvent): Boolean = timeMark.isAfter(other.timeMark)
    def isBefore(other: TimelineEvent): Boolean = timeMark.isBefore(other.timeMark)
  }

  object TimelineEvent {
    implicit val eqTimelineEvent: Eq[TimelineEvent] = semiauto.eq
    implicit val showTimelineEvent: Show[TimelineEvent] = semiauto.show
  }

  type TimelineEvents = List[TimelineEvent]

  trait Timelines[F[_]] {
    def readBy(userNames: NonEmptyList[UserName]): F[TimelineEvents]
    def save(event: TimelineEvent): F[Unit]
  }
}
