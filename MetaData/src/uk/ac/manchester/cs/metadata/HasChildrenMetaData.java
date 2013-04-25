/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.manchester.cs.metadata;

import java.util.List;
import org.openrdf.model.Resource;
import uk.ac.manchester.cs.rdftools.RdfInterface;
import uk.ac.manchester.cs.rdftools.VoidValidatorException;
import uk.ac.manchester.cs.validator.Validator;

/**
 *
 * @author Christian
 */
abstract class HasChildrenMetaData extends MetaDataBase {

    final List<MetaDataBase> children;

    HasChildrenMetaData(List<MetaDataBase> childrenMetaData){
        this.children = childrenMetaData;
    }

    @Override
    boolean appendError(StringBuilder builder, RdfInterface rdf, Resource resource, Resource context, int tabLevel, Validator validator) throws VoidValidatorException {
        boolean result = false;
        for (MetaDataBase child:children){
            if (child.appendError(builder, rdf, resource, context, tabLevel, validator)){
                result = result = true;
            }
        }
        return result;
    }

    @Override
    boolean hasRequiredValues(RdfInterface rdf, Resource resource, Resource context) throws VoidValidatorException {
        for (MetaDataBase child:children){
            if (!child.hasRequiredValues(rdf, resource, context)){
                return false;
            }
        }
        return true;
    }

    @Override
    boolean isValid(RdfInterface rdf, Resource resource, Resource context) throws VoidValidatorException {
        for (MetaDataBase child:children){
            if (!child.isValid(rdf, resource, context)){
                return false;
            }
        }
        return true;
    }


}
