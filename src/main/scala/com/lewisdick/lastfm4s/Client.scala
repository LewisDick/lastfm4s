package com.lewisdick.lastfm4s

import cats.Applicative
import cats.implicits._
import cats.effect.{ ConcurrentEffect, Sync }
import com.lewisdick.lastfm4s.domain.{
  Album,
  AlbumInfo,
  ApiError,
  ArtistInfo,
  ChartArtist,
  ChartArtists,
  ChartTracks,
  Correction,
  Root,
  RootAlbumInfo,
  RootArtistInfo,
  RootSearchResult,
  RootTagInfo,
  RootTags,
  SearchArtist,
  SearchResult,
  SimilarArtist,
  SimilarArtists,
  Tag,
  TagInfo,
  TagWithCount,
  TopAlbum,
  TopAlbums,
  TopTags,
  TopTrack,
  TopTracks,
  TrackArtist
}
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import io.circe.Decoder
import org.http4s.QueryParamEncoder.stringQueryParamEncoder
import org.http4s.client.{ Client => HttpClient }
import org.http4s.implicits.http4sLiteralsSyntax
import org.http4s.{ EntityDecoder, Uri }
import com.lewisdick.lastfm4s.domain.ApiError._

import scala.concurrent.ExecutionContext

sealed trait Client[F[_]] {
  def addAlbumTags(
      artist: String,
      album: String,
      tags: List[String],
      signature: String,
      sessionKey: String
  ): F[Either[ApiError, Unit]]

  def getAlbumInfo(
      artist: String,
      album: String,
      mbid: Option[String] = None,
      autocorrect: Option[Boolean] = None,
      username: Option[String] = None,
      lang: Option[String] = None
  ): F[Either[ApiError, AlbumInfo]]

  def getAlbumTags(
      artist: String,
      album: String,
      user: String,
      autocorrect: Option[Boolean] = None,
      mbid: Option[Boolean] = None
  ): F[Either[ApiError, List[Tag]]]

  def getAlbumTopTags(
      artist: String,
      album: String,
      autocorrect: Option[Boolean] = None,
      mbid: Option[String] = None
  ): F[Either[ApiError, List[TagWithCount]]]

  def searchAlbums(
      album: String,
      limit: Option[Int] = None,
      page: Option[Int] = None
  ): F[Either[ApiError, SearchResult[Album]]]

  def getArtistInfo(
      artist: String,
      mbid: Option[String] = None,
      lang: Option[String] = None,
      autocorrect: Option[Boolean] = None,
      username: Option[String] = None
  ): F[Either[ApiError, ArtistInfo]]

  def getArtistCorrection(
      artist: String
  ): F[Either[ApiError, TrackArtist]]

  def getSimilarArtists(
      artist: String,
      mbid: Option[String] = None,
      limit: Option[Int] = None,
      autocorrect: Option[Boolean] = None
  ): F[Either[ApiError, List[SimilarArtist]]]

  def getArtistTags(
      artist: String,
      user: String,
      mbid: Option[Boolean] = None,
      autocorrect: Option[Boolean] = None
  ): F[Either[ApiError, List[Tag]]]

  def getArtistTopAlbums(
      artist: String,
      mbid: Option[Boolean] = None,
      autocorrect: Option[Boolean] = None,
      page: Option[Int] = None,
      limit: Option[Int] = None
  ): F[Either[ApiError, List[TopAlbum]]]

  def getArtistTopTags(
      artist: String,
      mbid: Option[String] = None,
      autocorrect: Option[Boolean] = None
  ): F[Either[ApiError, List[TagWithCount]]]

  def getArtistTopTracks(
      artist: String,
      mbid: Option[String] = None,
      autocorrect: Option[Boolean] = None,
      page: Option[Int] = None,
      limit: Option[Int] = None
  ): F[Either[ApiError, List[TopTrack]]]

  def searchArtists(
      artist: String,
      limit: Option[Int] = None,
      page: Option[String] = None
  ): F[Either[ApiError, SearchResult[SearchArtist]]]

  def getTopArtistsChart(
      page: Option[Int] = None,
      limit: Option[Int] = None
  ): F[Either[ApiError, List[ChartArtist]]]

  def getTopTagsChart(
      page: Option[Int] = None,
      limit: Option[Int] = None
  ): F[Either[ApiError, List[TagInfo]]]

  def getTopTracksChart(
      page: Option[Int] = None,
      limit: Option[Int] = None
  ): F[Either[ApiError, List[TopTrack]]]

  //TODO Add unsupported endpoints:
  //  def removeAlbumTags
  //  def addArtistTags
  //  def removeArtistTag
  //  def addTrackTags
  //  def loveTrack
  //  def removeTrackTag
  //  def getMobileSession
  //  def getSession
  //  def getToken
  //  def scrobble
  //  def searchTracks
  //  def unloveTrack
  //  def updateNowPlaying

