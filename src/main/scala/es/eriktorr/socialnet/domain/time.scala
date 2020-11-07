package es.eriktorr.socialnet.domain

import java.time.LocalDateTime

import cats._
import cats.implicits._
import io.estatico.newtype.macros.newtype

object time {
  @newtype case class TimeMark(unTimeMark: LocalDateTime)

  object TimeMark {
    implicit class TimeMarkOps(self: TimeMark) {
      def isAfter(other: TimeMark): Boolean = self.unTimeMark.isAfter(other.unTimeMark)
      def plusMinutes(minutes: Long): TimeMark = TimeMark(self.unTimeMark.plusMinutes(minutes))
    }

    implicit val eqTimeMark: Eq[TimeMark] = Eq.fromUniversalEquals
    implicit val showTimeMark: Show[TimeMark] = Show.show(_.toString)
  }

  trait TimeMarker[F[_]] {
    def now: F[TimeMark]
  }

  object LiveTimeMarker {
    def impl[F[_]: Applicative]: TimeMarker[F] = new TimeMarker[F] {
      override def now: F[TimeMark] = TimeMark(LocalDateTime.now()).pure[F]
    }
  }
}
