package es.eriktorr.socialnet.infrastructure

import cats._
import cats.derived._
import cats.implicits._
import es.eriktorr.socialnet.domain.subscription._
import es.eriktorr.socialnet.shared.infrastructure.GeneratorSyntax._
import es.eriktorr.socialnet.shared.infrastructure.SocialNetworkGenerators.userNameGen
import es.eriktorr.socialnet.spec.JdbcIOSuiteWithCheckers
import org.scalacheck._

object JdbcSubscriptionsSuite extends JdbcIOSuiteWithCheckers {
  simpleTest("Write and read subscriptions from database") {
    final case class TestCase(subscriber: Subscriber, subscriptions: TimelineSubscriptions)

    object TestCase {
      implicit val showTestCase: Show[TestCase] = semiauto.show
    }

    val gen = (for {
      subscriber <- userNameGen.map(Subscriber(_))
      subscriptions <- Gen.containerOfN[List, TimelineSubscription](
        3,
        userNameGen.map(TimelineSubscription(_))
      )
    } yield TestCase(subscriber, subscriptions)).sampleWithSeed("JdbcSubscriptionsSuite")

    forall(gen) {
      case TestCase(subscriber, subscriptions) =>
        testResources.use { transactor =>
          val subscriptionsRepository = JdbcSubscriptions(transactor)
          for {
            _ <- subscriptions.traverse_(subscriptionsRepository.subscribe(subscriber, _))
            actual <- subscriptionsRepository.subscriptionsOf(subscriber)
          } yield expect(
            actual.sortWith(_ < _) == subscriptions.sortWith(_ < _)
          )
        }
    }
  }
}
