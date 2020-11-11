package es.eriktorr.socialnet.infrastructure.jdbc

import cats.implicits._
import es.eriktorr.socialnet.domain.error._
import pureconfig._
import pureconfig.generic.auto._

final case class JdbcConfiguration(
  driverClassName: String,
  connectUrl: String,
  user: String,
  password: String
)

object JdbcConfiguration {
  val postgresDriverClassName: String = "org.postgresql.Driver"

  def postgres(connectUrl: String, user: String, password: String): JdbcConfiguration =
    JdbcConfiguration(driverClassName = postgresDriverClassName, connectUrl, user, password)

  def fromConfiguration: Either[ConfigurationReadError, JdbcConfiguration] =
    ConfigSource.default.at("jdbc").load[JdbcConfiguration] match {
      case Left(error) => ConfigurationReadError(error.toString).asLeft
      case Right(config) => config.asRight
    }
}
