package es.eriktorr.socialnet.application

import es.eriktorr.socialnet.domain.subscription._

trait SubscribeToTimeline[F[_]] {
  def subscribe(subscriber: Subscriber, subscription: TimelineSubscription): F[Unit]
}
