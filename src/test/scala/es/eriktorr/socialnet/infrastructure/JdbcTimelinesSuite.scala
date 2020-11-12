package es.eriktorr.socialnet.infrastructure

import cats._
import cats.data._
import cats.derived._
import cats.implicits._
import es.eriktorr.socialnet.domain.message._
import es.eriktorr.socialnet.domain.timeline._
import es.eriktorr.socialnet.domain.user.UserName.UserType._
import es.eriktorr.socialnet.domain.user._
import es.eriktorr.socialnet.shared.infrastructure.GeneratorSyntax._
import es.eriktorr.socialnet.shared.infrastructure.SocialNetworkGenerators.{messageGen, userNameGen}
import es.eriktorr.socialnet.shared.infrastructure.TimeGenerators.timeMarkGen
import es.eriktorr.socialnet.spec.JdbcIOSuiteWithCheckers
import org.scalacheck._
import org.scalacheck.cats.implicits._

object JdbcTimelinesSuite extends JdbcIOSuiteWithCheckers {
  override def currentSchema: String = "test_timelines"

  simpleTest("Write and read messages from database") {
    final case class TestCase(
      addressee: UserName[Addressee],
      allEvents: TimelineEvents,
      expectedEvents: TimelineEvents
    )

    object TestCase {
      implicit val showTestCase: Show[TestCase] = semiauto.show
    }

    val gen = (for {
      (targetUserName, otherUserNames) <- (
        userNameGen[Addressee],
        Gen.containerOfN[List, UserName[Sender]](3, userNameGen[Sender])
      ).tupled
      allUserNames = targetUserName.asUserName[Sender] :: otherUserNames
      (targetMessages, otherMessages) <- (
        Gen.containerOfN[List, Message](
          3,
          messageGen(
            senderGen = Gen.oneOf(allUserNames),
            addresseeGen = Gen.const(targetUserName)
          )
        ),
        Gen.containerOfN[List, Message](
          5,
          messageGen(
            senderGen = Gen.oneOf(allUserNames),
            addresseeGen = Gen.oneOf(otherUserNames.map(_.asUserName[Addressee]))
          )
        )
      ).tupled
      (targetEvents, otherEvents) <- (targetMessages.traverse { message =>
        timeMarkGen.map(timeMark => TimelineEvent(timeMark, message))
      }, otherMessages.traverse { message =>
        timeMarkGen.map(timeMark => TimelineEvent(timeMark, message))
      }).tupled
    } yield TestCase(
      targetUserName,
      (targetEvents ++ otherEvents).sortWith(_ isBefore _),
      targetEvents.sortWith(_ isAfter _)
    )).sampleWithSeed("JdbcTimelinesSuite")

    forall(gen) {
      case TestCase(addressee, allEvents, expectedEvents) =>
        testResources.use {
          case (migrator, transactor) =>
            val timelines = JdbcTimelines(transactor)
            for {
              _ <- migrator.migrate() *> allEvents.traverse_(timelines.save)
              events <- timelines.readBy(NonEmptyList.of(addressee))
            } yield expect(events == expectedEvents)
        }
    }
  }
}
