/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.manchester.cs.openphacts.valdator.rdftools;

import java.util.List;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

/**
 *
 * @author Christian
 */
public interface RdfInterface {
    
    public List<Statement> getStatementList(Resource subjectResource, URI predicate, Value object, 
            Resource... contexts) throws VoidValidatorException;

}
