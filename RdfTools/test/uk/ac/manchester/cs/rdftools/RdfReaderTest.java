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
     * Test of loadFile method with a gz file, of class RdfReader.
     */
     

}//file:/C:/OpenPhacts/Validator/RdfTools/test-data/test.ttl.gz
