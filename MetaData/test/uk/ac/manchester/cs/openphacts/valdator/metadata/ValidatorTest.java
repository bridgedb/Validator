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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openrdf.rio.RDFFormat;
import uk.ac.manchester.cs.openphacts.valdator.rdftools.Reporter;
import uk.ac.manchester.cs.openphacts.valdator.rdftools.TestUtils;
import uk.ac.manchester.cs.openphacts.valdator.rdftools.VoidValidatorException;
import uk.ac.manchester.cs.openphacts.validator.RdfValidator;
import uk.ac.manchester.cs.openphacts.validator.Validator;
import uk.ac.manchester.cs.openphacts.validator.ValidatorExampleConstants;

/**
 *
 * @author Christian
 */
public abstract class ValidatorTest {
  
    protected static Validator validator;
    
    @BeforeClass
    public static void setUpClass() throws VoidValidatorException {
       MetaDataSpecification.LoadSpecification(ValidatorExampleConstants.SIMPLE_FILE, 
               ValidatorExampleConstants.SIMPLE_NAME, ValidatorExampleConstants.SIMPLE_DESCRIPTION);
    }
   
    @Test
    public void testTextValidate() throws VoidValidatorException, FileNotFoundException, IOException {
        Reporter.println("TextValidate");
        String text;
        BufferedReader br = new BufferedReader(new FileReader("../MetaData/test-data/testSimple.ttl"));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
            text = sb.toString();
        } finally {
            br.close();
        }
        String result = validator.validateText(text, RDFFormat.TURTLE.getName(), 
                ValidatorExampleConstants.SIMPLE_NAME, Boolean.TRUE);
        assertThat(result,  endsWith(RdfValidator.SUCCESS)); 
    }

    @Test
    public void testURIValidate() throws VoidValidatorException, FileNotFoundException, IOException, URISyntaxException {
        Reporter.println("URIValidate");
        String uri = "https://github.com/openphacts/Validator/blob/master/MetaData/test-data/remoteTest.ttl";
        TestUtils.checkURI(uri);
        String result = validator.validateUri(uri, RDFFormat.TURTLE.getName(), 
                ValidatorExampleConstants.SIMPLE_NAME, Boolean.TRUE);
        assertThat(result,  endsWith(RdfValidator.SUCCESS)); 
        
    }
}
