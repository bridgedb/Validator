/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.manchester.cs.openphacts.valdator.metadata;

import uk.ac.manchester.cs.openphacts.valdator.metadata.SpecificationsRegistry;
import uk.ac.manchester.cs.openphacts.valdator.metadata.MetaDataSpecification;
import java.io.File;
import static org.hamcrest.Matchers.*;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openrdf.model.Resource;
import uk.ac.manchester.cs.openphacts.valdator.rdftools.RdfFactory;
import uk.ac.manchester.cs.openphacts.valdator.rdftools.RdfReader;
import uk.ac.manchester.cs.openphacts.valdator.rdftools.Reporter;
import uk.ac.manchester.cs.openphacts.valdator.rdftools.VoidValidatorException;
import uk.ac.manchester.cs.openphacts.validator.Validator;

/**
 *
 * @author Christian
 */
public class ValidationTest {
    
    static RdfReader minReader;
    static Resource minContext;
    static MetaDataSpecification specifications;
    private static final boolean INCLUDE_WARNINGS = true;
   
    public ValidationTest() {
        
    }
    
    @BeforeClass
    public static void setUpClass() throws VoidValidatorException {
        File file = new File ("test-data/testMin.ttl");
        minReader = RdfFactory.getMemory();
        minContext = minReader.loadFile(file);
        specifications = SpecificationsRegistry.specificationByName("opsVoid");
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
        String result = Validator.validate(minReader, minContext, specifications, INCLUDE_WARNINGS);
        assertThat(result, endsWith(Validator.SUCCESS));
    }
    
 
}
