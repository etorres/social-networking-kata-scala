package es.eriktorr.socialnet.domain

import scala.util.control.NoStackTrace

object error {
  sealed trait RequestError extends NoStackTrace

  final case class ConfigurationReadError(error: String) extends RequestError
  final case class InvalidParameter(error: String) extends RequestError
  case object UnknownCommand extends RequestError
}
