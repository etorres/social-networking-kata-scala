package es.eriktorr.socialnet.infrastructure.jdbc

import cats.effect._
import doobie.hikari._

import scala.concurrent.ExecutionContext

final class PostgresTransactor private (jdbcConfiguration: JdbcConfiguration)(
  implicit connectEc: ExecutionContext,
  blocker: Blocker,
  contextShift: ContextShift[IO]
) {
  val transactor: Resource[IO, HikariTransactor[IO]] =
    for {
      xa <- HikariTransactor.newHikariTransactor[IO](
        jdbcConfiguration.driverClassName,
        jdbcConfiguration.connectUrl,
        jdbcConfiguration.user,
        jdbcConfiguration.password,
        connectEc,
        blocker
      )
    } yield xa
}

object PostgresTransactor {
  def apply(jdbcConfiguration: JdbcConfiguration)(
    implicit connectEc: ExecutionContext,
    blocker: Blocker,
    contextShift: ContextShift[IO]
  ): PostgresTransactor =
    new PostgresTransactor(jdbcConfiguration)(connectEc, blocker, contextShift)
}
