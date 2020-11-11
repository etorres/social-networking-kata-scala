package es.eriktorr.socialnet.spec

import cats.effect._
import es.eriktorr.socialnet.infrastructure.jdbc.JdbcConfiguration
import org.flywaydb.core.Flyway

trait JdbcMigrator[F[_]] {
  def migrate(): F[Unit]
}

object JdbcMigrator {
  @SuppressWarnings(Array("org.wartremover.warts.NonUnitStatements"))
  def impl[F[_]: Sync: ContextShift](flyway: Flyway)(implicit blocker: Blocker): JdbcMigrator[F] =
    () => blocker.delay(flyway.migrate())

  def flywayResource[F[_]: Sync: ContextShift](
    jdbcConfiguration: JdbcConfiguration
  )(implicit blocker: Blocker): Resource[F, JdbcMigrator[F]] = {
    val F = Sync[F]
    Resource.make(F.delay {
      impl {
        Flyway
          .configure()
          .dataSource(
            jdbcConfiguration.connectUrl,
            jdbcConfiguration.user,
            jdbcConfiguration.password
          )
          .load()
      }
    })(_ => F.unit)
  }
}
