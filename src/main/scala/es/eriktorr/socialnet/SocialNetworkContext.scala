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
import es.eriktorr.socialnet.infrastructure.jdbc.JdbcTransactor
import es.eriktorr.socialnet.infrastructure.{JdbcSubscriptions, JdbcTimelines}

import scala.concurrent.ExecutionContext

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
  def impl(config: SocialNetworkConfig)(
    implicit connectEc: ExecutionContext,
    blocker: Blocker,
    contextShift: ContextShift[IO]
  ): Resource[IO, SocialNetworkContext] =
    for {
      transactor <- JdbcTransactor(config.jdbcConfiguration).transactorResource
      context = new SocialNetworkContext(
        JdbcSubscriptions(transactor),
        LiveTimeMarker.impl[IO],
        JdbcTimelines(transactor)
      )
    } yield context
}
