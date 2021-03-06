package es.eriktorr.socialnet.infrastructure

import cats.data._
import cats.effect._
import doobie._
import doobie.implicits._
import doobie.implicits.javatime._
import es.eriktorr.socialnet.domain.message._
import es.eriktorr.socialnet.domain.time._
import es.eriktorr.socialnet.domain.timeline._
import es.eriktorr.socialnet.domain.user.UserName.UserType._
import es.eriktorr.socialnet.domain.user._
import es.eriktorr.socialnet.infrastructure.jdbc.NewTypeMapping

final class JdbcTimelines private (transactor: Transactor[IO])
    extends Timelines[IO]
    with NewTypeMapping {
  implicit val messageRead: Read[Message] =
    Read[(UserName[Sender], UserName[Addressee], MessageBody)].map {
      case (sender, addressee, body) => Message(sender, addressee, body)
    }

  implicit val timelineEventRead: Read[TimelineEvent] =
    Read[(TimeMark, Message)].map {
      case (timeMark, message) => TimelineEvent(timeMark, message)
    }

  override def readBy(addressees: NonEmptyList[UserName[Addressee]]): IO[TimelineEvents] =
    for {
      events <- (fr"""
        SELECT
          received_at AS markTime,
          sender,
          addressee,
          body
        FROM timeline_events
        WHERE""" ++ Fragments.in(fr"addressee", addressees)
        ++ fr"ORDER BY received_at DESC LIMIT 100")
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
          ${event.timeMark}, 
          ${event.message.sender},
          ${event.message.addressee},
          ${event.message.body}
        )
        """.update.run.transact(transactor)
    } yield ()
}

object JdbcTimelines {
  def apply(transactor: Transactor[IO]): JdbcTimelines = new JdbcTimelines(transactor)
}
