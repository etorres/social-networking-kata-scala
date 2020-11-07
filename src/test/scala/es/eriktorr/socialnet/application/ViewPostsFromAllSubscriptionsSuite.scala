package es.eriktorr.socialnet.application

import cats._
import cats.derived._
import es.eriktorr.socialnet.domain.user._
import es.eriktorr.socialnet.shared.infrastructure.FakeSocialNetworkContext.withSocialNetworkContext
import es.eriktorr.socialnet.shared.infrastructure.SocialNetworkGenerators.userNameGen
import org.scalacheck._
import weaver._
import weaver.scalacheck._

object ViewPostsFromAllSubscriptionsSuite extends SimpleIOSuite with IOCheckers {
  simpleTest("Subscribe to timelines, and view an aggregated list of all subscriptions") {
    final case class TestCase()

    object TestCase {
      implicit val showTestCase: Show[TestCase] = semiauto.show
    }

    val gen = for {
      subscriber <- userNameGen
      subscriptions <- Gen.containerOfN[List, UserName](2, userNameGen)
    } yield TestCase()

    forall(gen) { testCase =>
      withSocialNetworkContext(???)(_.viewPostsFromAllSubscriptions.viewPostsByAll) map {
        case (finalState, _) =>
          expect(true)
      }
    }
  }
}
