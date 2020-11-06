package es.eriktorr.socialnet.domain.infrastructure

import cats.effect._
import cats.effect.concurrent.Ref
import cats.implicits._
import es.eriktorr.socialnet.domain.timeline._
import es.eriktorr.socialnet.domain.user._

final case class TimelinesState(events: List[TimelineEvent])

final class FakeTimelines[F[_]: Sync] private[infrastructure] (val ref: Ref[F, TimelinesState])
    extends Timelines[F] {
  override def readBy(userName: UserName): F[TimelineEvents] =
    ref.get.map(_.events.filter(_.message.from === userName))

  override def save(event: TimelineEvent): F[Unit] =
    ref.get.flatMap(current => ref.set(current.copy(event :: current.events)))
}

object FakeTimelines {
  def impl[F[_]: Sync](ref: Ref[F, TimelinesState]): Timelines[F] = new FakeTimelines[F](ref)
}
