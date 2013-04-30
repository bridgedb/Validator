/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.manchester.cs.openphacts.valdator.rdftools;

import java.io.File;
import org.openrdf.repository.Repository;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

/**
 *
 * @author Christian
 */
public class RdfFactory {
   
    public static RdfReader getMemory() throws VoidValidatorException{
        Repository repository = new SailRepository(new MemoryStore());
        RdfReader rdfReader = RdfReader.factory(repository);
        return rdfReader;
    } 
}
