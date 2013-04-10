/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.manchester.cs.metadata;

import java.util.Set;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import uk.ac.manchester.cs.rdftools.RdfReader;
import uk.ac.manchester.cs.rdftools.RequirementLevel;
import uk.ac.manchester.cs.rdftools.VoidValidatorException;

/**
 *
 * @author Christian
 */
class LinkedResource extends MetaDataBase {

    public LinkedResource(URI predicate, String type, int cardinality, RequirementLevel requirementLevel, Set<URI> linkedTypes, MetaDataSpecification aThis) {
    }

    @Override
    boolean appendValidate(StringBuilder builder, RdfReader rdf, Resource resource, boolean includeWarnings, int tabLevel) throws VoidValidatorException {
        tab(builder, tabLevel);
        builder.append ("TODO appendValidate LinkedResource\n");
        return true;
    }

    @Override
    boolean appendError(StringBuilder builder, RdfReader rdf, Resource resource, int tabLevel) throws VoidValidatorException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    boolean hasRequiredValues(RdfReader rdf, Resource resource) throws VoidValidatorException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    void appendRequirement(StringBuilder builder, RdfReader rdf, Resource resource, int tabLevel) throws VoidValidatorException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
