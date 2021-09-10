package com.lewisdick.lastfm4s.domain

import cats.implicits.none
import com.lewisdick.lastfm4s.domain.ApiError._
import io.circe.{ Decoder, HCursor }
import io.circe.Decoder.Result
import io.circe.generic.semiauto.deriveDecoder
import org.http4s.Uri
import org.http4s.circe.decodeUri // USED FOR DECODING TAG

sealed trait BaseTag {
  val name: String
  val url: Uri
}
final case class Tag(name: String, url: Uri)                      extends BaseTag
final case class TagWithCount(name: String, url: Uri, count: Int) extends BaseTag

case class TagInfo(name: String, url: Uri, reach: Int, taggings: Int, wiki: Option[Wiki])

case class RootTagInfo(tags: List[TagInfo]) extends Root[List[TagInfo]] {
  override def get: List[TagInfo] = tags
}

object TagInfo {

  implicit val tagInfoDec: Decoder[TagInfo] = new Decoder[TagInfo] {
    override def apply(c: HCursor): Result[TagInfo] =
      for {
        name     <- c.downField("name").as[String]
        url      <- c.downField("url").as[Uri]
        reach    <- c.downField("reach").as[Int]
        taggings <- c.downField("taggings").as[Int]
        wiki     <- Right(c.downField("wiki").as[Wiki].fold[Option[Wiki]](_ => none[Wiki], w => Some(w)))
      } yield TagInfo(name, url, reach, taggings, wiki)
  }

  implicit val rootTagInfoDec: Decoder[RootTagInfo] = new Decoder[RootTagInfo] {
    override def apply(c: HCursor): Result[RootTagInfo] =
      c.downField("tags").downField("tag").as[List[TagInfo]].map(RootTagInfo.apply)
  }

  implicit val chartArtistResult: Decoder[Either[ApiError, RootTagInfo]] = decodeError[RootTagInfo]
}

object Tag {
  implicit val decodeTag: Decoder[Tag]                                = deriveDecoder[Tag]
  implicit val decodeRootTag: Decoder[RootTag]                        = deriveDecoder[RootTag]
  implicit val decodeRootTags: Decoder[RootTags]                      = Decoder.forProduct1[RootTags, RootTag]("tags")(t => RootTags(t.get))
  implicit val decoderTagsResult: Decoder[Either[ApiError, RootTags]] = decodeError[RootTags]
}

final case class RootTag(tag: Option[List[Tag]]) extends Root[List[Tag]] {
  override def get: List[Tag] = tag.getOrElse(List.empty)
}

final case class RootTags(tags: List[Tag]) extends Root[List[Tag]] {
  override def get: List[Tag] = tags
}

final case class RootTagWithCount(tag: List[TagWithCount])

final case class TopTags(tags: List[TagWithCount]) extends Root[List[TagWithCount]] {
  override def get: List[TagWithCount] = tags
}

object TagWithCount {
  implicit val decodeTagWithCount: Decoder[TagWithCount]         = deriveDecoder[TagWithCount]
  implicit val decodeRootTagWithCount: Decoder[RootTagWithCount] = deriveDecoder[RootTagWithCount]

  implicit val topTagsDec: Decoder[TopTags] = new Decoder[TopTags] {
    override def apply(c: HCursor): Result[TopTags] =
      c.downField("toptags").downField("tag").as[List[TagWithCount]].map(TopTags)
  }

  implicit val decoderResult: Decoder[Either[ApiError, TopTags]] = decodeError[TopTags]
}
