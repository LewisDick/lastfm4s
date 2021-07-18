package com.lewisdick.lastfm4s.domain

import com.lewisdick.lastfm4s.ResultDecoder
import io.circe.Decoder
import io.circe.generic.auto._
import io.circe.generic.semiauto.deriveDecoder

case class RootSearchResult(results: SearchResult) extends Root[SearchResult] {
  override def get: SearchResult = results
}

case class SearchResult(totalResults: Int, startIndex: Int, itemsPerPage: Int, albums: List[Album])

object SearchResult {
  implicit val rootDecoder: Decoder[RootSearchResult] = deriveDecoder[RootSearchResult]
  implicit val decoder = Decoder.forProduct4[SearchResult, Int, Int, Int, RootAlbum](
    "opensearch:totalResults",
    "opensearch:startIndex",
    "opensearch:itemsPerPage",
    "albummatches"
  ) {
    case (t, s, i, a) => SearchResult(t, s, i, a.album)
  }

  implicit val decoderResult: Decoder[Either[ApiError, RootSearchResult]] = ResultDecoder.create[RootSearchResult]
}
