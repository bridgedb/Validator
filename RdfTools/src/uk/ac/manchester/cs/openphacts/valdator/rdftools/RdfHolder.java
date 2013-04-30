// OpenPHACTS RDF Validator,
// A tool for validating and storing RDF.
//
// Copyright 2012-2013  Christian Y. A. Brenninkmeijer
// Copyright 2012-2013  University of Manchester
// Copyright 2012-2013  OpenPhacts
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// OpenPHACTS RDF Validator,
// A tool for validating and storing RDF.
//
// Copyright 2012-2013  Christian Y. A. Brenninkmeijer
// Copyright 2012-2013  University of Manchester
// Copyright 2012-2013  OpenPhacts
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
package uk.ac.manchester.cs.openphacts.valdator.rdftools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

/**
 * This is mainly for testing.
 * @author Christian
 */
public class RdfHolder implements RdfInterface, Cloneable{

    private final List<Statement> statements;
    
    public RdfHolder(){
        statements = new ArrayList<Statement>();
    }
    
    public RdfHolder(List<Statement> incomingStatements){
        this.statements = incomingStatements;
    }

    public RdfHolder(RdfInterface other, Resource context) throws VoidValidatorException{
        statements = new ArrayList<Statement>(other.getStatementList(null, null, null, context));
    }

    @Override
    public RdfHolder clone(){
        RdfHolder other = new RdfHolder();
        other.statements.addAll(this.statements);
        return other;
    }
    
    public void addStatement(Statement statement){
        statements.add(statement);
    }
    
    public void addStatements(Collection<Statement> newStatements) {
        statements.addAll(newStatements);
    }
    
    public void removeStatement(Statement statement){
        statements.remove(statement);
    }

     public void removeStatements(Collection<Statement> removeStatements){
        statements.removeAll(removeStatements);
    }

    @Override
    // Test that use this should not depend on importing
    public List<Statement> getStatementList(Resource subjectResource, URI predicate, Value object, Resource... contexts) throws VoidValidatorException {
        List<Statement> results = new ArrayList<Statement>();
        for (Statement statement:statements){
            if (requested(statement, subjectResource, predicate, object, contexts)){ 
                results.add(statement);
            }
        }
        return results;
    }
    
    private boolean requested(Statement statement, Resource subjectResource, URI predicate, Value object, 
            Resource... contexts){
        if (subjectResource != null && !statement.getSubject().equals(subjectResource)){
            return false;
        }
        if (predicate != null && !statement.getPredicate().equals(predicate)){
            return false;
        }
        if (object != null && !statement.getObject().equals(object)){
            return false;
        }
        if (contexts.length == 0){
            return true;
        }
        for (int i = 0; i < contexts.length; i++){
            if (contexts[i].equals(statement.getContext())){
                return true;
            }
        }
        return false;
    }

 }
