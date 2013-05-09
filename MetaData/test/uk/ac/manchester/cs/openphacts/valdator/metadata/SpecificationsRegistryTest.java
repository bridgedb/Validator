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
package uk.ac.manchester.cs.openphacts.valdator.metadata;

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
import uk.ac.manchester.cs.openphacts.validator.ValidatorExampleConstants;

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
        MetaDataSpecification.LoadSpecification(ValidatorExampleConstants.SIMPLE_FILE, 
               ValidatorExampleConstants.SIMPLE_NAME, ValidatorExampleConstants.SIMPLE_DESCRIPTION);
        Set<String> names = MetaDataSpecification.getSpecificationNames();
        assertThat(names.size(), greaterThan(0));
    }
}
