package es.eriktorr.socialnet.domain

import java.time.OffsetDateTime

import cats._
import cats.implicits._
import io.estatico.newtype.macros.newtype

object time {
  @newtype case class TimeMark(unTimeMark: OffsetDateTime) {
    def isAfter(other: TimeMark): Boolean = unTimeMark.isAfter(other.unTimeMark)
    def isBefore(other: TimeMark): Boolean = unTimeMark.isBefore(other.unTimeMark)
    def plusMinutes(minutes: Long): TimeMark = TimeMark(unTimeMark.plusMinutes(minutes))
  }

  object TimeMark {
    implicit val eqTimeMark: Eq[TimeMark] = Eq.fromUniversalEquals
    implicit val showTimeMark: Show[TimeMark] = Show.show(_.toString)
  }

  trait TimeMarker[F[_]] {
    def now: F[TimeMark]
  }

  object LiveTimeMarker {
    def impl[F[_]: Applicative]: TimeMarker[F] = new TimeMarker[F] {
      import java.time.ZoneOffset.UTC
      override def now: F[TimeMark] = TimeMark(OffsetDateTime.now(UTC)).pure[F]
    }
  }
}
