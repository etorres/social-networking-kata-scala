package es.eriktorr.socialnet.infrastructure

import cats.effect._
import doobie._
import doobie.implicits._
import doobie.implicits.javatime._
import es.eriktorr.socialnet.domain.message._
import es.eriktorr.socialnet.domain.time._
import es.eriktorr.socialnet.domain.timeline._
import es.eriktorr.socialnet.domain.user._
import es.eriktorr.socialnet.infrastructure.jdbc.NewTypeMapping

final class JdbcTimelines private (transactor: Transactor[IO])
    extends Timelines[IO]
    with NewTypeMapping {
  implicit val messageRead: Read[Message] =
    Read[(UserName, UserName, MessageBody)].map {
      case (sender, addressee, body) => Message(sender, addressee, body)
    }

  implicit val timelineEventRead: Read[TimelineEvent] =
    Read[(TimeMark, Message)].map {
      case (timeMark, message) => TimelineEvent(timeMark, message)
    }

  override def readBy(userNames: UserName*): IO[TimelineEvents] =
    for {
      events <- sql"""
        SELECT
          received_at AS markTime,
          sender,
          addressee,
          body
        FROM timeline_events
        WHERE
          addressee IN (${userNames.mkString(",")})
        ORDER BY received_at DESC
        LIMIT 100"""
        .query[TimelineEvent]
        .to[List]
        .transact(transactor)
    } yield events

  override def save(event: TimelineEvent): IO[Unit] =
    for {
      _ <- sql"""
        INSERT INTO 
          timeline_events (received_at, sender, addressee, body) 
        VALUES (
          ${event.timeMark.unTimeMark}, 
          ${event.message.sender.unUserName},
          ${event.message.addressee.unUserName},
          ${event.message.body.unBody}
        )
        """.update.run.transact(transactor)
    } yield ()
}

object JdbcTimelines {
  def apply(transactor: Transactor[IO]): JdbcTimelines = new JdbcTimelines(transactor)
}
