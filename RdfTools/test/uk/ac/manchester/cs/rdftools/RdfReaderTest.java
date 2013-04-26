/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.manchester.cs.rdftools;

import java.io.File;
import java.util.List;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import org.junit.Test;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

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
        statements = instance.getDirectOnlyStatementList(null, null, null);
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
        statements = instance.getStatementList(subject, predicate, null, context);
        assertEquals(1, statements.size());
        subject = new URIImpl("http://example.com#person2");
        statements = instance.getDirectOnlyStatementList(subject, predicate, null, context);
        assertEquals(0, statements.size());
        statements = instance.getDirectOnlyStatementList(null, null, null);
        for (Statement statement:statements){
            System.out.println(statement);
        }
        statements = instance.getStatementList(subject, predicate, null, context);
        assertEquals(1, statements.size());
    }

}