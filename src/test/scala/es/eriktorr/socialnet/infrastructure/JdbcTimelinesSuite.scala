package es.eriktorr.socialnet.infrastructure

import java.time.LocalDateTime

import cats._
import cats.derived._
import cats.effect._
import cats.implicits._
import es.eriktorr.socialnet.domain.message._
import es.eriktorr.socialnet.domain.time._
import es.eriktorr.socialnet.domain.timeline._
import es.eriktorr.socialnet.domain.user._
import es.eriktorr.socialnet.infrastructure.jdbc.JdbcTestTransactor
import es.eriktorr.socialnet.shared.infrastructure.GeneratorSyntax._
import es.eriktorr.socialnet.shared.infrastructure.SocialNetworkGenerators.{messageGen, userNameGen}
import es.eriktorr.socialnet.shared.infrastructure.TimeGenerators._
import org.scalacheck._
import org.scalacheck.cats.implicits._
import weaver._
import weaver.scalacheck._

import scala.concurrent.ExecutionContextExecutor

object JdbcTimelinesSuite extends SimpleIOSuite with IOCheckers {
  implicit val evEc: ExecutionContextExecutor = ec
  implicit val evBlocker: Blocker = Blocker.liftExecutionContext(ec)

  override def maxParallelism: Int = 1
  override def checkConfig: CheckConfig =
    super.checkConfig.copy(minimumSuccessful = 10, perPropertyParallelism = 1)

  simpleTest("Write and read messages from database") {
    final case class TestCase(
      userName: UserName,
      allEvents: TimelineEvents,
      expectedEvents: TimelineEvents
    )

    object TestCase {
      implicit val showTestCase: Show[TestCase] = semiauto.show
    }

    val timeMarkGen = Arbitrary.arbitrary[LocalDateTime].map(a => TimeMark(a))

    val gen = (for {
      (targetUserName, otherUserNames) <- (
        userNameGen,
        Gen.containerOfN[List, UserName](3, userNameGen)
      ).tupled
      allUserNames = targetUserName :: otherUserNames
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
            addresseeGen = Gen.oneOf(otherUserNames)
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

    val testResources =
      JdbcTestTransactor.testTransactorResource(JdbcTestTransactor.socialNetworkJdbcConfig)

    forall(gen) {
      case TestCase(userName, allEvents, expectedEvents) =>
        testResources.use { transactor =>
          val timelines = JdbcTimelines(transactor)
          for {
            _ <- allEvents.traverse_(timelines.save)
            events <- timelines.readBy(userName)
          } yield expect(events == expectedEvents)
        }
    }
  }
}
