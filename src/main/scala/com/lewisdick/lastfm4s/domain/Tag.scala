package com.lewisdick.lastfm4s.domain

import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder
import org.http4s.Uri
import com.lewisdick.lastfm4s.domain.ApiError.decoderError
import org.http4s.circe.decodeUri // USED FOR DECODING TAG

sealed trait BaseTag {
  val name: String
  val url: Uri
}
final case class Tag(name: String, url: Uri)                      extends BaseTag
final case class TagWithCount(name: String, url: Uri, count: Int) extends BaseTag

object Tag {
  implicit val decodeTag: Decoder[Tag]         = deriveDecoder[Tag]
  implicit val decodeRootTag: Decoder[RootTag] = deriveDecoder[RootTag]
}

final case class RootTag(tag: List[Tag]) extends Root[List[Tag]] {
  override def get: List[Tag] = tag
}

final case class RootTagWithCount(tag: List[TagWithCount])

final case class RootTopTags(topTags: List[TagWithCount]) extends Root[List[TagWithCount]] {
  override def get: List[TagWithCount] = topTags
}

object TagWithCount {
  implicit val decodeTagWithCount: Decoder[TagWithCount]         = deriveDecoder[TagWithCount]
  implicit val decodeRootTagWithCount: Decoder[RootTagWithCount] = deriveDecoder[RootTagWithCount]
  implicit val decodeRootTopTags: Decoder[RootTopTags] =
    Decoder.forProduct1[RootTopTags, RootTagWithCount]("toptags")(t => RootTopTags(t.tag))
  implicit val decoderResult: Decoder[Either[ApiError, RootTopTags]] = decoderError[RootTopTags]
}
