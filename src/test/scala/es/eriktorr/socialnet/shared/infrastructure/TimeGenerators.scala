package es.eriktorr.socialnet.shared.infrastructure

import java.time.{Instant, OffsetDateTime}

import es.eriktorr.socialnet.domain.time._
import org.scalacheck._

object TimeGenerators {
  implicit lazy val offsetDateTimeGen: Arbitrary[OffsetDateTime] = {
    import java.time.ZoneOffset.UTC
    Arbitrary {
      val now = OffsetDateTime.now(UTC)
      for {
        epochSecond <- Gen.chooseNum(
          now.minusMonths(6L).toEpochSecond,
          now.toEpochSecond
        )
      } yield OffsetDateTime.ofInstant(Instant.ofEpochSecond(epochSecond), UTC)
    }
  }

  val timeMarkGen: Gen[TimeMark] = Arbitrary.arbitrary[OffsetDateTime].map(TimeMark(_))
}
