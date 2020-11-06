package es.eriktorr.socialnet.domain

import es.eriktorr.socialnet.domain.message._
import es.eriktorr.socialnet.domain.time._
import es.eriktorr.socialnet.domain.user._

object timeline {
  final case class TimelineEvent(timeMark: TimeMark, message: Message)

  type TimelineEvents = List[TimelineEvent]

  trait Timelines[F[_]] {
    def read(userName: UserName): F[TimelineEvents]
    def save(event: TimelineEvent): F[Unit]
  }
}
