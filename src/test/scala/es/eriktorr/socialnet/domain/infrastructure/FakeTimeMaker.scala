package es.eriktorr.socialnet.domain.infrastructure

import java.time.LocalDateTime

import cats._
import cats.effect._
import es.eriktorr.socialnet.domain.time._

object FakeTimeMaker {
  def impl[F[_]: Sync]: TimeMarker[F] = new TimeMarker[F] {
    private[this] val F = Monad[F]
    override def now: F[TimeMark] = F.pure(LocalDateTime.of(2020, 9, 18, 23, 3, 17))
  }
}
