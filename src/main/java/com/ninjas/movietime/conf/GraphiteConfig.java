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

package com.ninjas.movietime.conf;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

/**
 * @author ayassinov on 28/07/2014.
 */
@Getter
@Setter
@ToString
public class GraphiteConfig {

    @NotEmpty(message = "ApiKey is mandatory")
    private String apiKey;

    @NotEmpty(message = "Host is mandatory")
    private String host;

    @NotEmpty(message = "Port is mandatory")
    private int port;

    @NotNull(message = "Activate should have a value of true or false")
    private boolean activate;
}
