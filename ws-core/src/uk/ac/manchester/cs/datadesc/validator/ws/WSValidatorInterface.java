/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.manchester.cs.datadesc.validator.ws;

import uk.ac.manchester.cs.datadesc.validator.rdftools.VoidValidatorException;

/**
 *
 * @author Christian
 */
public interface WSValidatorInterface {
    
    public String validate(String text, String uri, String rdfFormat, String specification, Boolean includeWarning)
            throws VoidValidatorException;      
    
}
