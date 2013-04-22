/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.manchester.cs.metadata;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import uk.ac.manchester.cs.rdftools.RdfReader;
import uk.ac.manchester.cs.rdftools.RequirementLevel;
import uk.ac.manchester.cs.rdftools.VoidValidatorException;

/**
 *
 * @author Christian
 */
abstract class MetaDataBase {
    public static int NO_CARDINALITY = -1;
    String name;

    abstract boolean appendValidate(StringBuilder builder, RdfReader rdf, Resource resource, boolean includeWarnings, 
            int tabLevel)  throws VoidValidatorException;
    
    abstract boolean appendError(StringBuilder builder, RdfReader rdf, Resource resource, int tabLevel)
            throws VoidValidatorException;

    abstract void appendRequirement(StringBuilder builder, RdfReader rdf, Resource resource, int tabLevel) 
            throws VoidValidatorException;

    abstract boolean hasRequiredValues(RdfReader rdf, Resource resource) throws VoidValidatorException;

    abstract boolean isValid(RdfReader rdf, Resource resource) throws VoidValidatorException;
 
    final void addValue(StringBuilder builder, Value value){
        if (value instanceof URI){
           URI uri = (URI)value;
           if (uri.getNamespace().startsWith(RdfReader.DEFAULT_BASE_URI)){
               builder.append(uri.getLocalName());
           } else {
               builder.append("<");
               builder.append(uri.stringValue());
               builder.append(">");
           }
        } else{
            builder.append(value);        
        }
    }
    
    final void addStatement(StringBuilder builder, Statement statement){
        addValue(builder, statement.getSubject());
        builder.append(" ");
        addValue(builder, statement.getPredicate());
        builder.append(" ");
        addValue(builder, statement.getObject());
    }
    
    final void tab(StringBuilder builder, int tabLevel){
        for (int i = 0; i < tabLevel; i++){
            builder.append("\t");
        }
    }


}
