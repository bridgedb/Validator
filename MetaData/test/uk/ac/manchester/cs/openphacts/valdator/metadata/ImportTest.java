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

import java.io.File;
import java.util.List;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import uk.ac.manchester.cs.openphacts.valdator.rdftools.RdfFactory;
import uk.ac.manchester.cs.openphacts.valdator.rdftools.RdfReader;
import uk.ac.manchester.cs.openphacts.valdator.rdftools.Reporter;
import uk.ac.manchester.cs.openphacts.valdator.rdftools.VoidValidatorException;
import uk.ac.manchester.cs.openphacts.validator.Validator;
import uk.ac.manchester.cs.openphacts.validator.ValidatorExampleConstants;

/**
 *
 * @author Christian
 */
public class ImportTest {
    
    static MetaDataSpecification specifications;
   
    private static final Resource ALL_SUBJECTS = null;
    private static final URI ALL_PREDICATES = null;
    private static final Value ALL_OBJECTS = null;
    private static final boolean INCLUDE_WARNINGS = true;
    
    @BeforeClass
    public static void setUpClass() throws VoidValidatorException {
       MetaDataSpecification.LoadSpecification(ValidatorExampleConstants.SIMPLE_FILE, 
               ValidatorExampleConstants.SIMPLE_NAME, ValidatorExampleConstants.SIMPLE_DESCRIPTION);
       specifications = MetaDataSpecification.specificationByName(ValidatorExampleConstants.SIMPLE_NAME);
    }

    @Test
    public void testTwoPartValidate() throws VoidValidatorException {
        Reporter.println("TwoPartValidate");
        RdfReader reader = RdfFactory.getMemory();
        File part1 = new File ("test-data/testPart1.ttl");
        File part2 = new File ("test-data/testPart2.ttl");
        reader.loadFile(part1);
        List<Statement> list1 = reader.getDirectOnlyStatementList(ALL_SUBJECTS, ALL_PREDICATES, ALL_OBJECTS);
        Resource context = reader.loadFile(part2);
        List<Statement> list2 = reader.getDirectOnlyStatementList(ALL_SUBJECTS, ALL_PREDICATES, ALL_OBJECTS);
        assertThat(list2.size(), greaterThan(list1.size()));
        String result = Validator.validate(reader, context, specifications, INCLUDE_WARNINGS);
        assertThat(result,  endsWith(Validator.SUCCESS));
    }

    @Test
    public void testTwoPartMissingValidate() throws VoidValidatorException {
        Reporter.println("TwoPartMissingValidate");
        RdfReader reader = RdfFactory.getMemory();
        File part1 = new File ("test-data/testPart1Missing.ttl");
        File part2 = new File ("test-data/testPart2.ttl");
        reader.loadFile(part1);
        List<Statement> list1 = reader.getDirectOnlyStatementList(ALL_SUBJECTS, ALL_PREDICATES, ALL_OBJECTS);
        Resource context = reader.loadFile(part2);
        List<Statement> list2 = reader.getDirectOnlyStatementList(ALL_SUBJECTS, ALL_PREDICATES, ALL_OBJECTS);
        assertThat(list2.size(), greaterThan(list1.size()));
        String result = Validator.validate(reader, context, specifications, INCLUDE_WARNINGS);
        assertThat(result, containsString(LinkedResource.ERROR_SEE_REPORT));
        assertThat(result,  endsWith(Validator.FAILED));
    }
    
    @Test
    public void testSubset() throws VoidValidatorException {
        Reporter.println("Subset");
        RdfReader reader = RdfFactory.getMemory();
        File file = new File ("test-data/testSubset.ttl");
        Resource context = reader.loadFile(file);
        String result = Validator.validate(reader, context, specifications, INCLUDE_WARNINGS);
        assertThat(result,  endsWith(Validator.SUCCESS));
    }

}
