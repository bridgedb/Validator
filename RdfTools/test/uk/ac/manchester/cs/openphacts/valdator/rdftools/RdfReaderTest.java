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
import java.io.File;
import java.util.List;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import org.junit.Test;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.resultio.sparqlxml.SPARQLResultsXMLWriter;

/**
 *
 * @author Christian
 */
public class RdfReaderTest {
    
     /**
     * Test of loadFile method, of class RdfReader.
     */
    @Test
    public void testLoadFile() throws Exception {
        Reporter.println("loadFile");
        File inputFile = new File("test-data/test.ttl");
        RdfReader instance = RdfFactory.getMemory();
        Resource expectedContext = new URIImpl(inputFile.toURI().toString());
        Resource context = instance.loadFile(inputFile);
        assertEquals(expectedContext, context);
        List<Statement> statements = instance.getStatementList(null, null, null, context);
        assertEquals(10, statements.size());
        URI person = new URIImpl("http://example.com/part1#person2");
        statements = instance.getStatementList(person);
        //7 statements as subject + one as object
        assertEquals(8, statements.size());
    }

    /**
     * Test of loadFile method with a gz file, of class RdfReader.
     */
    @Test
    public void testLoadFileGZ() throws Exception {
        Reporter.println("loadFileGZ");
        File inputFile = new File("test-data/test.ttl.gz");
        RdfReader instance = RdfFactory.getMemory();
        Resource expectedContext = new URIImpl(inputFile.toURI().toString());
        Resource context = instance.loadFile(inputFile);
        assertEquals(expectedContext, context);
        List<Statement> statements = instance.getStatementList(null, null, null, context);
        assertEquals(10, statements.size());
    }
    
    /**
     * Test of loadFile method, of class RdfReader.
     */
    @Test
    public void testRemote() throws Exception {
        Reporter.println("testRemote");
        File inputFile = new File("test-data/testPart1.ttl");
        RdfReader instance = RdfFactory.getMemory();
        Resource expectedContext = new URIImpl(inputFile.toURI().toString());
        Resource context = instance.loadFile(inputFile);
        assertEquals(expectedContext, context);
        List<Statement> statements = instance.getStatementList(null, null, null, context);
        assertEquals(7, statements.size());
        Resource subject = new URIImpl("http://example.com/part1#person2");
        URI predicate = new URIImpl("http://openphacts.cs.man.ac.uk:9090/Void/testOntology.owl#hasStreet");
        statements = instance.getDirectOnlyStatementList(subject, predicate, null, subject);
        assertEquals(0, statements.size());
        statements = instance.getStatementList(subject, predicate, null, subject);
        assertEquals(1, statements.size());        
    }
     
    /**
     * Test of loadFile method, of class RdfReader.
     */
    @Test
    public void testSubset() throws Exception {
        Reporter.println("testSubset");
        File inputFile = new File("test-data/testSubset.ttl");
        RdfReader instance = RdfFactory.getMemory();
        Resource expectedContext = new URIImpl(inputFile.toURI().toString());
        Resource context = instance.loadFile(inputFile);
        assertEquals(expectedContext, context);
        List<Statement> statements = instance.getStatementList(null, null, null, context);
        assertEquals(6, statements.size());
        Resource subject = new URIImpl("http://example.com#person1");
        URI predicate = new URIImpl("http://openphacts.cs.man.ac.uk:9090/Void/testOntology.owl#hasName");
        statements = instance.getStatementList(subject, predicate, null);
        assertEquals(1, statements.size());
        subject = new URIImpl("http://example.com#person2");
        statements = instance.getDirectOnlyStatementList(subject, predicate, null);
        assertEquals(0, statements.size());
        statements = instance.getStatementList(subject, predicate, null);
        assertEquals(1, statements.size());
    }

   /**
     * Test of loadFile method, of class RdfReader.
     */
    @Test
    public void testRunSparqlQuery() throws Exception {
        Reporter.println("runSparqlQuery");
        File inputFile = new File("test-data/test.ttl");
        RdfReader instance = RdfFactory.getMemory();
        Resource context = instance.loadFile(inputFile);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();  
        SPARQLResultsXMLWriter writer = new SPARQLResultsXMLWriter(stream);
        String queryString = "SELECT ?x ?y WHERE { ?x ?p ?y } ";
        instance.runSparqlQuery(queryString, writer);
        String result = new String(stream.toByteArray(), "UTF-8");
        assertThat(result.length(), greaterThan(100));
    }
    
   /**
     * Test of loadFile method, of class RdfReader.
     */
    @Test
    public void testRunSparqlQueryParts() throws Exception {
        Reporter.println("runSparqlQueryParts");
        RdfReader instance1 = RdfFactory.getMemory();
        File inputFile = new File("test-data/testPart1.ttl");
        instance1.loadFile(inputFile);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();  
        SPARQLResultsXMLWriter writer = new SPARQLResultsXMLWriter(stream);
        String queryString = "SELECT ?x ?y WHERE { ?x ?p ?y } ";
        instance1.runSparqlQuery(queryString, writer);
        String result1 = new String(stream.toByteArray(), "UTF-8");
        assertThat(result1.length(), greaterThan(100));
        RdfReader instance2 = RdfFactory.getMemory();
        inputFile = new File("test-data/testSubset.ttl");
        instance2.loadFile(inputFile);
        stream = new ByteArrayOutputStream();  
        writer = new SPARQLResultsXMLWriter(stream);
        instance2.runSparqlQuery(queryString, writer);
        String result2 = new String(stream.toByteArray(), "UTF-8");
        assertThat(result2.length(), greaterThan(100));
        assertThat(result1, not(result2));
        instance2.addOtherSource(instance1);
        stream = new ByteArrayOutputStream();  
        writer = new SPARQLResultsXMLWriter(stream);
        instance2.runSparqlQuery(queryString, writer);
        String result3 = new String(stream.toByteArray(), "UTF-8");
        assertThat(result3.length(), greaterThan(result2.length()));
    }
    
   /**
     * Test of loadFile method, of class RdfReader.
     */
    @Test
    public void testFileBased() throws Exception {
        Reporter.println("FileBased");
        File inputFile = new File("test-data/test.ttl");
        RdfReader instance = RdfFactory.getTestFilebase();
        Resource expectedContext = new URIImpl(inputFile.toURI().toString());
        Resource context = instance.loadFile(inputFile);
        assertEquals(expectedContext, context);
        List<Statement> statements = instance.getStatementList(null, null, null, context);
        //for (Statement statement:statements){
            //ystem.out.println(statement);
        //}
        assertEquals(10, statements.size());
    }

    /**
     * Test of loadFile method, of class RdfReader.
     */
    @Test
    public void testOtherRdfReader() throws Exception {
        Reporter.println("OtherRdfReader");
        File inputFile = new File("test-data/test.ttl");
        RdfReader instance1 = RdfFactory.getMemory();
        Resource context = instance1.loadFile(inputFile);
        Resource subject = new URIImpl("http://example.com/part1#person2");
        List<Statement> statements = instance1.getStatementList(subject, null, null, context);
        assertEquals(7, statements.size());
        RdfReader instance2 = RdfFactory.getMemory();
        statements = instance2.getStatementList(subject, null, null, context);
        assertEquals(0, statements.size()); 
        instance2.addOtherSource(instance1);
        statements = instance2.getStatementList(subject, null, null, context);
        assertEquals(7, statements.size()); 
    }

}