  //  def getTopArtistsGeo
  //  def getTopTracksGeo

  //  def getLibraryArtists

  //  def getTagInfo
  //  def getSimilarTags
  //  def getTagTopAlbums
  //  def getTagTopArtists
  //  def getTagTopTracks
  //  def getTopTags
  //  def getTagWeeklyChartList
  //  def getTrackCorrection
  //  def getTrackInfo
  //  def getSimilarTracks
  //  def getTrackTags
  //  def getTopTags

  //  def getFriends
  //  def getUserInfo
  //  def getLovedTracks
  //  def getPersonalTags
  //  def getUserRecentTracks
  //  def getUserTopAlbums
  //  def getUserTopArtists
  //  def getUserTopTags
  //  def getUserWeeklyAlbumChart
  //  def getUserWeeklyArtistChart
  //  def getUserWeeklyChartList
  //  def getUserWeeklyTrackChart
}

object Client {
  def apply[F[_]: Sync: ConcurrentEffect: Applicative](client: HttpClient[F], apiKey: String)(implicit
      ec: ExecutionContext
  ): Client[F] = {
    val uri                                                   = uri"http://ws.audioscrobbler.com/2.0/?format=json".withQueryParam("api_key", apiKey)
    implicit val unitDecoder: Decoder[Either[ApiError, Unit]] = decodeError[Unit]

    new Client[F] {
      override def addAlbumTags(
          artist: String,
          album: String,
          tags: List[String],
          signature: String,
          sessionKey: String
      ): F[Either[ApiError, Unit]] = ???

      override def getAlbumInfo(
          artist: String,
          album: String,
          mbid: Option[String] = None,
          autocorrect: Option[Boolean] = None,
          username: Option[String] = None,
          lang: Option[String] = None
      ): F[Either[ApiError, AlbumInfo]] =
        send[RootAlbumInfo, AlbumInfo](
          uri
            .withQueryParams(
              Map(
                "method" -> "album.getinfo",
                "artist" -> artist,
                "album"  -> album
              )
            )
            .withOptionQueryParam("mbid", mbid)
            .withOptionQueryParam("autocorrect", autocorrect.map(toLastFmBoolean))
            .withOptionQueryParam("username", username)
            .withOptionQueryParam("lang", lang)
        )

      override def searchAlbums(
          album: String,
          limit: Option[Int] = None,
          page: Option[Int]
      ): F[Either[ApiError, SearchResult[Album]]] =
        send[RootSearchResult[Album], SearchResult[Album]](
          uri
            .withQueryParams(
              Map(
                "method" -> "album.search",
                "album"  -> album
              )
            )
            .withOptionQueryParam("limit", limit)
            .withOptionQueryParam("page", page)
        )

      override def getArtistInfo(
          artist: String,
          mbid: Option[String],
          lang: Option[String],
          autocorrect: Option[Boolean],
          username: Option[String]
      ): F[Either[ApiError, ArtistInfo]] =
        send[RootArtistInfo, ArtistInfo](
          uri
            .withQueryParams(Map("method" -> "artist.getinfo", "artist" -> artist))
            .withOptionQueryParam("mbid", mbid)
            .withOptionQueryParam("autocorrect", autocorrect.map(toLastFmBoolean))
            .withOptionQueryParam("username", username)
            .withOptionQueryParam("lang", lang)
        )

      override def getAlbumTags(
          artist: String,
          album: String,
          user: String,
          autocorrect: Option[Boolean],
          mbid: Option[Boolean]
      ): F[Either[ApiError, List[Tag]]] =
        send[RootTags, List[Tag]](
          uri
            .withQueryParams(Map("method" -> "album.getTags", "artist" -> artist, "album" -> album, "user" -> user))
            .withOptionQueryParam("autocorrect", autocorrect.map(toLastFmBoolean))
            .withOptionQueryParam("mbid", mbid)
        )

      override def getAlbumTopTags(
          artist: String,
          album: String,
          autocorrect: Option[Boolean],
          mbid: Option[String]
      ): F[Either[ApiError, List[TagWithCount]]] =
        send[TopTags, List[TagWithCount]](
          uri
            .withQueryParams(Map("method" -> "album.getTopTags", "artist" -> artist, "album" -> album))
            .withOptionQueryParam("autocorrect", autocorrect.map(toLastFmBoolean))
            .withOptionQueryParam("mbid", mbid)
        )

      private def send[T <: Root[E], E](uri: Uri)(implicit
          ed: EntityDecoder[F, Either[ApiError, T]]
      ): F[Either[ApiError, E]] =
        client.expect[Either[ApiError, T]](uri).map(_.map(_.get))

      private def toLastFmBoolean(bool: Boolean): String =
        if (bool)
          "1"
        else
          "0"

      override def getArtistCorrection(artist: String): F[Either[ApiError, TrackArtist]] =
        send[Correction, TrackArtist](
          uri.withQueryParams(Map("method" -> "artist.getCorrection", "artist" -> artist))
        )

      override def getSimilarArtists(
          artist: String,
          mbid: Option[String],
          limit: Option[Int],
          autocorrect: Option[Boolean]
      ): F[Either[ApiError, List[SimilarArtist]]] =
        send[SimilarArtists, List[SimilarArtist]](
          uri
            .withQueryParams(Map("method" -> "artist.getSimilar", "artist" -> artist))
            .withOptionQueryParam("autocorrect", autocorrect.map(toLastFmBoolean))
            .withOptionQueryParam("mbid", mbid)
            .withOptionQueryParam("limit", limit)
        )

      override def getArtistTags(
          artist: String,
          user: String,
          mbid: Option[Boolean],
          autocorrect: Option[Boolean]
      ): F[Either[ApiError, List[Tag]]] =
        send[RootTags, List[Tag]](
          uri
            .withQueryParams(Map("method" -> "artist.getTags", "artist" -> artist, "user" -> user))
            .withOptionQueryParam("autocorrect", autocorrect.map(toLastFmBoolean))
            .withOptionQueryParam("mbid", mbid)
        )

      override def getArtistTopAlbums(
          artist: String,
          mbid: Option[Boolean],
          autocorrect: Option[Boolean],
          page: Option[Int],
          limit: Option[Int]
      ): F[Either[ApiError, List[TopAlbum]]] =
        send[TopAlbums, List[TopAlbum]](
          uri
            .withQueryParams(Map("method" -> "artist.getTopAlbums", "artist" -> artist))
            .withOptionQueryParam("autocorrect", autocorrect.map(toLastFmBoolean))
            .withOptionQueryParam("mbid", mbid)
            .withOptionQueryParam("page", page)
            .withOptionQueryParam("limit", limit)
        )

      override def getArtistTopTags(
          artist: String,
          mbid: Option[String],
          autocorrect: Option[Boolean]
      ): F[Either[ApiError, List[TagWithCount]]] =
        send[TopTags, List[TagWithCount]](
          uri
            .withQueryParams(Map("method" -> "artist.getTopTags", "artist" -> artist))
            .withOptionQueryParam("autocorrect", autocorrect.map(toLastFmBoolean))
            .withOptionQueryParam("mbid", mbid)
        )

      override def getArtistTopTracks(
          artist: String,
          mbid: Option[String],
          autocorrect: Option[Boolean],
          page: Option[Int],
          limit: Option[Int]
      ): F[Either[ApiError, List[TopTrack]]] =
        send[TopTracks, List[TopTrack]](
          uri
            .withQueryParams(Map("method" -> "artist.getTopTracks", "artist" -> artist))
            .withOptionQueryParam("autocorrect", autocorrect.map(toLastFmBoolean))
            .withOptionQueryParam("mbid", mbid)
            .withOptionQueryParam("page", page)
            .withOptionQueryParam("limit", limit)
        )

      override def searchArtists(
          artist: String,
          limit: Option[Int],
          page: Option[String]
      ): F[Either[ApiError, SearchResult[SearchArtist]]] =
        send[RootSearchResult[SearchArtist], SearchResult[SearchArtist]](
          uri
            .withQueryParams(Map("method" -> "artist.search", "artist" -> artist))
            .withOptionQueryParam("limit", limit)
            .withOptionQueryParam("page", page)
        )

      override def getTopArtistsChart(page: Option[Int], limit: Option[Int]): F[Either[ApiError, List[ChartArtist]]] =
        send[ChartArtists, List[ChartArtist]](
          uri
            .withQueryParams(Map("method" -> "chart.getTopArtists"))
            .withOptionQueryParam("limit", limit)
            .withOptionQueryParam("page", page)
        )

      override def getTopTagsChart(page: Option[Int], limit: Option[Int]): F[Either[ApiError, List[TagInfo]]] =
        send[RootTagInfo, List[TagInfo]](
          uri
            .withQueryParams(Map("method" -> "chart.getTopTags"))
            .withOptionQueryParam("limit", limit)
            .withOptionQueryParam("page", page)
        )

      override def getTopTracksChart(page: Option[Int], limit: Option[Int]): F[Either[ApiError, List[TopTrack]]] =
        send[ChartTracks, List[TopTrack]](
          uri
            .withQueryParams(Map("method" -> "chart.getTopTracks"))
            .withOptionQueryParam("limit", limit)
            .withOptionQueryParam("page", page)
        )
    }
  }
}
