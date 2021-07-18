package com.lewisdick.lastfm4s.domain

import com.lewisdick.lastfm4s.domain.Artist.trackArtistDecoder
import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder
import org.http4s.Uri
import org.http4s.circe.decodeUri

case class Track(name: String, url: Uri, duration: Int, rank: Int, artist: TrackArtist)

final case class RootTrack(track: List[Track]) extends Root[List[Track]] {
  override def get: List[Track] = track
}

object Track {
  implicit val decoder =
    Decoder.forProduct4[Track, String, Uri, Int, TrackArtist]("name", "url", "duration", "artist") {
      case (n, u, d, a) => Track(n, u, d, 0, a)
    }

  implicit val decodeRootAlbum: Decoder[RootTrack] = deriveDecoder[RootTrack]
}
