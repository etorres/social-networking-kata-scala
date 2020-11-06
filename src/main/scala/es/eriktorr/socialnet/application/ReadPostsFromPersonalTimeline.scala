package es.eriktorr.socialnet.application

import cats.effect._
import cats.implicits._
import es.eriktorr.socialnet.domain.timeline._
import es.eriktorr.socialnet.domain.user._

trait ReadPostsFromPersonalTimeline[F[_]] {
  def read(userName: UserName): F[TimelineEvents]
}

object ReadPostsFromPersonalTimeline {
  def impl[F[_]: Sync](timelines: Timelines[F]): ReadPostsFromPersonalTimeline[F] =
    (userName: UserName) => timelines.read(userName).map(identity)
}
