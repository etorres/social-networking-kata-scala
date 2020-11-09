package es.eriktorr.socialnet.infrastructure

import java.time.LocalDateTime

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
          received_at AS timeMark,
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
                  TimelineEvent(TimeMark(row.timeMark), Message(sender, addressee, body))
              }
          }
          .traverse(identity)
      )
    } yield events

  override def save(event: TimelineEvent): IO[Unit] = ???

  // INSERT INTO "exposed" ("exposed_id", "key", "received_at", "rolling_start_number", "rolling_period", "transmission_risk_level") VALUES (1, 'f79211eaadc10242ac120002', '2020-09-15 20:35:09.65198', 2655072, 1209600000, 3);
}

object JdbcTimelines {
  def apply(transactor: Transactor[IO]): JdbcTimelines = new JdbcTimelines(transactor)

  final case class JdbcTimelineEvent(
    timeMark: LocalDateTime,
    sender: String,
    addressee: String,
    body: String
  )
}
