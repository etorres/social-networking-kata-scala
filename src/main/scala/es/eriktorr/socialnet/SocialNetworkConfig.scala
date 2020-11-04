package es.eriktorr.socialnet

import cats.effect._

final case class SocialNetworkConfig()

object SocialNetworkConfig {
  def from(args: List[String]): IO[SocialNetworkConfig] = IO.pure(SocialNetworkConfig())
}
