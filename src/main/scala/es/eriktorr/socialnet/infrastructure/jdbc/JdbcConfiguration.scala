package es.eriktorr.socialnet.infrastructure.jdbc

final case class JdbcConfiguration(
  driverClassName: String,
  connectUrl: String,
  user: String,
  password: String
)
