package es.eriktorr.socialnet.infrastructure

import cats.effect._
import doobie._
import doobie.implicits._
import es.eriktorr.socialnet.domain.subscription._
import io.estatico.newtype._

final class JdbcSubscriptions private (transactor: Transactor[IO]) extends Subscriptions[IO] {
  implicit def newTypePut[R, N](implicit ev: Coercible[Put[R], Put[N]], R: Put[R]): Put[N] = ev(R)
  implicit def newTypeRead[R, N](implicit ev: Coercible[Read[R], Read[N]], R: Read[R]): Read[N] =
    ev(R)

  override def subscribe(subscriber: Subscriber, subscription: TimelineSubscription): IO[Unit] =
    for {
      _ <- sql"""
        INSERT INTO
          subscriptions (subscriber, subscription)
          VALUES ($subscriber, $subscription)
          """.update.run.transact(transactor)
    } yield ()

  override def subscriptionsOf(subscriber: Subscriber): IO[TimelineSubscriptions] =
    for {
      subscriptions <- sql"""
        SELECT 
          subscription
        FROM subscriptions
        WHERE 
          subscriber = $subscriber"""
        .query[TimelineSubscription]
        .to[List]
        .transact(transactor)
    } yield subscriptions
}

object JdbcSubscriptions {
  def apply(transactor: Transactor[IO]): JdbcSubscriptions = new JdbcSubscriptions(transactor)
}
