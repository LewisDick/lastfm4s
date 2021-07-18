package com.lewisdick.lastfm4s.domain

import enumeratum.values.{ IntCirceEnum, IntEnum, IntEnumEntry }
import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder
import io.circe.disjunctionCodecs.decoderEither

final case class ApiError(error: ErrorCode, message: String)

object ApiError {
  implicit val decoder: Decoder[ApiError]                                             = deriveDecoder[ApiError]
  implicit def decoderError[T](implicit dt: Decoder[T]): Decoder[Either[ApiError, T]] = decoderEither[ApiError, T]
}

sealed abstract class ErrorCode(val value: Int) extends IntEnumEntry

object ErrorCode extends IntEnum[ErrorCode] with IntCirceEnum[ErrorCode] {
  val values = findValues

  case object InvalidService           extends ErrorCode(2)
  case object InvalidMethod            extends ErrorCode(3)
  case object AuthenticationFailed     extends ErrorCode(4)
  case object InvalidFormat            extends ErrorCode(5)
  case object InvalidParameters        extends ErrorCode(6)
  case object InvalidResourceSpecified extends ErrorCode(7)
  case object OperationFailed          extends ErrorCode(8)
  case object InvalidApiKey            extends ErrorCode(9)
  case object InvalidSessionKey        extends ErrorCode(10)
  case object ServiceOffline           extends ErrorCode(11)
  case object InvalidMethodSignature   extends ErrorCode(13)
  case object TemporaryError           extends ErrorCode(16)
  case object SuspendedApiKey          extends ErrorCode(26)
  case object RateLimitExceeded        extends ErrorCode(29)
}
