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
package uk.ac.manchester.cs.datadesc.validator.rdftools.test;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.resultio.sparqlxml.SPARQLResultsXMLWriter;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

/**
 *
 * @author Christian
 */
public class SparqlTest {

    public static void main(String[] args) throws Exception {
        try {
            Repository repository = new SailRepository(new MemoryStore());
            repository.initialize();
            RepositoryConnection con = repository.getConnection();
            URI test = new URIImpl("http://example.com");
            Statement newStatement = 
                new StatementImpl(test, test, test);
            con.add(newStatement);
            try {
                String queryString = "SELECT ?x ?y WHERE { ?x ?p ?y } ";
                TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
                TupleQueryResult result = tupleQuery.evaluate();
                try {
                    SPARQLResultsXMLWriter writer = new SPARQLResultsXMLWriter(System.out);
                    writer.startQueryResult(result.getBindingNames());
                    while (result.hasNext()){
                        writer.handleSolution(result.next());
                    }
                    writer.endQueryResult();
                } finally {
                    result.close();
                }
            } finally {
                con.close();
            }
        } catch (OpenRDFException e) {
            e.printStackTrace();
        }   
    }
}
