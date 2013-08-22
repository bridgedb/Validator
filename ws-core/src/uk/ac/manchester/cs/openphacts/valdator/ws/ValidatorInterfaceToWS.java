/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.manchester.cs.openphacts.valdator.ws;

import java.io.InputStream;
import uk.ac.manchester.cs.openphacts.valdator.rdftools.VoidValidatorException;
import uk.ac.manchester.cs.openphacts.validator.Validator;

/**
 *
 * @author Christian
 */
public class ValidatorInterfaceToWS implements Validator{

    private final WSValidatorInterface wsInterface;
    
    private static final String NO_TEXT = null;
    private static final String NO_URI = null;
    
    public ValidatorInterfaceToWS(WSValidatorInterface wsInterface){
        this.wsInterface = wsInterface;
    }
            
    @Override
    public String validateText(String text, String formatName, String specificationName, Boolean includeWarning) throws VoidValidatorException {
        return wsInterface.validate(text, NO_URI, formatName, specificationName, includeWarning);
    }

    @Override
    public String validateUri(String uri, String formatName, String specificationName, Boolean includeWarning) throws VoidValidatorException {
        return wsInterface.validate(NO_TEXT, uri, formatName, specificationName, includeWarning);
    }

    @Override
    public String validateInputStream(InputStream stream, String formatName, String specificationName, Boolean includeWarning) throws VoidValidatorException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
