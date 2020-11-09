package es.eriktorr.socialnet.infrastructure.jdbc

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
}
