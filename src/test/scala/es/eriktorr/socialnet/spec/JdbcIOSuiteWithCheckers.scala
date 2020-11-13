package es.eriktorr.socialnet.spec

import cats.effect._
import doobie._
import es.eriktorr.socialnet.infrastructure.jdbc.JdbcTestTransactor
import weaver._
import weaver.scalacheck._

import scala.concurrent.ExecutionContextExecutor

trait JdbcIOSuiteWithCheckers extends SimpleIOSuite with IOCheckers {
  implicit val evEc: ExecutionContextExecutor = ec
  implicit val evBlocker: Blocker = Blocker.liftExecutionContext(ec)

  override def maxParallelism: Int = 1
  override def checkConfig: CheckConfig =
    super.checkConfig.copy(minimumSuccessful = 10, perPropertyParallelism = 1)

  def currentSchema: String

  val testResources: Resource[IO, Transactor[IO]] = JdbcTestTransactor.testTransactorResource(
    JdbcTestTransactor.socialNetworkJdbcConfig,
    currentSchema
  )
}
