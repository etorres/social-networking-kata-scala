package es.eriktorr.socialnet.application

import cats.effect._
import cats.implicits._
import es.eriktorr.socialnet.domain.message._
import es.eriktorr.socialnet.domain.time._
import es.eriktorr.socialnet.domain.timeline._

trait PostMessageToPersonalTimeline[F[_]] {
  def post(message: Message): F[Unit]
}

object PostMessageToPersonalTimeline {
  def impl[F[_]: Sync](
    timeMarker: TimeMarker[F],
    timelines: Timelines[F]
  ): PostMessageToPersonalTimeline[F] =
    (message: Message) =>
      for {
        timeMark <- timeMarker.now
        _ <- timelines.save(TimelineEvent(timeMark, message))
      } yield ()
}
