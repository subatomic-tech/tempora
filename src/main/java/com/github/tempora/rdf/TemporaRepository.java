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
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.impl.TreeModel;
import org.eclipse.rdf4j.model.vocabulary.FOAF;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

@SpringComponent
public class TemporaRepository {

    private static final String DEFAULT_PREFIX = "http://www.example.com/";

    @Autowired
    CurrentUser currentUser;

    private Repository repository;
    private ValueFactory valueFactory;

    private Model temporaModel;
    private IRI currentUserIRI;

    public TemporaRepository() {
        this.repository = new SailRepository(new MemoryStore());
        this.repository.initialize();

        this.valueFactory = SimpleValueFactory.getInstance();
        this.temporaModel = new TreeModel();
        this.currentUserIRI = this.valueFactory.createIRI(DEFAULT_PREFIX, currentUser.getUserIdentifier());

        this.temporaModel.add(this.currentUserIRI, RDF.TYPE, FOAF.MBOX);
    }

    /**
     * Stores a Gmail message in the repository.
     * @param message the {@link GMessage}.
     */
    public void storeMessage(GMessage message) {
        this.temporaModel.add(this.currentUserIRI, FOAF.PERSON, valueFactory.createLiteral(message.getFrom()));
        this.temporaModel.add(this.currentUserIRI, RDF.SUBJECT, valueFactory.createLiteral(message.getSubject()));
        this.temporaModel.add(this.currentUserIRI, RDF.VALUE, valueFactory.createLiteral(message.getBody()));
    }

    /**
     * Returns all the statements in the underlying model.
     *
     * @return a {@link Set} of statements.
     */
    public Set<Statement> getStatements() {
        return this.temporaModel;
    }

}
