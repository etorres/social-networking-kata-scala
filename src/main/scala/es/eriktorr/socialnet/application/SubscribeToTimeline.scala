package es.eriktorr.socialnet.application

import cats.effect._
import es.eriktorr.socialnet.domain.subscription._
import es.eriktorr.socialnet.domain.user._
import es.eriktorr.socialnet.domain.user.UserName.UserType._

trait SubscribeToTimeline[F[_]] {
  def subscribe(follower: UserName[Follower], followee: UserName[Followee]): F[Unit]
}

object SubscribeToTimeline {
  def impl[F[_]: Sync](subscriptions: Subscriptions[F]): SubscribeToTimeline[F] =
    (follower: UserName[Follower], followee: UserName[Followee]) =>
      subscriptions.follow(follower, followee)
}
