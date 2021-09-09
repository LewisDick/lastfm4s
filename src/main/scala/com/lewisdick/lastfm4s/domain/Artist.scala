package com.lewisdick.lastfm4s.domain

import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder
import org.http4s.Uri
import org.http4s.circe.decodeUri
import com.lewisdick.lastfm4s.domain.ApiError._

case class ArtistInfo(
    name: String,
    mbid: String,
    url: Uri,
    image: List[Image],
    stats: Stats,
    similar: List[Artist],
    tags: List[Tag],
    bio: Wiki
)

final case class RootArtist(artist: List[Artist]) extends Root[List[Artist]] {
  override def get: List[Artist] = artist
}

final case class RootArtistInfo(artist: ArtistInfo) extends Root[ArtistInfo] {
  override def get: ArtistInfo = artist
}

case class Artist(name: String, url: Uri, image: List[Image])

case class TrackArtist(name: String, mbid: String, url: Uri)

case class Stats(listeners: Int, playcount: Int)

object Stats {
  implicit val statsDecoder: Decoder[Stats] = deriveDecoder[Stats]
}

object Artist {
  implicit val trackArtistDecoder: Decoder[TrackArtist] = deriveDecoder[TrackArtist]
  implicit val artistDecoder: Decoder[Artist]           = deriveDecoder[Artist]
  implicit val rootArtistDecoder: Decoder[RootArtist]   = deriveDecoder[RootArtist]
}

object ArtistInfo {
  implicit val rootArtistInfoDecoder: Decoder[RootArtistInfo]           = deriveDecoder[RootArtistInfo]
  implicit val decoderResult: Decoder[Either[ApiError, RootArtistInfo]] = decodeError[RootArtistInfo]
  implicit val artistInfoDecoder: Decoder[ArtistInfo] =
    Decoder.forProduct8[ArtistInfo, String, String, Uri, List[Image], Stats, RootArtist, RootTag, Wiki](
      "name",
      "mbid",
      "url",
      "image",
      "stats",
      "similar",
      "tags",
      "bio"
    ) {
      case (n, m, u, i, s, si, t, b) => ArtistInfo(n, m, u, i, s, si.artist, t.tag, b)
    }
}
