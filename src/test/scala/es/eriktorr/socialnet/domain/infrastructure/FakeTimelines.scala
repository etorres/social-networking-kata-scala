package es.eriktorr.socialnet.domain.infrastructure

import cats._
import cats.derived._
import cats.effect._
import cats.effect.concurrent.Ref
import cats.implicits._
import es.eriktorr.socialnet.domain.timeline._
import es.eriktorr.socialnet.domain.user._

final case class TimelinesState(events: TimelineEvents)

object TimelinesState {
  implicit val eqTimelinesState: Eq[TimelinesState] = semiauto.eq
}

final class FakeTimelines[F[_]: Sync] private[infrastructure] (val ref: Ref[F, TimelinesState])
    extends Timelines[F] {
  override def readBy(userNames: UserName*): F[TimelineEvents] =
    ref.get.map(_.events.filter(e => userNames.toList.contains_(e.message.from)))

  override def save(event: TimelineEvent): F[Unit] =
    ref.get.flatMap(current => ref.set(current.copy(event :: current.events)))
}

object FakeTimelines {
  def impl[F[_]: Sync](ref: Ref[F, TimelinesState]): Timelines[F] = new FakeTimelines[F](ref)
}
