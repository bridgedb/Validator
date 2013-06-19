/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.manchester.cs.openphacts.valdator.ws;

import java.io.InputStream;
import uk.ac.manchester.cs.openphacts.valdator.rdftools.VoidValidatorException;

/**
 *
 * @author Christian
 */
public interface WSValidatorInterface {
    
    public String validate(String text, String uri, String rdfFormat, String specification, Boolean includeWarning)
            throws VoidValidatorException;      
    
    public String validate(InputStream stream, String rdfFormat, String specification, Boolean includeWarning)
            throws VoidValidatorException;   
    

}
