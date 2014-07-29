/*
 * Copyright 2014 GlagSoftware
 *
 * Licensed under the MIT License;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://opensource.org/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ninjas.movietime;

import com.bugsnag.Client;
import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.ninjas.movietime.conf.ApplicationConfig;
import com.ninjas.movietime.conf.GraphiteConfig;
import com.ninjas.movietime.conf.HttpClientFactory;
import com.ninjas.movietime.conf.RunModeEnum;
import com.ninjas.movietime.core.jackson.FuzzyEnumModule;
import com.ninjas.movietime.core.jackson.GuavaExtrasModule;
import com.ninjas.movietime.core.jackson.MovieTimeJsonMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * @author ayassinov on 11/07/14
 */
@Configuration
@ComponentScan
@EnableAutoConfiguration
public class MovieTimeApplication implements CommandLineRunner {

    private static final Logger LOG = LoggerFactory.getLogger(MovieTimeApplication.class);

    @Autowired
    private ApplicationConfig configuration;

    public static void main(String[] args) throws Exception {
        SpringApplication.run(MovieTimeApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        configureMetrics();
    }

    private void configureMetrics() {
        final MetricRegistry registry = getRegistry();
        //set console reporter only on dev mode
        if (configuration.getMode().equals(RunModeEnum.DEV)) {
            final ConsoleReporter reporter = ConsoleReporter.forRegistry(registry)
                    .convertRatesTo(TimeUnit.SECONDS)
                    .convertDurationsTo(TimeUnit.MILLISECONDS)
                    .build();
            reporter.start(1, TimeUnit.MINUTES);
            LOG.info("Metrics console reporter bootstrapped");
        }

        //set graphite reporter
        final GraphiteConfig graphiteConfig = configuration.getGraphite();
        if (graphiteConfig.isActivate()) {
            final Graphite graphite = new Graphite(new InetSocketAddress(graphiteConfig.getHost(), graphiteConfig.getPort()));
            final GraphiteReporter graphiteReporter = GraphiteReporter.forRegistry(registry)
                    .prefixedWith(graphiteConfig.getApiKey())
                    .convertRatesTo(TimeUnit.SECONDS)
                    .convertDurationsTo(TimeUnit.MILLISECONDS)
                    .filter(MetricFilter.ALL)
                    .build(graphite);
            graphiteReporter.start(1, TimeUnit.MINUTES);
            LOG.info("Metrics graphite reporter bootstrapped");
        }
    }


    @Bean
    @Scope(value = "singleton")
    public MetricRegistry getRegistry() {
        return new MetricRegistry();
    }

    /**
     * Create a BugSnag Client, we rely on the configuration URL to create this object
     *
     * @return BugSnag Client
     */
    @Bean
    @Scope(value = "singleton")
    public Client getBugSnagClient() {
        Client bugSnag = new Client(configuration.getBugSnag());
        //set the current version
        bugSnag.setAppVersion(configuration.getVersion());
        //set the current release stage
        bugSnag.setReleaseStage(configuration.getMode().toString());
        //notify about exception only in production mode;
        bugSnag.setNotifyReleaseStages(RunModeEnum.PROD.toString());
        return bugSnag;
    }

    /**
     * Configure Jackson mapper with our preferences
     *
     * @return Jackson converter
     */
    @Bean
    public MappingJackson2HttpMessageConverter configureMapper() {
        final MovieTimeJsonMapper jsonMapper = new MovieTimeJsonMapper();
        jsonMapper.setObjectMapper(objectMapper());
        return jsonMapper;
    }

    /**
     * Return an Jackson Object Mapper to convert from/to Java Object to JsonNode types.
     *
     * @return Object mapper
     */
    @Bean
    public ObjectMapper objectMapper() {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JodaModule());
        objectMapper.registerModule(new FuzzyEnumModule());
        objectMapper.registerModule(new GuavaExtrasModule());
        objectMapper.registerModule(new GuavaModule());
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
        return objectMapper;
    }

    /**
     * @return MongoDB factory to get a mongo client connection.
     * @throws Exception When the URL mongo server is empty/null or Malformed
     */
    @Bean
    @Scope(value = "singleton")
    public MongoDbFactory mongoDbFactory() throws Exception {
        //parse the uri
        final MongoClientURI mongoClientURI = new MongoClientURI(configuration.getMongoUrl());
        //create mongodb client
        final MongoClient mongoClient = new MongoClient(mongoClientURI);
        //create spring mongodbFactory
        return new SimpleMongoDbFactory(mongoClient, mongoClientURI.getDatabase());
    }

    /**
     * @return Spring Mongo Template
     * @throws Exception if the MongoDB factory creation was not successfully
     */
    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        return new MongoTemplate(mongoDbFactory());
    }

    /**
     * Create a REST template to call an external API
     *
     * @return Spring REST template
     */
    @Bean
    public RestTemplate restTemplate() {
        //get HttpFactory
        final HttpComponentsClientHttpRequestFactory factory =
                HttpClientFactory.INSTANCE.getRequestFactory(getRegistry(), configuration.getClient());

        return new RestTemplate(factory);
    }

}

