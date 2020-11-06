package es.eriktorr.socialnet.application

import cats.effect._
import cats.implicits._
import es.eriktorr.socialnet.domain.timeline._
import es.eriktorr.socialnet.domain.user._

trait ViewPostsFromTimeline[F[_]] {
  def viewPostsBy(userName: UserName): F[TimelineEvents]
}

object ViewPostsFromTimeline {
  def impl[F[_]: Sync](timelines: Timelines[F]): ViewPostsFromTimeline[F] =
    (userName: UserName) => timelines.readBy(userName).map(identity)
}
