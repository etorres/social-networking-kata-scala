package es.eriktorr.socialnet.infrastructure

import cats._
import cats.derived._
import cats.effect._
import cats.effect.concurrent.Ref
import cats.implicits._
import es.eriktorr.socialnet.domain.subscription._

final case class SubscriptionsState(subscriptions: UsersSubscriptions)

object SubscriptionsState {
  implicit val eqSubscriptionsState: Eq[SubscriptionsState] = semiauto.eq
}

final class FakeSubscriptions[F[_]: Sync] private[infrastructure] (
  val ref: Ref[F, SubscriptionsState]
) extends Subscriptions[F] {
  override def subscribe(subscriber: Subscriber, subscription: TimelineSubscription): F[Unit] =
    ref.get.flatMap(current =>
      ref.set {
        current.copy(
          current.subscriptions + (subscriber -> (subscription :: current.subscriptions
            .getOrElse(subscriber, List.empty)))
        )
      }
    )

  override def subscriptionsOf(subscriber: Subscriber): F[TimelineSubscriptions] =
    ref.get.map(_.subscriptions.getOrElse(subscriber, List.empty))
}

object FakeSubscriptions {
  def impl[F[_]: Sync](ref: Ref[F, SubscriptionsState]): Subscriptions[F] =
    new FakeSubscriptions[F](ref)
}
