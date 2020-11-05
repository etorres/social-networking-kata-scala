package es.eriktorr.socialnet.domain.infrastructure

import cats.effect._
import cats.effect.concurrent.Ref
import cats.implicits._
import es.eriktorr.socialnet.domain.timeline._

final case class TimelinesState(events: List[TimelineEvent])

object TimelinesState {
  def empty: TimelinesState = TimelinesState(events = List.empty)
}

final class FakeTimelines[F[_]: Sync] private[infrastructure] (val ref: Ref[F, TimelinesState])
    extends Timelines[F] {
  override def save(event: TimelineEvent): F[Unit] =
    ref.get.flatMap(current => ref.set(current.copy(event :: current.events)))
}

object FakeTimelines {
  def impl[F[_]: Sync](ref: Ref[F, TimelinesState]): Timelines[F] = new FakeTimelines[F](ref)
}
