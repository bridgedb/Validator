/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.manchester.cs.openphacts.valdator.rdftools;

import org.junit.BeforeClass;

/**
 *
 * @author Christian
 */
public class FileInterfaceTest extends RdfInterfaceTest{
    @BeforeClass
    public static void setUpClass() throws VoidValidatorException {
        instance = RdfFactory.getTestFilebase();
    }
    
}
