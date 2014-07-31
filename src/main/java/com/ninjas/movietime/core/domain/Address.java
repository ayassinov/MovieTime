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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.mongodb.core.index.Indexed;

/**
 * @author ayassinov on 25/07/14
 */
@Getter
@ToString
@EqualsAndHashCode(of = {"address", "city", "postalCode"})
public class Address {

    private final String address;

    @Indexed
    private final String city;

    @Indexed
    private final String postalCode;

    @JsonCreator
    public Address(@JsonProperty("address") String address,
                   @JsonProperty("city") String city,
                   @JsonProperty("postalCode") String postalCode) {
        this.address = address;
        this.city = city;
        this.postalCode = postalCode;
    }
}
