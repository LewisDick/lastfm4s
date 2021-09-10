package com.lewisdick.lastfm4s.domain

import com.lewisdick.lastfm4s.domain.Artist.trackArtistDecoder
import io.circe.{ Decoder, HCursor }
import io.circe.Decoder.Result
import io.circe.generic.semiauto.deriveDecoder
import org.http4s.Uri
import org.http4s.circe.decodeUri

case class Track(name: String, url: Uri, duration: Int, rank: Int, artist: TrackArtist)

case class TopTrack(
    name: String,
    playcount: Int,
    listeners: Int,
    mbid: Option[String],
    url: Uri,
    artist: TrackArtist,
    image: List[Image]
)

case class TopTracks(tracks: List[TopTrack]) extends Root[List[TopTrack]] {
  override def get: List[TopTrack] = tracks
}

final case class RootTrack(track: List[Track]) extends Root[List[Track]] {
  override def get: List[Track] = track
}

object TopTrack {
  implicit val topTrackDec: Decoder[TopTrack] =
    Decoder.forProduct7[TopTrack, String, Int, Int, Option[String], Uri, TrackArtist, List[Image]](
      "name",
      "playcount",
      "listeners",
      "mbid",
      "url",
      "artist",
      "image"
    )(TopTrack.apply)

  implicit val topTracksDec: Decoder[TopTracks] = new Decoder[TopTracks] {
    override def apply(c: HCursor): Result[TopTracks] =
      c.downField("toptracks").downField("track").as[List[TopTrack]].map(TopTracks)
  }
}

object Track {
  implicit val decoder: Decoder[Track] =
    Decoder.forProduct4[Track, String, Uri, Int, TrackArtist]("name", "url", "duration", "artist") {
      case (n, u, d, a) => Track(n, u, d, 0, a)
    }

  implicit val decodeRootAlbum: Decoder[RootTrack] = deriveDecoder[RootTrack]
}
