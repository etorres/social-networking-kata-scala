package es.eriktorr.socialnet.domain

import es.eriktorr.socialnet.domain.user._
import io.estatico.newtype.macros.newtype

object subscription {
  @newtype case class Subscriber(unSubscriber: UserName)
  @newtype case class TimelineSubscription(unTimelineSubscription: UserName)

  type TimelineSubscriptions = List[TimelineSubscription]

  trait Subscriptions[F[_]] {
    def subscribe(subscriber: Subscriber, subscription: TimelineSubscription): F[Unit]
    def subscriptionsOf(subscriber: Subscriber): F[TimelineSubscriptions]
  }
}
