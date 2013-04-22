/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.manchester.cs.metadata;

import java.io.File;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.ac.manchester.cs.rdftools.RdfInterface;
import uk.ac.manchester.cs.rdftools.RdfReader;
import uk.ac.manchester.cs.rdftools.VoidValidatorException;
import uk.ac.manchester.cs.validator.Validator;

/**
 *
 * @author Christian
 */
public class ValidationTest {
    
    public ValidationTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
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

    @Test
    public void testValidate() throws VoidValidatorException   {
        System.out.println("getLinkingPredicates");
        MetaDataSpecification specifications = new MetaDataSpecification();
        File file = new File ("test-data/testMin.ttl");
        RdfInterface reader = new RdfReader(file);
        Validator validator = new Validator(reader, specifications);
        System.out.println(validator.validate());
    }
    
    
}
