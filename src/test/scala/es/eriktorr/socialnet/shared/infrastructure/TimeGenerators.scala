package es.eriktorr.socialnet.shared.infrastructure

import java.time.LocalDateTime

import org.scalacheck.{Arbitrary, Gen}

object TimeGenerators {
  implicit lazy val localDateTimeGen: Arbitrary[LocalDateTime] = {
    import java.time.ZoneOffset.UTC
    Arbitrary {
      val now = LocalDateTime.now(UTC)
      for {
        seconds <- Gen.chooseNum(
          now.minusMonths(6L).toEpochSecond(UTC),
          now.toEpochSecond(UTC)
        )
      } yield LocalDateTime.ofEpochSecond(seconds, 0, UTC)
    }
  }
}
