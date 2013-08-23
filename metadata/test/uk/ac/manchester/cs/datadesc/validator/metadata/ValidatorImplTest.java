/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.manchester.cs.datadesc.validator.metadata;

import org.junit.BeforeClass;
import uk.ac.manchester.cs.datadesc.validator.ValidatorImpl;
import uk.ac.manchester.cs.datadesc.validator.rdftools.VoidValidatorException;

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
