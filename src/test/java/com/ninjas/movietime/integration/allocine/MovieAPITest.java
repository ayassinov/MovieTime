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

package com.ninjas.movietime.integration.allocine;

import com.google.common.base.Optional;
import com.ninjas.movietime.core.domain.Movie;
import com.ninjas.movietime.integration.BaseAlloCineTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**
 * @author ayassinov on 17/07/14
 */
public class MovieAPITest extends BaseAlloCineTest {

    @Autowired
    private MovieAPI movieAPI;

    @Test
    public void testGetMovie() throws Exception {
        final Optional<Movie> response = movieAPI.findById("143067");
        Assert.assertThat(response.isPresent(), is(true));
        Assert.assertThat(response.get().getCode(), equalTo("143067"));
    }

    @Test
    public void testSearch() {
        final String response = movieAPI.findByName("atlas", 10);
        checkAlloCineAPIResponseError(response, "\"originalTitle\":\"Cloud Atlas\"");
    }
}
