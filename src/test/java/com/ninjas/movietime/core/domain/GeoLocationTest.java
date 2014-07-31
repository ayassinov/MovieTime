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

package com.ninjas.movietime.core.domain;

import com.ninjas.movietime.BaseTest;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.springframework.test.util.MatcherAssertionErrors.assertThat;

/**
 * @author ayassinov on 16/07/14
 */
public class GeoLocationTest extends BaseTest{

    @Test
    public void testEquals() {
        final GeoLocation locA = new GeoLocation("tata", "sasa");
        final GeoLocation locB = new GeoLocation("tata", "sasa");
        final GeoLocation locC = new GeoLocation("titi", "sisi");
        assertThat(locA, equalTo(locB));
        assertThat(locA, not(equalTo(locC)));
    }

}
