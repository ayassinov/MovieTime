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

package com.ninjas.movietime.core.domain.theater;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.joda.time.DateTime;

/**
 * @author ayassinov
 */
@Getter
@ToString
@JsonPropertyOrder({"dateStart", "dateEnd", "dateStartText", "dateEndText"})
@EqualsAndHashCode(of = {"dateStart", "dateEnd"})
public class ShutDownStatus {

    private final DateTime dateStart;

    private final DateTime dateEnd;

    public ShutDownStatus(DateTime dateStart, DateTime dateEnd) {
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
    }

    @JsonProperty
    public String getDateStartText() {
        return dateStart.toString();
    }

    @JsonProperty
    public String getDateEndText() {
        return dateEnd.toString();
    }
}
