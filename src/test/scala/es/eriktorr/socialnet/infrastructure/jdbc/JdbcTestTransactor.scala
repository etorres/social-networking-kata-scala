package es.eriktorr.socialnet.infrastructure.jdbc

import cats.effect._
import cats.implicits._
import doobie._
import doobie.hikari._
import doobie.implicits._

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
      _ <- truncateAllTablesIn(transactor, currentSchema)
    } yield transactor

  private[this] def truncateAllTablesIn(
    transactor: Transactor[IO],
    currentSchema: String
  ): Resource[IO, Unit] =
    Resource.make {
      (for {
        tableNames <- sql"""
          SELECT table_name
          FROM information_schema.tables
          WHERE table_schema = $currentSchema
          ORDER BY table_name""".query[String].to[List]
        _ <- tableNames
          .map(tableName => Fragment.const(s"truncate table $tableName"))
          .traverse_(_.update.run)
      } yield ()).transact(transactor)
    }(_ => IO.unit)

  def socialNetworkJdbcConfig: JdbcConfiguration =
    JdbcConfiguration.postgres(
      connectUrl = "jdbc:postgresql://localhost:5432/social_network",
      user = "postgres",
      password = "s3c4Et"
    )
}
