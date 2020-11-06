package es.eriktorr.socialnet.domain.infrastructure

import cats._
import cats.effect._
import cats.effect.concurrent.Ref
import cats.implicits._
import es.eriktorr.socialnet.domain.time._

final case class TimeMarkerState(initialMark: TimeMark, marks: List[TimeMark])

object TimeMarkerState {
  def startingAt(initialMark: TimeMark): TimeMarkerState = TimeMarkerState(initialMark, List.empty)
}

final class FakeTimeMarker[F[_]: Sync] private[infrastructure] (val ref: Ref[F, TimeMarkerState])
    extends TimeMarker[F] {
  override def now: F[TimeMark] = ref.get.flatMap { current =>
    val mark = current.marks.headOption.getOrElse(current.initialMark).plusMinutes(1)
    ref.set(current.copy(marks = mark :: current.marks)) *> Monad[F].pure(mark)
  }
}

object FakeTimeMarker {
  def impl[F[_]: Sync](ref: Ref[F, TimeMarkerState]): TimeMarker[F] = new FakeTimeMarker[F](ref)
}
