/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.manchester.cs.metadata;

import java.util.List;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import uk.ac.manchester.cs.rdftools.RdfReader;
import uk.ac.manchester.cs.rdftools.RequirementLevel;
import uk.ac.manchester.cs.rdftools.VoidValidatorException;

/**
 *
 * @author Christian
 */
public class ResourceMetaData extends HasChildrenMetaData {

    private final URI type;
    
    ResourceMetaData(URI theType, List<MetaDataBase> childMetaData) {
        super(childMetaData);
        type = theType;
        System.out.println("creating ResourceMetaData for " + type);
    }

    @Override
    public boolean appendValidate(StringBuilder builder, RdfReader rdf, Resource resource, boolean includeWarning, 
        int tabLevel) throws VoidValidatorException {
        tab(builder, tabLevel);
        builder.append("Validation report for: (");
        this.addValue(builder, type);
        builder.append(") ");
        this.addValue(builder, resource);
        builder.append("\n");
        boolean ok = true;
        for (MetaDataBase child:children){
            if (child.appendValidate(builder, rdf, resource, includeWarning, tabLevel + 1)){
                ok = false;
            }
        }
        if (ok){
            builder.append("\tNo Errors found! \n");        
        }
        return true;
    }

    @Override
    void appendRequirement(StringBuilder builder, RdfReader rdf, Resource resource, int tabLevel) throws VoidValidatorException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

   
}
