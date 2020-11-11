package es.eriktorr.socialnet

import cats.effect._
import es.eriktorr.socialnet.infrastructure.jdbc.JdbcConfiguration

final case class SocialNetworkConfig(jdbcConfiguration: JdbcConfiguration)

object SocialNetworkConfig {
  def from(args: List[String]): IO[SocialNetworkConfig] =
    for {
      jdbcConfiguration <- IO.fromEither(JdbcConfiguration.fromConfiguration)
    } yield SocialNetworkConfig(jdbcConfiguration)
}
