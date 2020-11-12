package es.eriktorr.socialnet.infrastructure

import cats._
import cats.derived._
import cats.effect._
import cats.effect.concurrent.Ref
import cats.implicits._
import es.eriktorr.socialnet.domain.subscription._
import es.eriktorr.socialnet.domain.user._
import es.eriktorr.socialnet.domain.user.UserName.UserType._

final case class SubscriptionsState(subscriptions: FolloweesPerUser)

object SubscriptionsState {
  implicit val eqSubscriptionsState: Eq[SubscriptionsState] = semiauto.eq
}

final class FakeSubscriptions[F[_]: Sync] private[infrastructure] (
  val ref: Ref[F, SubscriptionsState]
) extends Subscriptions[F] {
  override def follow(follower: UserName[Follower], followee: UserName[Followee]): F[Unit] =
    ref.get.flatMap(current =>
      ref.set {
        current.copy(
          current.subscriptions + (follower -> (followee :: current.subscriptions
            .getOrElse(follower, List.empty)))
        )
      }
    )

  override def followeesOf(follower: UserName[Follower]): F[Followees] =
    ref.get.map(_.subscriptions.getOrElse(follower, List.empty))
}

object FakeSubscriptions {
  def impl[F[_]: Sync](ref: Ref[F, SubscriptionsState]): Subscriptions[F] =
    new FakeSubscriptions[F](ref)
}
