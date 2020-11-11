package es.eriktorr.socialnet.infrastructure.jdbc

import cats.effect._
import doobie.hikari._

import scala.concurrent.ExecutionContext

object JdbcTestTransactor {
  def testTransactorResource(jdbcConfiguration: JdbcConfiguration, currentSchema: String)(
    implicit connectEc: ExecutionContext,
    blocker: Blocker,
    contextShift: ContextShift[IO]
  ): Resource[IO, HikariTransactor[IO]] =
    for {
      transactor <- JdbcTransactor
        .apply(
          jdbcConfiguration
            .copy(connectUrl = s"${jdbcConfiguration.connectUrl}?currentSchema=$currentSchema")
        )
        .transactorResource
    } yield transactor

  def socialNetworkJdbcConfig: JdbcConfiguration =
    JdbcConfiguration.postgres(
      connectUrl = "jdbc:postgresql://localhost:5432/social_network",
      user = "postgres",
      password = "s3c4Et"
    )
}
