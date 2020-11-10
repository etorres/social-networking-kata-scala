package es.eriktorr.socialnet.infrastructure

import java.time.{OffsetDateTime, ZoneOffset}

import cats.effect._
import cats.implicits._
import doobie._
import doobie.implicits._
import doobie.implicits.javatime._
import es.eriktorr.socialnet.domain.message._
import es.eriktorr.socialnet.domain.time._
import es.eriktorr.socialnet.domain.timeline._
import es.eriktorr.socialnet.domain.user._

final class JdbcTimelines private (transactor: Transactor[IO]) extends Timelines[IO] {
  override def readBy(userNames: UserName*): IO[TimelineEvents] =
    for {
      rows <- sql"""
        SELECT
          received_at AS receivedAt,
          sender,
          addressee,
          body
        FROM timeline_events
        WHERE
          addressee IN (${userNames.mkString(",")})
        ORDER BY received_at DESC
        LIMIT 100"""
        .query[JdbcTimelines.JdbcTimelineEvent]
        .to[List]
        .transact(transactor)
      events <- IO.fromEither(
        rows
          .map { row =>
            (
              UserName.fromString(row.sender),
              UserName.fromString(row.addressee),
              MessageBody.fromString(row.body)
            ).tupled
              .map {
                case (sender, addressee, body) =>
                  TimelineEvent(
                    TimeMark(row.receivedAt.withOffsetSameLocal(ZoneOffset.UTC).toLocalDateTime),
                    Message(sender, addressee, body)
                  )
              }
          }
          .traverse(identity)
      )
    } yield events

  override def save(event: TimelineEvent): IO[Unit] =
    for {
      _ <- sql"""
        INSERT INTO 
          timeline_events (received_at, sender, addressee, body) 
        VALUES (
          ${event.timeMark.unTimeMark.atOffset(ZoneOffset.UTC)}, 
          ${event.message.sender.unUserName},
          ${event.message.addressee.unUserName},
          ${event.message.body.unBody}
        )
        """.update.run.transact(transactor)
    } yield ()
}

object JdbcTimelines {
  def apply(transactor: Transactor[IO]): JdbcTimelines = new JdbcTimelines(transactor)

  final case class JdbcTimelineEvent(
    receivedAt: OffsetDateTime,
    sender: String,
    addressee: String,
    body: String
  )
}
