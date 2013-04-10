/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.manchester.cs.rdftools;

/**
 *
 * @author Christian
 */
public class VoidValidatorException extends Exception {

    public VoidValidatorException(String msg) {
        super(msg);
    }

    public VoidValidatorException(String msg, Exception ex) {
        super(msg, ex);
    }
    
}
