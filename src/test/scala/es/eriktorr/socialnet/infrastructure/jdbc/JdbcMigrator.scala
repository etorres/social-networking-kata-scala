package es.eriktorr.socialnet.infrastructure.jdbc

import cats.effect._
import org.flywaydb.core.Flyway

trait JdbcMigrator[F[_]] {
  def migrate(): F[Unit]
}

object JdbcMigrator {
  @SuppressWarnings(Array("org.wartremover.warts.NonUnitStatements"))
  def impl[F[_]: Sync: ContextShift](flyway: Flyway)(implicit blocker: Blocker): JdbcMigrator[F] =
    () =>
      blocker.delay {
        flyway.clean()
        flyway.baseline()
        flyway.migrate()
      }

  def migratorResource[F[_]: Sync: ContextShift](
    jdbcConfiguration: JdbcConfiguration,
    currentSchema: String
  )(implicit blocker: Blocker): Resource[F, JdbcMigrator[F]] = {
    val F = Sync[F]
    Resource.make(F.delay {
      impl {
        Flyway
          .configure()
          .dataSource(
            s"${jdbcConfiguration.connectUrl}?currentSchema=$currentSchema",
            jdbcConfiguration.user,
            jdbcConfiguration.password
          )
          .defaultSchema(currentSchema)
          .schemas(currentSchema)
          .load()
      }
    })(_ => F.unit)
  }
}
