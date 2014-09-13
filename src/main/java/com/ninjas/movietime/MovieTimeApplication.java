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

package com.ninjas.movietime;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author ayassinov on 11/07/14
 */
@Slf4j
@Configuration
@ComponentScan
@EnableAutoConfiguration
public class MovieTimeApplication implements CommandLineRunner {

    @Autowired
    private MovieTimeConfig config;

    @Autowired
    private ScheduledTasks scheduledTasks;

    public static void main(String[] args) throws Exception {
        SpringApplication.run(MovieTimeApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        if (!config.getApp().getMode().equals(MovieTimeConfig.RunModeEnum.PROD)) {
            final List<MovieTimeConfig.CronTask> cronTasks = config.getApp().getTasks();
            scheduledTasks.scheduleTasks(cronTasks);
        }
    }
}

