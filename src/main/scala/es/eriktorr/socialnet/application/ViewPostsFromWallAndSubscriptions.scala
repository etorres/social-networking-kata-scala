package es.eriktorr.socialnet.application

import cats.effect._
import es.eriktorr.socialnet.domain.subscription._
import es.eriktorr.socialnet.domain.timeline._

trait ViewPostsFromWallAndSubscriptions[F[_]] {
  def viewPostsByAll: F[TimelineEvents]
}

object ViewPostsFromWallAndSubscriptions {
  def impl[F[_]: Sync](
    timelines: Timelines[F],
    subscriptions: Subscriptions[F]
  ): ViewPostsFromWallAndSubscriptions[F] = ???
}
// TODO: all subscriptions and her own wall
