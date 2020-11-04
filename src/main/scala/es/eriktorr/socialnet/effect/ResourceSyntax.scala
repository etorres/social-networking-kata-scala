package es.eriktorr.socialnet.effect

import cats.effect._

trait ResourceSyntax {
  implicit class ResourceOps[A](self: A) {
    def asResource: Resource[IO, A] = Resource.make(IO(self))(_ => IO.unit)
  }
}
