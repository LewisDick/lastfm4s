package com.lewisdick.lastfm4s.domain

import com.lewisdick.lastfm4s.domain.ApiError._
import io.circe.{ Decoder, HCursor }
import io.circe.Decoder.{ decodeList, Result }
import io.circe.generic.semiauto.deriveDecoder
import org.http4s.Uri
import org.http4s.circe.decodeUri

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

case class TopAlbum(name: String, mbid: String, playCount: Int, url: Uri, image: List[Image])

case class TopAlbums(albums: List[TopAlbum]) extends Root[List[TopAlbum]] {
  override def get: List[TopAlbum] = albums
}

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

object TopAlbum {
  implicit val topAlbumDec: Decoder[TopAlbum] = Decoder.forProduct5[TopAlbum, String, String, Int, Uri, List[Image]](
    "name",
    "mbid",
    "playcount",
    "url",
    "image"
  )(TopAlbum.apply)

  implicit val topAlbumsDec: Decoder[TopAlbums] = new Decoder[TopAlbums] {
    override def apply(c: HCursor): Result[TopAlbums] =
      c.downField("topalbums").downField("album").as[List[TopAlbum]].map(TopAlbums)
  }
}

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
      case (n, a, m, u, i, l, p, t, ta, w) => AlbumInfo(n, a, m, u, i, l, p, t.track, ta.get, w)
    }

  implicit val decodeRootAlbumInfo: Decoder[RootAlbumInfo]             = deriveDecoder[RootAlbumInfo]
  implicit val decodeRootAlbum: Decoder[RootAlbum]                     = deriveDecoder[RootAlbum]
  implicit val decoderResult: Decoder[Either[ApiError, RootAlbumInfo]] = decodeError[RootAlbumInfo]
}

case class Wiki(published: String, summary: String, content: String)

object Wiki {
  implicit val wikiDecoder: Decoder[Wiki] = deriveDecoder[Wiki]
}
