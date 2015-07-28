package com.ninjas.movietime.batch.repository;

import com.google.common.base.Preconditions;
import com.ninjas.movietime.core.domain.People;
import com.ninjas.movietime.core.domain.movie.Genre;
import com.ninjas.movietime.core.domain.movie.Movie;
import com.ninjas.movietime.core.domain.showtime.Showtime;
import com.ninjas.movietime.core.domain.theater.Theater;
import com.ninjas.movietime.core.domain.theater.TheaterChain;
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
            //ExceptionManager.log(ex, "Error on saving Theater with id %s to mongodb", theater.getId());
        }
    }

    public void saveMovie(Movie movie) {
        Preconditions.checkNotNull(movie, "Movie to save cannot be null");
        try {
            movie.getStaff().getActors().forEach(mongoTemplate::save);

            movie.getStaff().getDirectors().forEach(mongoTemplate::save);

            movie.getStaff().getWriters().forEach(mongoTemplate::save);

            movie.getStaff().getProducers().forEach(mongoTemplate::save);

            movie.getGenres().forEach(mongoTemplate::save);

            if (movie.getMovieType() != null)
                mongoTemplate.save(movie.getMovieType());

            mongoTemplate.save(movie);
        } catch (Exception ex) {
            //ExceptionManager.log(ex, "Error on saving Movie title %s to mongodb", movie.getTitle());
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
          //  ExceptionManager.log(ex, "Error on saving Showtime for theater %s and movie %s to mongodb",
          //          showtime.getTheater().getId(), showtime.getMovie().getId());
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
        //shutDownStatus
        final Query theaterQuery = Query.query(
                Criteria.where("theaterChain").is(theaterChain));
        if (idOnly) {
            theaterQuery.fields().include("id");
            log.debug("List Open Theater by Theater Chain returning only id field.");
        }
        final List<Theater> theaters = mongoTemplate.find(theaterQuery, Theater.class);
        log.debug("List theaters that are open and by theater chain id={} found theaters={}", theaterChain.getId(), theaters.size());
        return theaters;
    }
}
