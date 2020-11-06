package es.eriktorr.socialnet.application

import cats.effect._
import es.eriktorr.socialnet.domain.subscription._
import es.eriktorr.socialnet.domain.timeline._

trait ViewPostsFromAllSubscriptions[F[_]] {
  def viewPostsByAll: F[TimelineEvents]
}

object ViewPostsFromAllSubscriptions {
  def impl[F[_]: Sync](
    timelines: Timelines[F],
    subscriptions: Subscriptions[F]
  ): ViewPostsFromAllSubscriptions[F] = ???
}
