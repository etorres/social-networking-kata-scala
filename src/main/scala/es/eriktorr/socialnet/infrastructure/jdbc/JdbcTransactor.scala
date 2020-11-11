package es.eriktorr.socialnet.infrastructure.jdbc

import cats.effect._
import doobie.hikari._

import scala.concurrent.ExecutionContext

final class JdbcTransactor private (jdbcConfiguration: JdbcConfiguration)(
  implicit connectEc: ExecutionContext,
  blocker: Blocker,
  contextShift: ContextShift[IO]
) {
  val transactorResource: Resource[IO, HikariTransactor[IO]] =
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

object JdbcTransactor {
  def apply(jdbcConfiguration: JdbcConfiguration)(
    implicit connectEc: ExecutionContext,
    blocker: Blocker,
    contextShift: ContextShift[IO]
  ): JdbcTransactor =
    new JdbcTransactor(jdbcConfiguration)(connectEc, blocker, contextShift)
}
