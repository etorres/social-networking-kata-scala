package es.eriktorr.socialnet.domain

import java.time.LocalDateTime

import cats._
import cats.implicits._

object time {
  type TimeMark = LocalDateTime

  trait TimeMarker[F[_]] {
    def now: F[TimeMark]
  }

  object LiveTimeMarker {
    def impl[F[_]: Applicative]: TimeMarker[F] = new TimeMarker[F] {
      override def now: F[LocalDateTime] = LocalDateTime.now().pure[F]
    }
  }
}
