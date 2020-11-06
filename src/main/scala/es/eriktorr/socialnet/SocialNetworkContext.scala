package es.eriktorr.socialnet

import cats.effect._
import es.eriktorr.socialnet.application.PostMessageToTimeline
import es.eriktorr.socialnet.domain.time._
import es.eriktorr.socialnet.domain.timeline._
import es.eriktorr.socialnet.effect._

final class SocialNetworkContext(timeMarker: TimeMarker[IO], timelines: Timelines[IO]) {
  def postMessageToPersonalTimeline: PostMessageToTimeline[IO] =
    PostMessageToTimeline.impl[IO](timeMarker, timelines)
}

object SocialNetworkContext {
  def impl(config: SocialNetworkConfig): Resource[IO, SocialNetworkContext] =
    new SocialNetworkContext(LiveTimeMarker.impl[IO], ???).asResource
}
