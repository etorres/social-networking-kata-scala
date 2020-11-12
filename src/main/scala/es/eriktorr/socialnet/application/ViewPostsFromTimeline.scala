package es.eriktorr.socialnet.application

import cats.data._
import cats.effect._
import es.eriktorr.socialnet.domain.timeline._
import es.eriktorr.socialnet.domain.user.UserName.UserType._
import es.eriktorr.socialnet.domain.user._

trait ViewPostsFromTimeline[F[_]] {
  def viewPostsBy(addressee: UserName[Addressee]): F[TimelineEvents]
}

object ViewPostsFromTimeline {
  def impl[F[_]: Sync](timelines: Timelines[F]): ViewPostsFromTimeline[F] =
    (addressee: UserName[Addressee]) => timelines.readBy(NonEmptyList.of(addressee))
}
