/*
 * Copyright 2014 Parisian Ninjas
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

package com.ninjas.movietime.integration.allocine.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ninjas.movietime.core.domain.Movie;
import com.ninjas.movietime.core.domain.Showtime;
import com.ninjas.movietime.core.domain.Theater;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
import java.util.List;

/**
 * @author ayassinov on 31/07/2014.
 */
@Getter
@Setter
@ToString
public class FeedResponse {

    @JsonProperty(required = true)
    private int page;

    @JsonProperty(required = true)
    private int count;

    @JsonProperty(required = true)
    private int totalResults;

    @JsonProperty(required = false)
    private Date updated;

    @JsonProperty(required = false)
    private List<Theater> theater;

    @JsonProperty(required = false)
    private List<Movie> movie;

    @JsonProperty(value = "theaterShowtimes", required = false)
    private List<Showtime> showtime;


}
