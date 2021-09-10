package com.lewisdick.lastfm4s.domain

import io.circe.{ Decoder, HCursor }
import io.circe.generic.semiauto.deriveDecoder
import org.http4s.Uri
import org.http4s.circe.decodeUri
import com.lewisdick.lastfm4s.domain.ApiError._
import com.lewisdick.lastfm4s.domain.Artist.trackArtistDecoder
import io.circe.Decoder.Result

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

case class SimilarArtist(name: String, mbid: String, similarity: Float, url: Uri, image: List[Image])

case class SimilarArtists(artists: List[SimilarArtist]) extends Root[List[SimilarArtist]] {
  override def get: List[SimilarArtist] = artists
}

case class Stats(listeners: Int, playcount: Int)

case class RootSearchArtist(artist: List[SearchArtist]) extends Root[List[SearchArtist]] {
  override def get: List[SearchArtist] = artist
}

case class SearchArtist(name: String, listeners: Int, mbid: String, url: Uri, image: List[Image])

case class Correction(artist: TrackArtist) extends Root[TrackArtist] {
  override def get: TrackArtist = artist
}

case class ChartArtist(name: String, playCount: Int, listeners: Int, mbid: String, url: Uri, image: List[Image])

case class ChartArtists(artists: List[ChartArtist]) extends Root[List[ChartArtist]] {
  override def get: List[ChartArtist] = artists
}

object ChartArtist {
  implicit val chartArtistDec: Decoder[ChartArtist] =
    Decoder.forProduct6[ChartArtist, String, Int, Int, String, Uri, List[Image]](
      "name",
      "playcount",
      "listeners",
      "mbid",
      "url",
      "image"
    )(ChartArtist.apply)

  implicit val chartArtistsDec: Decoder[ChartArtists] = new Decoder[ChartArtists] {
    override def apply(c: HCursor): Result[ChartArtists] =
      c.downField("artists").downField("artist").as[List[ChartArtist]].map(ChartArtists.apply)
  }

  implicit val chartArtistResult: Decoder[Either[ApiError, ChartArtists]] = decodeError[ChartArtists]
}

object Correction {
  implicit val correctionDec: Decoder[Correction] = new Decoder[Correction] {
    override def apply(c: HCursor): Result[Correction] =
      c.downField("corrections").downField("correction").downField("artist").as[TrackArtist].map(Correction.apply)
  }

  implicit val correctionResult: Decoder[Either[ApiError, Correction]] = decodeError[Correction]
}

object SearchArtist {
  implicit val rootSearchArtistDec: Decoder[RootSearchArtist] = deriveDecoder
  implicit val searchArtistDec: Decoder[SearchArtist]         = deriveDecoder
}

object SimilarArtist {
  implicit val similarArtistDec: Decoder[SimilarArtist] =
    Decoder.forProduct5[SimilarArtist, String, String, Float, Uri, List[Image]](
      "name",
      "mbid",
      "match",
      "url",
      "image"
    )(SimilarArtist.apply)
  implicit val similarArtistsDec: Decoder[SimilarArtists] = new Decoder[SimilarArtists] {
    override def apply(c: HCursor): Result[SimilarArtists] =
      c.downField("similarartists").downField("artist").as[List[SimilarArtist]].map(SimilarArtists)
  }
}

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
      case (n, m, u, i, s, si, t, b) => ArtistInfo(n, m, u, i, s, si.artist, t.get, b)
    }
}
