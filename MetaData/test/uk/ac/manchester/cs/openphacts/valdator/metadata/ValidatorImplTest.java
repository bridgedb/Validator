/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.manchester.cs.openphacts.valdator.metadata;

import org.junit.BeforeClass;
import uk.ac.manchester.cs.openphacts.valdator.rdftools.VoidValidatorException;
import uk.ac.manchester.cs.openphacts.validator.ValidatorImpl;

/**
 *
 * @author Christian
 */
public class ValidatorImplTest extends ValidatorTest{
    
    @BeforeClass
    public static void setUpValidator() throws VoidValidatorException {
        validator = new ValidatorImpl();
    }

}
