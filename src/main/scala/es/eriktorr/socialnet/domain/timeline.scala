package es.eriktorr.socialnet.domain

import es.eriktorr.socialnet.domain.message._
import es.eriktorr.socialnet.domain.time.TimeMark
import io.estatico.newtype.macros.newtype

object timeline {
  @newtype case class TimelineEvent(timeMark: TimeMark, message: Message)

  trait Timelines[F[_]] {
    def save(event: TimelineEvent): F[Unit]
  }
}
