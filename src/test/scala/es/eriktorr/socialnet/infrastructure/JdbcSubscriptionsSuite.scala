package es.eriktorr.socialnet.infrastructure

import cats._
import cats.derived._
import cats.implicits._
import es.eriktorr.socialnet.domain.subscription._
import es.eriktorr.socialnet.domain.user._
import es.eriktorr.socialnet.domain.user.UserName.UserType._
import es.eriktorr.socialnet.shared.infrastructure.GeneratorSyntax._
import es.eriktorr.socialnet.shared.infrastructure.SocialNetworkGenerators.userNameGen
import es.eriktorr.socialnet.spec.JdbcIOSuiteWithCheckers
import org.scalacheck._

object JdbcSubscriptionsSuite extends JdbcIOSuiteWithCheckers {
  override def currentSchema: String = "test_subscriptions"

  simpleTest("Write and read subscriptions from database") {
    final case class TestCase(follower: UserName[Follower], followees: Followees)

    object TestCase {
      implicit val showTestCase: Show[TestCase] = semiauto.show
    }

    val gen = (for {
      follower <- userNameGen[Follower]
      subscriptions <- Gen.containerOfN[List, UserName[Followee]](3, userNameGen[Followee])
    } yield TestCase(follower, subscriptions)).sampleWithSeed("JdbcSubscriptionsSuite")

    forall(gen) {
      case TestCase(subscriber, subscriptions) =>
        testResources.use {
          case (migrator, transactor) =>
            val subscriptionsRepository = JdbcSubscriptions(transactor)
            for {
              _ <- migrator.migrate() *> subscriptions.traverse_(
                subscriptionsRepository.follow(subscriber, _)
              )
              actual <- subscriptionsRepository.followeesOf(subscriber)
            } yield expect(
              actual.sortWith(_ < _) == subscriptions.sortWith(_ < _)
            )
        }
    }
  }
}
