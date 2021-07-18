package com.lewisdick.lastfm4s.domain

import io.circe.Decoder
import io.circe.Decoder.decodeList
import io.circe.generic.semiauto.deriveDecoder
import org.http4s.Uri
import org.http4s.circe.decodeUri
import com.lewisdick.lastfm4s.domain.ApiError.decoderError

abstract class Root[T] {
  def get: T
}

final case class RootAlbumInfo(album: AlbumInfo) extends Root[AlbumInfo] {
  override def get: AlbumInfo = album
}

final case class RootAlbum(album: List[Album]) extends Root[List[Album]] {
  override def get: List[Album] = album
}

case class Album(name: String, artist: String, mbid: String, image: List[Image])

case class AlbumInfo(
    name: String,
    artist: String,
    mbid: String,
    url: Uri,
    image: List[Image],
    listeners: Int,
    playcount: Int,
    tracks: List[Track],
    tags: List[Tag],
    wiki: Wiki
)

object AlbumInfo {
  implicit val decodeAlbum: Decoder[Album] = deriveDecoder[Album]

  implicit val decodeAlbumInfo: Decoder[AlbumInfo] =
    Decoder.forProduct10[AlbumInfo, String, String, String, Uri, List[Image], Int, Int, RootTrack, RootTag, Wiki](
      "name",
      "artist",
      "mbid",
      "url",
      "image",
      "listeners",
      "playcount",
      "tracks",
      "tags",
      "wiki"
    ) {
      case (n, a, m, u, i, l, p, t, ta, w) => AlbumInfo(n, a, m, u, i, l, p, t.track, ta.tag, w)
    }

  implicit val decodeRootAlbumInfo: Decoder[RootAlbumInfo]             = deriveDecoder[RootAlbumInfo]
  implicit val decodeRootAlbum: Decoder[RootAlbum]                     = deriveDecoder[RootAlbum]
  implicit val decoderResult: Decoder[Either[ApiError, RootAlbumInfo]] = decoderError[RootAlbumInfo]
}

case class Wiki(published: String, summary: String, content: String)

object Wiki {
  implicit val wikiDecoder: Decoder[Wiki] = deriveDecoder[Wiki]
}
