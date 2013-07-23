/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.manchester.cs.openphacts.valdator.rdftools;

import java.io.InputStream;
import java.net.URL;
import org.junit.Ignore;

/**
 *
 * @author Christian
 */
@Ignore
public class TestUtils {
    
    public static void checkURI(String uri){
        try{
            URL URL = new URL(uri);
            InputStream stream = URL.openStream();
        }  catch (Exception ex){
            Reporter.println("********************************************************************************");
            Reporter.println("UNABLE to READ URI " + uri);
            Reporter.println("Check URI and that you are connected to the internet!");
            Reporter.println("SKIPPING TEST!!");
            Reporter.println("********************************************************************************");
            org.junit.Assume.assumeTrue(false);
        }

    }
}
