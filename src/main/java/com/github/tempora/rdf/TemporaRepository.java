/**
 * Copyright 2017 Faissal Elamraoui
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.tempora.rdf;

import com.github.tempora.oauth.CurrentUser;
import com.github.tempora.svc.GMessage;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.vocabulary.FOAF;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;

@SpringComponent
@UIScope
public class TemporaRepository {

    @Autowired
    CurrentUser currentUser;

    private static final String DEFAULT_PREFIX = "https://www.gmail.com/";

    private Repository repository;
    private ValueFactory valueFactory;
    private Model temporaModel;

    @Autowired
    public TemporaRepository(CurrentUser currentUser) {
        this.repository = new SailRepository(new MemoryStore());
        this.repository.initialize();

        this.valueFactory = SimpleValueFactory.getInstance();

        this.temporaModel = new ModelBuilder().setNamespace("mail", DEFAULT_PREFIX)
                .subject("mail:" + currentUser.getUserIdentifier())
                .add(RDF.TYPE, FOAF.MBOX)
                .build();
    }

    /**
     * Stores a Gmail message in the repository.
     * @param message the {@link GMessage}.
     */
    public void storeMessage(GMessage message) {
        BNode msg = this.valueFactory.createBNode(message.getId());
        this.temporaModel.add(msg, FOAF.PERSON, this.valueFactory.createLiteral(message.getFrom()));
        this.temporaModel.add(msg, RDF.SUBJECT, this.valueFactory.createLiteral(message.getSubject()));
        this.temporaModel.add(msg, RDF.HTML, this.valueFactory.createLiteral(message.getBody()));
        this.temporaModel.add(msg, RDF.VALUE, this.valueFactory.createLiteral(message.getBodySize()));
    }

    /**
     * Stores an entire collection of {@link GMessage}(s).
     *
     * @param messages The collection of messages.
     */
    public void storeAll(Collection<GMessage> messages) {
        if (Objects.isNull(messages))
            throw new RuntimeException("Cannot store an empty collection of messages.");
        messages.parallelStream().forEach(m -> storeMessage(m));
    }

    /**
     * Returns all the statements in the underlying model.
     *
     * @return a {@link Set} of statements.
     */
    public synchronized Set<Statement> getStatements() {
        return this.temporaModel;
    }

}
