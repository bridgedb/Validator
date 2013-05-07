/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.TupleQueryResultHandler;
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
