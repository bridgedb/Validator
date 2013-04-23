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
import uk.ac.manchester.cs.rdftools.RdfReader;
import uk.ac.manchester.cs.rdftools.Reporter;
import uk.ac.manchester.cs.rdftools.VoidValidatorException;
import uk.ac.manchester.cs.validator.Validator;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 *
 * @author Christian
 */
public class ValidationTest {
    
    static RdfReader minReader;
    static MetaDataSpecification specifications;
   
    public ValidationTest() {
        
    }
    
    @BeforeClass
    public static void setUpClass() throws VoidValidatorException {
        File file = new File ("test-data/testMin.ttl");
        minReader = new RdfReader(file);
        specifications = new MetaDataSpecification("resources/VoidInfo.owl");
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
    public void minFileValidate() throws VoidValidatorException {
        Reporter.println("minFileValidate");
        Validator validator = new Validator(minReader, specifications);
        String result = validator.validate();
        assertThat(result,  endsWith(Validator.SUCCESS));
    }
    
 
}
