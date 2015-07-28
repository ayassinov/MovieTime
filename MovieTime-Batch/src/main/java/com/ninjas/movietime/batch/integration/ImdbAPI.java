package com.ninjas.movietime.batch.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.ninjas.movietime.core.domain.movie.Movie;
import com.ninjas.movietime.core.util.DateUtils;
import com.ninjas.movietime.core.util.StringUtils;
import com.ninjas.movietime.batch.integration.exception.CannotFindIMDIdException;
import com.ninjas.movietime.batch.integration.helpers.RequestBuilder;
import com.ninjas.movietime.batch.integration.helpers.RestClientHelper;
import com.ninjas.movietime.batch.integration.uri.URICreator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.net.URI;

/**
 * @author ayassinov on 31/08/14.
 */
@Slf4j
@Service
public class ImdbAPI {
    private final RestClientHelper restClient;

    private final URICreator uriCreator;

    @Autowired
    public ImdbAPI(RestClientHelper restClient, @Qualifier("imdbURICreator") URICreator uriCreator) {
        this.restClient = restClient;
        this.uriCreator = uriCreator;
    }

    public void updateMovieInformation(Movie movie, int movieYear) {
        final URI uri = RequestBuilder.
                create(uriCreator, "search/movie")
                .add("query", StringUtils.encode(movie.getTitle()))
                .add("year", movieYear)
                .build();

        final JsonNode node = restClient.get(uri);
        if (node.path("total_results").asInt() > 0) {
            movie.setTimdbId(node.path("results").get(0).path("id").asText());
            movie.setImdbTitle(node.path("results").get(0).path("title").asText()); //second Title
            movie.getRating().setImdbRating(node.path("results").get(0).path("vote_average").asDouble());
            movie.getRating().setImdbVoteCount(node.path("results").get(0).path("vote_count").asInt());
            movie.getMovieUpdateStatus().setLastImdbUpdate(DateUtils.nowServerDateTime());
            log.debug("Information from IMDB found for the movie {}", movie.getTitle());
        } else {

            throw new CannotFindIMDIdException("Cannot find movie id=% title=%s", movie.getId(), movie.getTitle());
        }

    }
}
