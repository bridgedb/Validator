/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.manchester.cs.metadata;

import java.util.ArrayList;
import org.openrdf.model.Resource;
import uk.ac.manchester.cs.rdftools.RdfInterface;
import uk.ac.manchester.cs.rdftools.VoidValidatorException;

/**
 *
 * @author Christian
 */
class MetaDataAlternatives extends MetaDataBase {

    final ArrayList<MetaDataBase> children;
    public final static String INCLUDE_ALTERNATIVE = "Please included one of the alternative specified. For Example:\n";
    
    public MetaDataAlternatives(String name, String type, RequirementLevel requirementLevel, ArrayList<MetaDataBase> children) {
        this.children = children;
    }
    
    @Override
    boolean appendValidate(StringBuilder builder, RdfInterface rdf, Resource resource, boolean includeWarnings, 
            int tabLevel)  throws VoidValidatorException{
        boolean result = false;
        if (appendError(builder, rdf, resource, tabLevel)){
            return true;
        }
        if (hasRequiredValues(rdf, resource)){
            return false;
        }
        tab(builder, tabLevel);
        builder.append("ERROR: Missing values:\n ");
        appendRequirement(builder, rdf, resource, tabLevel + 1);
        return true;
    }

    @Override
    boolean appendError(StringBuilder builder, RdfInterface rdf, Resource resource, int tabLevel) throws VoidValidatorException {
        boolean result = false;
        for (MetaDataBase child:children){
            if (child.appendError(builder, rdf, resource, tabLevel)){
                result = result = true;
            }
        }
        return result;
    }

    @Override
    void appendRequirement(StringBuilder builder, RdfInterface rdf, Resource resource, int tabLevel) throws VoidValidatorException {
        tab(builder, tabLevel);
        builder.append (INCLUDE_ALTERNATIVE);
        children.get(0).appendRequirement(builder, rdf, resource, tabLevel + 1);
    }

    @Override
    boolean hasRequiredValues(RdfInterface rdf, Resource resource) throws VoidValidatorException {
        for (MetaDataBase child:children){
            if (child.hasRequiredValues(rdf, resource)){
                return true;
            }
        }
        return false;
    }
    
    @Override
    boolean isValid(RdfInterface rdf, Resource resource) throws VoidValidatorException {
        for (MetaDataBase child:children){
            if (child.isValid(rdf, resource)){
                return true;
            }
        }
        return false;
    }


}
