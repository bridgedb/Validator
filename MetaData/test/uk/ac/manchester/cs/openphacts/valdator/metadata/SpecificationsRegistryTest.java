/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.manchester.cs.openphacts.valdator.metadata;

import uk.ac.manchester.cs.openphacts.valdator.metadata.SpecificationsRegistry;
import java.util.Set;
import static org.hamcrest.Matchers.*;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.ac.manchester.cs.openphacts.valdator.rdftools.Reporter;
import uk.ac.manchester.cs.openphacts.valdator.rdftools.VoidValidatorException;

/**
 *
 * @author Christian
 */
public class SpecificationsRegistryTest {
    
    public SpecificationsRegistryTest() {
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

    /**
     * Test of specificationByName method, of class SpecificationsRegistry.
     */
    @Test
    public void testSpecification() throws VoidValidatorException {
        Reporter.println("specification");
        SpecificationsRegistry.init();
        Set<String> names = SpecificationsRegistry.getSpecificationNames();
        assertThat(names.size(), greaterThan(0));
    }
}
