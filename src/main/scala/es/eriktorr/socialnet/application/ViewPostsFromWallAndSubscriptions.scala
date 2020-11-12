package es.eriktorr.socialnet.application

import cats.data._
import cats.effect._
import cats.implicits._
import es.eriktorr.socialnet.domain.subscription._
import es.eriktorr.socialnet.domain.timeline._
import es.eriktorr.socialnet.domain.user.UserName.UserType._
import es.eriktorr.socialnet.domain.user._

trait ViewPostsFromWallAndSubscriptions[F[_]] {
  def viewAllPostsFor(addressee: UserName[Addressee]): F[TimelineEvents]
}

object ViewPostsFromWallAndSubscriptions {
  def impl[F[_]: Sync](
    timelines: Timelines[F],
    subscriptions: Subscriptions[F]
  ): ViewPostsFromWallAndSubscriptions[F] =
    (addressee: UserName[Addressee]) =>
      for {
        followees <- subscriptions.followeesOf(addressee.asUserName[Follower])
        timelineEvents <- timelines.readBy(
          NonEmptyList.ofInitLast(followees.map(_.asUserName[Addressee]), addressee)
        )
      } yield timelineEvents
}
