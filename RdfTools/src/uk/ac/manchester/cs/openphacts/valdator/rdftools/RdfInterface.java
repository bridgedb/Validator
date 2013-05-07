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

import java.util.List;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.TupleQueryResultHandler;
import org.openrdf.rio.RDFFormat;

/**
 *
 * @author Christian
 */
public interface RdfInterface {
    
    public List<Statement> getStatementList(Resource subjectResource, URI predicate, Value object, 
            Resource... contexts) throws VoidValidatorException;
    
    public List<Statement> getDirectOnlyStatementList(Resource subjectResource, URI predicate, Value object, 
            Resource...contexts) throws VoidValidatorException;
    
    /*
     * Find any Statement in any context that has this resource as either the subject or object.
     * Should look in Other RDFInterfaces associated but not read externally.
     * Also will not load from a parent resource (sush as void:subset) but only the parent(void:subset) declaration.
     */
    public List<Statement> getStatementList(Resource resource) throws VoidValidatorException;

    public void runSparqlQuery(String query, TupleQueryResultHandler handler) throws VoidValidatorException;

    public Resource loadURI(String address, RDFFormat format) throws VoidValidatorException;

 }
