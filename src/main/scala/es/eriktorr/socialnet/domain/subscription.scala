package es.eriktorr.socialnet.domain

import es.eriktorr.socialnet.domain.user.UserName.UserType._
import es.eriktorr.socialnet.domain.user._

object subscription {
  type Followees = List[UserName[Followee]]
  type FolloweesPerUser = Map[UserName[Follower], Followees]

  trait Subscriptions[F[_]] {
    def follow(follower: UserName[Follower], followee: UserName[Followee]): F[Unit]
    def followeesOf(follower: UserName[Follower]): F[Followees]
  }
}
