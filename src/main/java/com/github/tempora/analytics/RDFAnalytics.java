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

import com.github.tempora.svc.Utilities;
import com.vaadin.spring.annotation.SpringComponent;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.vocabulary.FOAF;
import org.eclipse.rdf4j.model.vocabulary.RDF;

import java.util.Collection;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

@SpringComponent
public class RDFAnalytics implements Analytics<Statement> {

    @Override
    public String getTop5Senders(Collection<Statement> elements) {
        return elements.parallelStream()
                .filter(stmt -> stmt.getPredicate() == FOAF.PERSON)
                .map(stmt -> stmt.getObject().stringValue())
                .collect(Collectors.groupingBy(s -> s, Collectors.counting()))
                .entrySet()
                .stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .map(e -> e.getKey())
                .collect(Collectors.joining("\n"));
    }

    @Override
    public String getTop5TitleTags(Collection<Statement> elements) {
        return elements.parallelStream()
                .filter(stmt -> stmt.getPredicate() == RDF.SUBJECT)
                .map(stmt -> Utilities.extractTag(stmt.getObject().stringValue()))
                .collect(Collectors.groupingBy(s -> s, Collectors.counting()))
                .entrySet()
                .stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .map(e -> e.getKey())
                .collect(Collectors.joining("\n"));
    }

    @Override
    public OptionalDouble getAverageBodySize(Collection<Statement> elements) {
        return elements.parallelStream()
                .filter(stmt -> stmt.getPredicate() == RDF.VALUE)
                .mapToInt(stmt -> Integer.parseInt(stmt.getObject().stringValue()))
                .average();
    }

}
