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

package com.ninjas.movietime.resource;

import com.google.common.base.Optional;
import com.ninjas.movietime.core.domain.theater.Theater;
import com.ninjas.movietime.core.domain.theater.TheaterChain;
import com.ninjas.movietime.core.util.MetricManager;
import com.ninjas.movietime.service.TheaterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.geo.GeoPage;
import org.springframework.web.bind.annotation.*;

/**
 * @author ayassinov on 11/07/14
 */
@RestController
@RequestMapping("api/v1/theater")
public class TheaterResource {

    private final TheaterService theaterService;

    @Autowired
    public TheaterResource(TheaterService theaterService) {
        this.theaterService = theaterService;
    }

    /**
     * List page theater that are open and affiliated to a theater chain company that we track
     *
     * @param page the page from with we start
     * @param size the size of the page
     * @return Page of a theater
     */
    @RequestMapping(method = RequestMethod.GET)
    public Page<Theater> all(@RequestParam(value = "page", defaultValue = "0") int page,
                             @RequestParam(value = "size", defaultValue = "10") int size) {
        MetricManager.markResourceMeter("theater", "all");
        return theaterService.listAll(page, size);
    }


    public GeoPage<Theater> listByLatLong(@RequestParam(value = "lat") double latitude,
                                       @RequestParam("long") double longitude,
                                       @RequestParam(value = "page", defaultValue = "0") int page,
                                       @RequestParam(value = "size", defaultValue = "10") int size) {

        return theaterService.listByGeoLocation(latitude, longitude, page, size);
    }

    @RequestMapping(value = "chain", method = RequestMethod.GET)
    public Page<TheaterChain> listTheaterChain(@RequestParam(value = "page", required = true, defaultValue = "0") int page,
                                               @RequestParam(value = "size", required = true, defaultValue = "10") int size) {
        return theaterService.listAllTheaterChain(page, size);
    }

    @RequestMapping(value = "chain/{id}", method = RequestMethod.GET)
    public Optional<TheaterChain> getTheaterChain(@PathVariable("id") String id) {
        return theaterService.getTheaterChain(id);
    }
}
