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

import com.ninjas.movietime.core.domain.TheaterChain;
import com.ninjas.movietime.service.TheaterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author ayassinov on 11/07/14
 */
@RestController
@RequestMapping("/theater")
public class TheaterResource {

    private final TheaterService theaterService;

    @Autowired
    public TheaterResource(TheaterService theaterService) {
        this.theaterService = theaterService;
    }

    @RequestMapping("/update")
    public String doUpdate() {
        this.theaterService.update();
        return "Done";
    }

    @RequestMapping("/chain")
    public List<TheaterChain> listAllWithTheaters(){
        return this.theaterService.listAllWithTheaters();
    }


}
