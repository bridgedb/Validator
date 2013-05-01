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

import java.io.File;
import java.util.List;
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
        statements = instance.getStatementList(subject, predicate, null, context);
        assertEquals(1, statements.size());
    }

}