/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.manchester.cs.metadata;

import java.util.List;
import org.openrdf.model.Resource;
import uk.ac.manchester.cs.rdftools.RdfReader;
import uk.ac.manchester.cs.rdftools.VoidValidatorException;

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
    boolean appendError(StringBuilder builder, RdfReader rdf, Resource resource, int tabLevel) throws VoidValidatorException {
        boolean result = false;
        for (MetaDataBase child:children){
            if (child.appendError(builder, rdf, resource, tabLevel)){
                result = result = true;
            }
        }
        return result;
    }

    @Override
    boolean hasRequiredValues(RdfReader rdf, Resource resource) throws VoidValidatorException {
        for (MetaDataBase child:children){
            if (!child.hasRequiredValues(rdf, resource)){
                return false;
            }
        }
        return true;
    }

    boolean isValid(RdfReader rdf, Resource resource) throws VoidValidatorException {
        for (MetaDataBase child:children){
            if (!child.isValid(rdf, resource)){
                return false;
            }
        }
        return true;
    }


}
