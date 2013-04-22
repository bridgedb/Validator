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
class MetaDataGroup extends HasChildrenMetaData {

    public MetaDataGroup(String name, String type, RequirementLevel requirementLevel, 
            ArrayList<MetaDataBase> childrenMetaData) {
        super(childrenMetaData);
    }
    
    @Override
    void appendRequirement(StringBuilder builder, RdfInterface rdf, Resource resource, int tabLevel) throws VoidValidatorException {
        tab(builder,tabLevel);
        builder.append("Please included all in the list below:\n");
        for (MetaDataBase child:children){
            child.appendRequirement(builder, rdf, resource, tabLevel + 1);
        }
    }

    @Override
    boolean appendValidate(StringBuilder builder, RdfInterface rdf, Resource resource, boolean includeWarnings, int tabLevel) throws VoidValidatorException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
