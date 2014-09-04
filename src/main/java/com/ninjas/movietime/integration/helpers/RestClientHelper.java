package com.ninjas.movietime.integration.helpers;

import com.codahale.metrics.Timer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import com.ninjas.movietime.core.util.ExceptionManager;
import com.ninjas.movietime.core.util.MetricManager;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;

/**
 * @author ayassinov on 26/08/2014.
 */
@Component
public class RestClientHelper {

    private final RestTemplate restTemplate;

    private final ObjectMapper objectMapper;

    private final String className = this.getClass().getCanonicalName();

    @Autowired
    public RestClientHelper(@NonNull RestTemplate restTemplate, @NonNull ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }


    public <T> T get(@NonNull final URI uri, Class<T> clazz) {
        final Optional<Timer.Context> timer = MetricManager.startTimer("get", uri.getPath());
        try {
            return restTemplate.getForObject(uri, clazz);
        } catch (RestClientException ex) {
            ExceptionManager.log(ex, "HTTP Response exception when calling %s", uri.toString());
            throw ex;
        } finally {
            MetricManager.stopTimer(timer);
        }
    }

    public JsonNode get(@NonNull final URI uri) {
        final Optional<Timer.Context> timer = MetricManager.startTimer("get", uri.getPath());
        try {
            final ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
            if (response.getStatusCode() != HttpStatus.OK)
                throw new RestClientException("HTTP Response NON OK. got = " + response.getStatusCode());

            return objectMapper.readTree(response.getBody());
        } catch (RestClientException | IOException ex) {
            ExceptionManager.log(ex, "HTTP Response exception or Parsing JSON text exception, when calling %s", uri.toString());
            throw new RestClientException("Json Response error.", ex);
        } finally {
            MetricManager.stopTimer(timer);
        }
    }
}
