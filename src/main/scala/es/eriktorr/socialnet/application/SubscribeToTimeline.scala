package es.eriktorr.socialnet.application

import cats.effect._
import es.eriktorr.socialnet.domain.subscription._

trait SubscribeToTimeline[F[_]] {
  def subscribe(subscriber: Subscriber, subscription: TimelineSubscription): F[Unit]
}

object SubscribeToTimeline {
  def impl[F[_]: Sync](subscriptions: Subscriptions[F]): SubscribeToTimeline[F] =
    (subscriber: Subscriber, subscription: TimelineSubscription) =>
      subscriptions.subscribe(subscriber, subscription)
}
