package es.eriktorr.socialnet.application

import cats.effect._
import cats.implicits._
import es.eriktorr.socialnet.domain.subscription._
import es.eriktorr.socialnet.domain.timeline._
import es.eriktorr.socialnet.domain.user._

trait ViewPostsFromWallAndSubscriptions[F[_]] {
  def viewAllPostsFor(userName: UserName): F[TimelineEvents]
}

object ViewPostsFromWallAndSubscriptions {
  def impl[F[_]: Sync](
    timelines: Timelines[F],
    subscriptions: Subscriptions[F]
  ): ViewPostsFromWallAndSubscriptions[F] =
    (userName: UserName) =>
      for {
        userSubscriptions <- subscriptions.subscriptionsOf(Subscriber(userName))
        timelineEvents <- timelines.readBy(
          userName :: userSubscriptions.map(_.unTimelineSubscription): _*
        )
      } yield timelineEvents
}
