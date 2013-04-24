/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.manchester.cs.metadata;

import java.util.List;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import uk.ac.manchester.cs.rdftools.RdfInterface;
import uk.ac.manchester.cs.rdftools.VoidValidatorException;

/**
 *
 * @author Christian
 */
public class ResourceMetaData extends HasChildrenMetaData {

    private final URI type;
    public static final String NO_ERRORS = "No Errors found!";
    
    ResourceMetaData(URI theType, List<MetaDataBase> childMetaData) {
        super(childMetaData);
        type = theType;
    }

    @Override
    public boolean appendValidate(StringBuilder builder, RdfInterface rdf, Resource resource, Resource context, boolean includeWarning, 
        int tabLevel) throws VoidValidatorException {
        tab(builder, tabLevel);
        builder.append("Validation report for: (");
        this.addValue(builder, type, context);
        builder.append(") ");
        this.addValue(builder, resource, context);
        builder.append("\n");
        boolean ok = true;
        for (MetaDataBase child:children){
            if (child.appendValidate(builder, rdf, resource, context, includeWarning, tabLevel + 1)){
                ok = false;
            }
        }
        if (ok){
            builder.append("\t " );  
            builder.append(NO_ERRORS);  
            builder.append("\n");  
            return false;
        }
        return true;
    }

    @Override
    void appendRequirement(StringBuilder builder, RdfInterface rdf, Resource resource, Resource context, int tabLevel) throws VoidValidatorException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

   
}
