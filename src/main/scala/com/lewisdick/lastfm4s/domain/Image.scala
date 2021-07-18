package com.lewisdick.lastfm4s.domain

import enumeratum.{ Enum, EnumEntry, _ }
import io.circe.Decoder
import org.http4s.Uri
import org.http4s.circe.decodeUri

case class Image(url: Uri, size: ImageSize)

object Image {
  implicit val decoder: Decoder[Image] = Decoder.forProduct2[Image, Uri, ImageSize]("#text", "size") {
    case (u, s) => Image(u, s)
  }
}

sealed abstract class ImageSize(override val entryName: String) extends EnumEntry

object ImageSize extends Enum[ImageSize] with CirceEnum[ImageSize] {
  val values = findValues

  case object small      extends ImageSize("small")
  case object medium     extends ImageSize("medium")
  case object large      extends ImageSize("large")
  case object extralarge extends ImageSize("extralarge")
  case object mega       extends ImageSize("mega")
  case object unknown    extends ImageSize("")
}
