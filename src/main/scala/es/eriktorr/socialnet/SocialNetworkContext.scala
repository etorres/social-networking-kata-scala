package es.eriktorr.socialnet

import cats.effect._
import es.eriktorr.socialnet.application.{
  PostMessageToTimeline,
  SubscribeToTimeline,
  ViewPostsFromTimeline,
  ViewPostsFromWallAndSubscriptions
}
import es.eriktorr.socialnet.domain.subscription.Subscriptions
import es.eriktorr.socialnet.domain.time._
import es.eriktorr.socialnet.domain.timeline._
import es.eriktorr.socialnet.effect._

final class SocialNetworkContext(
  subscriptions: Subscriptions[IO],
  timeMarker: TimeMarker[IO],
  timelines: Timelines[IO]
) {
  def postMessageToPersonalTimeline: PostMessageToTimeline[IO] =
    PostMessageToTimeline.impl[IO](timeMarker, timelines)
  def subscribeToTimeline: SubscribeToTimeline[IO] = SubscribeToTimeline.impl[IO](subscriptions)
  def viewPostsFromAllSubscriptions: ViewPostsFromWallAndSubscriptions[IO] =
    ViewPostsFromWallAndSubscriptions.impl[IO](timelines, subscriptions)
  def viewPostsFromTimeline: ViewPostsFromTimeline[IO] = ViewPostsFromTimeline.impl[IO](timelines)
}

object SocialNetworkContext {
  def impl(config: SocialNetworkConfig): Resource[IO, SocialNetworkContext] =
    new SocialNetworkContext(???, LiveTimeMarker.impl[IO], ???).asResource
}
