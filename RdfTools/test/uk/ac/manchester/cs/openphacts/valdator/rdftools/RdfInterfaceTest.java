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

import java.io.ByteArrayOutputStream;
import java.util.List;
import static org.hamcrest.Matchers.*;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.resultio.sparqlxml.SPARQLResultsXMLWriter;
import org.openrdf.rio.RDFFormat;

/**
 *
 * @author Christian
 */
public abstract class RdfInterfaceTest {
    
    static RdfInterface instance;
    public static RDFFormat NO_FORMAT_SPECIFIED = null;
    public RdfInterfaceTest() {
    }

    @BeforeClass
    public static void setUpClass() throws VoidValidatorException {
        instance.loadURI("https://github.com/openphacts/Validator/blob/master/RdfTools/test-data/testPart1.ttl", null);
    }

    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getStatementList method, of class RdfInterface.
     */
    @Test
    public void testGetStatementList() throws Exception {
        Reporter.println("getStatementList");
        Resource subjectResource = null;
        URI predicate = null;
        Value object = null;
        List result = instance.getStatementList(subjectResource, predicate, object);
        assertThat(result.size(), greaterThan(6));
    }

    /**
     * Test of getDirectOnlyStatementList method, of class RdfInterface.
     */
    @Test
    public void testGetDirectOnlyStatementList() throws Exception {
        Reporter.println("getDirectOnlyStatementList");
        Resource subjectResource = null;
        URI predicate = null;
        Value object = null;
        List result = instance.getDirectOnlyStatementList(subjectResource, predicate, object);
        assertThat(result.size(), greaterThan(6));
    }

    /**
     * Test of getStatementList method, of class RdfInterface.
     */
    @Test
    public void testGetStatementList_Resource() throws Exception {
        Reporter.println("getStatementList_Resource");
        Resource resource = new URIImpl("http://example.com/part1#person2");
        List result = instance.getStatementList(resource);
        assertThat(result.size(), greaterThan(6));
    }

    /**
     * Test of runSparqlQuery method, of class RdfInterface.
     */
    @Test
    public void testRunSparqlQuery() throws Exception {
        Reporter.println("runSparqlQuery");
        ByteArrayOutputStream stream = new ByteArrayOutputStream();  
        SPARQLResultsXMLWriter writer = new SPARQLResultsXMLWriter(stream);
        String queryString = "SELECT ?x ?y WHERE { ?x ?p ?y } ";
        instance.runSparqlQuery(queryString, writer);
        String result = new String(stream.toByteArray(), "UTF-8");
        assertThat(result.length(), greaterThan(100));
    }

}
