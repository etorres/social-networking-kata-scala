package es.eriktorr.socialnet.shared.infrastructure

import org.scalacheck._

object NameGenerator {
  val nameGen: Gen[String] = Gen.oneOf(
    "Olivia",
    "Oliver",
    "Amelia",
    "George",
    "Maya",
    "Noah",
    "Ava",
    "Arthur",
    "Mia",
    "Harry",
    "Isabella",
    "Leo",
    "Sophia",
    "Muhammad",
    "Grace",
    "Jack",
    "Lily",
    "Charlie",
    "Freya",
    "Oscar",
    "Emily",
    "Jacob",
    "Ivy",
    "Henry",
    "Ella",
    "Thomas",
    "Rosie",
    "Freddie",
    "Evie",
    "Alfie",
    "Theo",
    "Florence",
    "William",
    "Poppy",
    "Theodore",
    "Charlotte",
    "Archie",
    "Willow",
    "Joshua",
    "Evelyn"
  )
}
