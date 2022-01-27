package zio.sql

import zio.test.TestEnvironment
import zio.ZLayer
import zio.Clock
import zio.test.ZIOSpecDefault

trait JdbcRunnableSpec extends ZIOSpecDefault with Jdbc {

  type JdbcEnvironment = TestEnvironment with SqlDriver

  val poolConfigLayer: ZLayer[Any, Throwable, ConnectionPoolConfig]

  final lazy val executorLayer = {
    val connectionPoolLayer: ZLayer[Clock, Throwable, ConnectionPool] =
      (poolConfigLayer ++ Clock.any) >>> ConnectionPool.live

    (connectionPoolLayer >+> SqlDriver.live).orDie
  }

  final lazy val jdbcLayer = TestEnvironment.live >>> executorLayer
}
