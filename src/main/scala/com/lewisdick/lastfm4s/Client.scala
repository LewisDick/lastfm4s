package com.lewisdick.lastfm4s

import cats.Applicative
import cats.implicits._
import cats.effect.{ ConcurrentEffect, Sync }
import com.lewisdick.lastfm4s.domain.{
  AlbumInfo,
  ApiError,
  ArtistInfo,
  Root,
  RootAlbumInfo,
  RootArtistInfo,
  RootSearchResult,
  RootTopTags,
  SearchResult,
  TagWithCount
}
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import io.circe.Decoder
import org.http4s.QueryParamEncoder.stringQueryParamEncoder
import org.http4s.client.{Client => httpClient}
import org.http4s.implicits.http4sLiteralsSyntax
import org.http4s.{ EntityDecoder, Uri }
import io.circe.disjunctionCodecs.decoderEither
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
  ): F[Either[ApiError, SearchResult]]

  def getArtistInfo(
      artist: String,
      mbid: Option[String] = None,
      lang: Option[String] = None,
      autocorrect: Option[Boolean] = None,
      username: Option[String] = None
  ): F[Either[ApiError, ArtistInfo]]

  //TODO Add unsupported endpoints:
  //  def getAlbumTags
  //  def removeAlbumTags
  //  def addArtistTags
  //  def getArtistCorrection
  //  def getSimilarArtists
  //  def getArtistTags
  //  def getArtistTopAlbums
  //  def getArtistTopTags
  //  def getArtistTopTracks
  //  def removeArtistTag
  //  def searchArtists
  //  def getMobileSession
  //  def getSession
  //  def getToken
  //  def getTopArtistsChart
  //  def getTopTagsChart
  //  def getTopTracksChart
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
  //  def addTrackTags
  //  def getTrackCorrection
  //  def getTrackInfo
  //  def getSimilarTracks
  //  def getTrackTags
  //  def getTopTags
  //  def loveTrack
  //  def removeTrackTag
  //  def scrobble
  //  def searchTracks
  //  def unloveTrack
  //  def updateNowPlaying
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
  def apply[F[_]: Sync: ConcurrentEffect: Applicative](client: httpClient[F], apiKey: String)(implicit
      ec: ExecutionContext
  ): Client[F] = {
    val uri                                                   = uri"http://ws.audioscrobbler.com/2.0/?format=json".withQueryParam("api_key", apiKey)
    implicit val unitDecoder: Decoder[Either[ApiError, Unit]] = decoderEither[ApiError, Unit]

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
      ): F[Either[ApiError, SearchResult]] =
        send[RootSearchResult, SearchResult](
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

      override def getAlbumTopTags(
          artist: String,
          album: String,
          autocorrect: Option[Boolean],
          mbid: Option[String]
      ): F[Either[ApiError, List[TagWithCount]]] =
        send[RootTopTags, List[TagWithCount]](
          uri
            .withQueryParams(Map("method" -> "album.getTopTags", "artist" -> artist, "album" -> album))
            .withOptionQueryParam("autocorrect", autocorrect.map(toLastFmBoolean))
            .withOptionQueryParam("mbid", mbid)
        )

      private def send[T <: Root[E], E](uri: Uri)(implicit
          ed: EntityDecoder[F, Either[ApiError, T]]
      ): F[Either[ApiError, E]] =
        client.expect[Either[ApiError, T]](uri).map(_.map(_.get))

      private def toLastFmBoolean(bool: Boolean) =
        if (bool)
          "1"
        else
          "0"
    }
  }
}
