package es.eriktorr.socialnet.infrastructure

import cats.effect._
import doobie._
import doobie.implicits._
import es.eriktorr.socialnet.domain.subscription._
import es.eriktorr.socialnet.domain.user._
import es.eriktorr.socialnet.domain.user.UserName.UserType._
import es.eriktorr.socialnet.infrastructure.jdbc.NewTypeMapping

final class JdbcSubscriptions private (transactor: Transactor[IO])
    extends Subscriptions[IO]
    with NewTypeMapping {
  override def follow(follower: UserName[Follower], followee: UserName[Followee]): IO[Unit] =
    for {
      _ <- sql"""
        INSERT INTO
          subscriptions (subscriber, subscription)
          VALUES ($follower, $followee)
          """.update.run.transact(transactor)
    } yield ()

  override def followeesOf(follower: UserName[Follower]): IO[Followees] =
    for {
      subscriptions <- sql"""
        SELECT 
          subscription
        FROM subscriptions
        WHERE 
          subscriber = $follower"""
        .query[UserName[Followee]]
        .to[List]
        .transact(transactor)
    } yield subscriptions
}

object JdbcSubscriptions {
  def apply(transactor: Transactor[IO]): JdbcSubscriptions = new JdbcSubscriptions(transactor)
}
