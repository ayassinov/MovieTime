package com.ninjas.movietime.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Preconditions;
import com.ninjas.movietime.core.domain.exception.CannotFindRottenTomatoesRatingException;
import com.ninjas.movietime.core.domain.movie.Movie;
import com.ninjas.movietime.core.util.DateUtils;
import com.ninjas.movietime.core.util.ExceptionManager;
import com.ninjas.movietime.integration.helpers.RequestBuilder;
import com.ninjas.movietime.integration.helpers.RestClientHelper;
import com.ninjas.movietime.integration.uri.RottenTomatoesURICreator;
import com.ninjas.movietime.integration.uri.URICreator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;

/**
 * @author ayassinov on 31/08/14.
 */
@Slf4j
@Service
public class RottenTomatoesAPI {

    private final RestClientHelper restClient;

    private final URICreator uriCreator;

    @Autowired
    public RottenTomatoesAPI(RestClientHelper restClient) {
        this.restClient = restClient;
        this.uriCreator = new RottenTomatoesURICreator();
    }

    public boolean updateMovieInformation(Movie movie) {
        Preconditions.checkNotNull(movie, "Movie to update cannot be null");
        Preconditions.checkNotNull(movie.getImdbId(), "Movie to update should have IMDB ID set");

        final URI uri = RequestBuilder.create(uriCreator, "movie_alias")
                .add("type", "imdb")
                .add("id", movie.getImdbId())
                .build();

        final JsonNode node = restClient.get(uri);
        if (!node.path("id").isMissingNode()) {
            movie.setRottenTomatoesId(node.path("id").asText());
            movie.getRating().setRottenCriticsRating(node.path("ratings").path("critics_score").asDouble());
            movie.getRating().setRottenUserRating(node.path("ratings").path("audience_score").asDouble());
            movie.setRottenTomatoesLastUpdate(DateUtils.now());

            log.debug("Information from Rotten Tomatoes found for the movie {}", movie.getTitle());
            return true;
        }

        ExceptionManager.log(new CannotFindRottenTomatoesRatingException("Movie not found on rotten tomatoes",
                        movie.getId(), movie.getImdbId(), movie.getTitle()),
                "Movie %s not found on RottenTomatoes API to get score updated, imdbID = %s",
                movie.getTitle(), movie.getImdbId());
        return false;
    }
}
