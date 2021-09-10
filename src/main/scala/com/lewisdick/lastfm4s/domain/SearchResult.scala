package com.lewisdick.lastfm4s.domain

import com.lewisdick.lastfm4s.domain.ApiError._
import io.circe.Decoder
import io.circe.generic.auto._
import io.circe.generic.semiauto.deriveDecoder

case class RootSearchResult[T](results: SearchResult[T]) extends Root[SearchResult[T]] {
  override def get: SearchResult[T] = results
}

case class SearchResult[T](totalResults: Int, startIndex: Int, itemsPerPage: Int, results: List[T])

object SearchResult {
  implicit val rootDecoder: Decoder[RootSearchResult[Album]] = deriveDecoder
  implicit val decoder: Decoder[SearchResult[Album]] =
    Decoder.forProduct4[SearchResult[Album], Int, Int, Int, RootAlbum](
      "opensearch:totalResults",
      "opensearch:startIndex",
      "opensearch:itemsPerPage",
      "albummatches"
    ) {
      case (t, s, i, a) => SearchResult(t, s, i, a.album)
    }

  implicit val rootSearchArtistDecoder: Decoder[RootSearchResult[SearchArtist]] = deriveDecoder
  implicit val searchArtistDecoder: Decoder[SearchResult[SearchArtist]] =
    Decoder.forProduct4[SearchResult[SearchArtist], Int, Int, Int, RootSearchArtist](
      "opensearch:totalResults",
      "opensearch:startIndex",
      "opensearch:itemsPerPage",
      "artistmatches"
    ) {
      case (t, s, i, a) => SearchResult(t, s, i, a.artist)
    }

  implicit val albumDecoderResult: Decoder[Either[ApiError, RootSearchResult[Album]]] =
    decodeError[RootSearchResult[Album]]
  implicit val artistDecoderResult: Decoder[Either[ApiError, RootSearchResult[SearchArtist]]] =
    decodeError[RootSearchResult[SearchArtist]]
}
