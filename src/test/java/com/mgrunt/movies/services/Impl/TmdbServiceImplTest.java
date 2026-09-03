package com.mgrunt.movies.services.Impl;

import com.mgrunt.movies.domain.dtos.tmdb.*;
import com.mgrunt.movies.exceptions.ExternalApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link TmdbServiceImpl}.
 * <p>
 * RestTemplate is mocked, so no real HTTP calls take place. The
 * {@code @Value}-injected fields (tmdbApiKey, tmdbBaseUrl) are set via
 * ReflectionTestUtils, since this class has no all-args constructor for them.
 */
@ExtendWith(MockitoExtension.class)
class TmdbServiceImplTest {

    private static final Long MOVIE_ID = 550L;
    private static final String BASE_URL = "https://api.themoviedb.org/3";
    private static final String API_KEY = "test-api-key";

    @Mock
    private RestTemplate restTemplate;

    private TmdbServiceImpl tmdbService;

    @BeforeEach
    void setUp() {
        tmdbService = new TmdbServiceImpl(restTemplate);
        ReflectionTestUtils.setField(tmdbService, "tmdbApiKey", API_KEY);
        ReflectionTestUtils.setField(tmdbService, "tmdbBaseUrl", BASE_URL);
    }

    private <T> void stubExchange(Class<T> responseType, T body) {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(responseType)))
                .thenReturn(ResponseEntity.ok(body));
    }

    private void stubExchangeThrows(Class<?> responseType, RuntimeException exception) {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(responseType)))
                .thenThrow(exception);
    }

    // ---------------------------------------------------------------
    // fetchRawMovieDetails
    // ---------------------------------------------------------------

    @Nested
    class FetchRawMovieDetails {

        @Test
        void returnsMovieDetails_onSuccess() {
            TmdbMovieDetailsResponse expected = mock(TmdbMovieDetailsResponse.class);
            stubExchange(TmdbMovieDetailsResponse.class, expected);

            TmdbMovieDetailsResponse result = tmdbService.fetchRawMovieDetails(MOVIE_ID);

            assertThat(result).isSameAs(expected);
        }

        @Test
        void callsCorrectUrl_withMovieIdAndLanguage() {
            stubExchange(TmdbMovieDetailsResponse.class, mock(TmdbMovieDetailsResponse.class));

            tmdbService.fetchRawMovieDetails(MOVIE_ID);

            ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
            verify(restTemplate).exchange(urlCaptor.capture(), eq(HttpMethod.GET), any(HttpEntity.class), eq(TmdbMovieDetailsResponse.class));
            assertThat(urlCaptor.getValue()).isEqualTo(BASE_URL + "/movie/" + MOVIE_ID + "?language=en-US");
        }

        @Test
        void wrapsFailure_inExternalApiException() {
            RuntimeException cause = new RuntimeException("connection refused");
            stubExchangeThrows(TmdbMovieDetailsResponse.class, cause);

            assertThatThrownBy(() -> tmdbService.fetchRawMovieDetails(MOVIE_ID))
                    .isInstanceOf(ExternalApiException.class)
                    .hasCause(cause);
        }
    }

    // ---------------------------------------------------------------
    // getMovieVideos
    // ---------------------------------------------------------------

    @Nested
    class GetMovieVideos {

        @Test
        void returnsResultsList_onSuccess() {
            TmdbVideoItemResponse video = mock(TmdbVideoItemResponse.class);
            TmdbVideosResponse response = mock(TmdbVideosResponse.class);
            when(response.results()).thenReturn(List.of(video));
            stubExchange(TmdbVideosResponse.class, response);

            List<TmdbVideoItemResponse> result = tmdbService.getMovieVideos(MOVIE_ID);

            assertThat(result).containsExactly(video);
        }

        @Test
        void returnsEmptyList_whenResponseBodyIsNull() {
            stubExchange(TmdbVideosResponse.class, null);

            List<TmdbVideoItemResponse> result = tmdbService.getMovieVideos(MOVIE_ID);

            assertThat(result).isEmpty();
        }

        @Test
        void returnsEmptyList_onException() {
            stubExchangeThrows(TmdbVideosResponse.class, new RuntimeException("timeout"));

            List<TmdbVideoItemResponse> result = tmdbService.getMovieVideos(MOVIE_ID);

            assertThat(result).isEmpty();
        }
    }

    // ---------------------------------------------------------------
    // getTrailerUrl
    // ---------------------------------------------------------------

    @Nested
    class GetTrailerUrl {

        private TmdbVideoItemResponse videoStub(String type, String site, Boolean official, String key) {
            TmdbVideoItemResponse video = mock(TmdbVideoItemResponse.class);
            lenient().when(video.type()).thenReturn(type);
            lenient().when(video.site()).thenReturn(site);
            lenient().when(video.official()).thenReturn(official);
            lenient().when(video.key()).thenReturn(key);
            return video;
        }

        private void stubVideos(List<TmdbVideoItemResponse> videos) {
            TmdbVideosResponse response = mock(TmdbVideosResponse.class);
            when(response.results()).thenReturn(videos);
            stubExchange(TmdbVideosResponse.class, response);
        }

        @Test
        void prefersOfficialYoutubeTrailer() {
            TmdbVideoItemResponse unofficial = videoStub("Trailer", "YouTube", false, "unofficial-key");
            TmdbVideoItemResponse official = videoStub("Trailer", "YouTube", true, "official-key");
            stubVideos(List.of(unofficial, official));

            String result = tmdbService.getTrailerUrl(MOVIE_ID);

            assertThat(result).isEqualTo("https://www.youtube.com/watch?v=official-key");
        }

        @Test
        void fallsBackToAnyYoutubeTrailer_whenNoOfficialOneExists() {
            TmdbVideoItemResponse unofficial = videoStub("Trailer", "YouTube", false, "unofficial-key");
            stubVideos(List.of(unofficial));

            String result = tmdbService.getTrailerUrl(MOVIE_ID);

            assertThat(result).isEqualTo("https://www.youtube.com/watch?v=unofficial-key");
        }

        @Test
        void ignoresNonTrailerAndNonYoutubeVideos() {
            TmdbVideoItemResponse featurette = videoStub("Featurette", "YouTube", true, "featurette-key");
            TmdbVideoItemResponse vimeoTrailer = videoStub("Trailer", "Vimeo", true, "vimeo-key");
            stubVideos(List.of(featurette, vimeoTrailer));

            String result = tmdbService.getTrailerUrl(MOVIE_ID);

            assertThat(result).isNull();
        }

        @Test
        void returnsNull_whenNoVideosExist() {
            stubVideos(List.of());

            String result = tmdbService.getTrailerUrl(MOVIE_ID);

            assertThat(result).isNull();
        }

        @Test
        void returnsNull_onException() {
            stubExchangeThrows(TmdbVideosResponse.class, new RuntimeException("timeout"));

            String result = tmdbService.getTrailerUrl(MOVIE_ID);

            assertThat(result).isNull();
        }
    }

    // ---------------------------------------------------------------
    // getImages
    // ---------------------------------------------------------------

    @Nested
    class GetImages {

        @Test
        void returnsFilePaths_upToLimit() {
            TmdbImageItemResponse bd1 = mock(TmdbImageItemResponse.class);
            TmdbImageItemResponse bd2 = mock(TmdbImageItemResponse.class);
            TmdbImageItemResponse bd3 = mock(TmdbImageItemResponse.class);
            when(bd1.filePath()).thenReturn("/bd1.jpg");
            when(bd2.filePath()).thenReturn("/bd2.jpg");
            lenient().when(bd3.filePath()).thenReturn("/bd3.jpg"); // never consumed due to limit(2)

            TmdbImagesResponse response = mock(TmdbImagesResponse.class);
            when(response.backdrops()).thenReturn(List.of(bd1, bd2, bd3));
            stubExchange(TmdbImagesResponse.class, response);

            List<String> result = tmdbService.getImages(MOVIE_ID, 2);

            assertThat(result).containsExactly("/bd1.jpg", "/bd2.jpg");
        }

        @Test
        void returnsEmptyList_whenResponseBodyIsNull() {
            stubExchange(TmdbImagesResponse.class, null);

            List<String> result = tmdbService.getImages(MOVIE_ID, 5);

            assertThat(result).isEmpty();
        }

        @Test
        void returnsEmptyList_whenBackdropsIsEmpty() {
            TmdbImagesResponse response = mock(TmdbImagesResponse.class);
            when(response.backdrops()).thenReturn(List.of());
            stubExchange(TmdbImagesResponse.class, response);

            List<String> result = tmdbService.getImages(MOVIE_ID, 5);

            assertThat(result).isEmpty();
        }
    }

    // ---------------------------------------------------------------
    // getTrendingMovies
    // ---------------------------------------------------------------

    @Nested
    class GetTrendingMovies {

        @Test
        void returnsResults_onSuccess() {
            TmdbTrendingMovieItemResponse movie1 = mock(TmdbTrendingMovieItemResponse.class);
            TmdbTrendingMovieItemResponse movie2 = mock(TmdbTrendingMovieItemResponse.class);
            TmdbTrendingMoviesResponse response = mock(TmdbTrendingMoviesResponse.class);
            when(response.results()).thenReturn(List.of(movie1, movie2));
            stubExchange(TmdbTrendingMoviesResponse.class, response);

            List<TmdbTrendingMovieItemResponse> result = tmdbService.getTrendingMovies();

            assertThat(result).containsExactly(movie1, movie2);
        }

        @Test
        void limitsResultsToTen() {
            List<TmdbTrendingMovieItemResponse> elevenMovies = java.util.stream.IntStream.range(0, 11)
                    .mapToObj(i -> mock(TmdbTrendingMovieItemResponse.class))
                    .toList();
            TmdbTrendingMoviesResponse response = mock(TmdbTrendingMoviesResponse.class);
            when(response.results()).thenReturn(elevenMovies);
            stubExchange(TmdbTrendingMoviesResponse.class, response);

            List<TmdbTrendingMovieItemResponse> result = tmdbService.getTrendingMovies();

            assertThat(result).hasSize(10);
            assertThat(result).containsExactlyElementsOf(elevenMovies.subList(0, 10));
        }

        @Test
        void returnsEmptyList_whenResponseBodyIsNull() {
            stubExchange(TmdbTrendingMoviesResponse.class, null);

            List<TmdbTrendingMovieItemResponse> result = tmdbService.getTrendingMovies();

            assertThat(result).isEmpty();
        }

        @Test
        void returnsEmptyList_onException() {
            stubExchangeThrows(TmdbTrendingMoviesResponse.class, new RuntimeException("timeout"));

            List<TmdbTrendingMovieItemResponse> result = tmdbService.getTrendingMovies();

            assertThat(result).isEmpty();
        }
    }

    // ---------------------------------------------------------------
    // searchResult
    // ---------------------------------------------------------------

    @Nested
    class SearchResult {

        @Test
        void returnsSearchResponse_onSuccess() {
            TmdbSearchResponse expected = mock(TmdbSearchResponse.class);
            stubExchange(TmdbSearchResponse.class, expected);

            TmdbSearchResponse result = tmdbService.searchResult("fight club", 1);

            assertThat(result).isSameAs(expected);
        }

        @Test
        void urlEncodesQueryWithSpaces() {
            stubExchange(TmdbSearchResponse.class, mock(TmdbSearchResponse.class));

            tmdbService.searchResult("fight club", 1);

            ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
            verify(restTemplate).exchange(urlCaptor.capture(), eq(HttpMethod.GET), any(HttpEntity.class), eq(TmdbSearchResponse.class));
            assertThat(urlCaptor.getValue())
                    .isEqualTo(BASE_URL + "/search/movie?query=fight+club&language=en-US&page=1");
        }

        @Test
        void returnsNull_onException() {
            stubExchangeThrows(TmdbSearchResponse.class, new RuntimeException("timeout"));

            TmdbSearchResponse result = tmdbService.searchResult("fight club", 1);

            assertThat(result).isNull();
        }
    }
}