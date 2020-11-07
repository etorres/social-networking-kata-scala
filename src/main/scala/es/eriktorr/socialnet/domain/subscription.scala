package es.eriktorr.socialnet.domain

import cats._
import es.eriktorr.socialnet.domain.user._
import io.estatico.newtype.macros.newtype

object subscription {
  @newtype case class Subscriber(unSubscriber: UserName)
  @newtype case class TimelineSubscription(unTimelineSubscription: UserName)

  object Subscriber {
    implicit val eqSubscriber: Eq[Subscriber] = Eq.fromUniversalEquals
    implicit val showSubscriber: Show[Subscriber] = Show.show(_.toString)
  }

  object TimelineSubscription {
    implicit val eqTimelineSubscription: Eq[TimelineSubscription] = Eq.fromUniversalEquals
    implicit val showTimelineSubscription: Show[TimelineSubscription] = Show.show(_.toString)
  }

  type TimelineSubscriptions = List[TimelineSubscription]

  trait Subscriptions[F[_]] {
    def subscribe(subscriber: Subscriber, subscription: TimelineSubscription): F[Unit]
    def subscriptionsOf(subscriber: Subscriber): F[TimelineSubscriptions]
  }
}
