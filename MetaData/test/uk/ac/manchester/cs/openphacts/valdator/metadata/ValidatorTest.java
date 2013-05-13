/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.manchester.cs.openphacts.valdator.metadata;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
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
  
    static Validator validator;
    
    @BeforeClass
    public static void setUpMetaDataSpecification() throws VoidValidatorException {
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
