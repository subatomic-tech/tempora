/* 
 * Copyright 2017 Faissal Elamraoui.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.tempora.analytics;

import com.github.tempora.svc.GMessage;
import com.github.tempora.svc.Utilities;
import com.vaadin.spring.annotation.SpringComponent;

import java.util.Collection;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

@SpringComponent
public class TemporaAnalytics implements Analytics<GMessage> {

    @Override
    public String getTop5Senders(Collection<GMessage> elements) {
        return elements.parallelStream()
                .map(msg -> msg.getFrom()) // Map the list to senders
                .collect(Collectors.groupingBy(s -> s, Collectors.counting()))
                .entrySet()
                .stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .map(e -> e.getKey())
                .collect(Collectors.joining("\n"));
    }

    @Override
    public String getTop5TitleTags(Collection<GMessage> elements) {
        return elements.parallelStream()
                .map(msg -> Utilities.extractTag(msg.getSubject()))
                .collect(Collectors.groupingBy(s -> s, Collectors.counting()))
                .entrySet()
                .stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .map(e -> e.getKey())
                .collect(Collectors.joining("\n"));
    }

    @Override
    public OptionalDouble getAverageBodySize(Collection<GMessage> elements) {
        return elements.parallelStream()
                .mapToInt(msg -> msg.getBodySize())
                .average();
    }

}
