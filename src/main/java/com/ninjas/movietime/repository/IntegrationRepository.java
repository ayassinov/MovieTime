package com.ninjas.movietime.repository;

import com.google.common.base.Preconditions;
import com.ninjas.movietime.core.domain.APICallLog;
import com.ninjas.movietime.core.domain.People;
import com.ninjas.movietime.core.domain.movie.Genre;
import com.ninjas.movietime.core.domain.movie.Movie;
import com.ninjas.movietime.core.domain.showtime.Showtime;
import com.ninjas.movietime.core.domain.theater.Theater;
import com.ninjas.movietime.core.domain.theater.TheaterChain;
import com.ninjas.movietime.core.util.ExceptionManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * @author ayassinov on 02/09/2014.
 */
@Slf4j
@Repository
public class IntegrationRepository {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public IntegrationRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public void saveTheater(Collection<Theater> theaters) {
        for (final Theater theater : theaters) {
            saveTheater(theater);
        }
    }

    public void saveTheater(Theater theater) {
        Preconditions.checkNotNull(theater, "Theater to save cannot be null");
        Preconditions.checkNotNull(theater.getTheaterChain(), "Theater to save cannot have a null TheaterChain");
        try {
            mongoTemplate.save(theater.getTheaterChain());
            mongoTemplate.save(theater);
        } catch (Exception ex) {
            ExceptionManager.log(ex, "Error on saving Theater with id %s to mongodb", theater.getId());
        }
    }

    public void saveMovie(Movie movie) {
        Preconditions.checkNotNull(movie, "Movie to save cannot be null");
        try {
            for (People actor : movie.getActors())
                mongoTemplate.save(actor);

            for (People director : movie.getDirectors())
                mongoTemplate.save(director);

            for (People writer : movie.getWriters())
                mongoTemplate.save(writer);

            for (People producer : movie.getProducers())
                mongoTemplate.save(producer);

            for (Genre genre : movie.getGenres())
                mongoTemplate.save(genre);

            mongoTemplate.save(movie.getMovieType());
            mongoTemplate.save(movie);
        } catch (Exception ex) {
            ExceptionManager.log(ex, "Error on saving Movie title %s to mongodb", movie.getTitle());
        }
    }

    public void saveShowtime(Showtime showtime) {
        Preconditions.checkNotNull(showtime, "Showtime to save cannot be null");
        Preconditions.checkNotNull(showtime.getTheater(), "Showtime to save cannot have a theater not set");
        Preconditions.checkNotNull(showtime.getMovie(), "Showtime to save cannot have movie not set");
        try {
            //we only save a linked movie, as the theaters cannot be different fom what we have in database
            saveMovie(showtime.getMovie());
            mongoTemplate.save(showtime);
        } catch (Exception ex) {
            ExceptionManager.log(ex, "Error on saving Showtime for theater %s and movie %s to mongodb",
                    showtime.getTheater().getId(), showtime.getMovie().getId());
        }
    }

    public void saveAPICallLog(APICallLog apiCallLog) {
        Preconditions.checkNotNull(apiCallLog, "API Call log to save cannot be null");
        try {
            mongoTemplate.save(apiCallLog);
        } catch (Exception ex) {
            ExceptionManager.log(ex, "Error on saving API Call log for %s to mongo", apiCallLog.getOperation().name());
        }
    }

    public List<TheaterChain> listAllTheaterChain(boolean isOnlyTracked) {
        final List<TheaterChain> theaterChains;
        if (isOnlyTracked) {
            final Query theaterChainQuery = Query.query(Criteria.where("isTracked").is(true));
            theaterChains = mongoTemplate.find(theaterChainQuery, TheaterChain.class);
            log.debug("List only tracked chain theater, found {}", theaterChains.size());
        } else {
            theaterChains = mongoTemplate.findAll(TheaterChain.class);
            log.debug("List all theater chain theater, found {}", theaterChains.size());
        }
        return theaterChains;
    }

    public List<Theater> listOpenTheaterByTheaterChain(TheaterChain theaterChain, boolean idOnly) {
        final Query theaterQuery = Query.query(Criteria.where("theaterChain").is(theaterChain).and("isOpen").is(true));
        if (idOnly) {
            theaterQuery.fields().include("id");
            log.debug("List Open Theater by Theater Chain returning only id field.");
        }
        return mongoTemplate.find(theaterQuery, Theater.class);
    }

    public List<Movie> listMovieWithoutTimdbId() {
        final Query query = Query.query(Criteria.where("timdbLastUpdate").is(null));
        final List<Movie> movies = mongoTemplate.find(query, Movie.class);
        log.debug("Found {} Movies without timdb Id", movies.size());
        return movies;
    }

    public List<Movie> listMovieWithoutRottenTomatoesRating() {
        final Query query = Query.query(Criteria.where("rottenTomatoesLastUpdate").is(null).and("imdbId").ne(null));

        final List<Movie> movies = mongoTemplate.find(query, Movie.class);
        log.debug("Found {} Movies without Rotten tomatoes rating", movies.size());
        return movies;
    }

    public List<Movie> listMovieWithoutTrackTvInformation() {
        final Query query = Query.query(Criteria.where("traktLastUpdate").is(null).and("timdbId").ne(null));
        final List<Movie> movies = mongoTemplate.find(query, Movie.class);
        log.debug("Found {} Movies without trackTV information and rating", movies.size());
        return movies;
    }
}
