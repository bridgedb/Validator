// OpenPHACTS RDF Validator,
// A tool for validating and storing RDF.
//
// Copyright 2012-2013  Christian Y. A. Brenninkmeijer
// Copyright 2012-2013  University of Manchester
// Copyright 2012-2013  OpenPhacts
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
package uk.ac.manchester.cs.openphacts.valdator.metadata;

import java.util.ArrayList;
import org.openrdf.model.Resource;
import uk.ac.manchester.cs.openphacts.valdator.rdftools.RdfInterface;
import uk.ac.manchester.cs.openphacts.valdator.rdftools.VoidValidatorException;
import uk.ac.manchester.cs.openphacts.validator.RdfValidator;

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
    boolean appendValidate(StringBuilder builder, RdfInterface rdf, Resource resource, Resource context, 
            boolean includeWarnings, int tabLevel, RdfValidator validator)  throws VoidValidatorException{
        boolean result = false;
        if (appendError(builder, rdf, resource, context, tabLevel, validator)){
            return true;
        }
        if (hasRequiredValues(rdf, resource, context)){
            return false;
        }
        tab(builder, tabLevel);
        builder.append("ERROR: Missing values:\n ");
        appendRequirement(builder, rdf, resource, context, tabLevel + 1);
        return true;
    }

    @Override
    boolean appendError(StringBuilder builder, RdfInterface rdf, Resource resource, Resource context, int tabLevel,
            RdfValidator validator) throws VoidValidatorException {
        boolean result = false;
        for (MetaDataBase child:children){
            if (child.appendError(builder, rdf, resource, context, tabLevel,  validator)){
                result = result = true;
            }
        }
        return result;
    }

    @Override
    void appendRequirement(StringBuilder builder, RdfInterface rdf, Resource resource, Resource context, int tabLevel) throws VoidValidatorException {
        tab(builder, tabLevel);
        builder.append (INCLUDE_ALTERNATIVE);
        children.get(0).appendRequirement(builder, rdf, resource, context, tabLevel + 1);
    }

    @Override
    boolean hasRequiredValues(RdfInterface rdf, Resource resource, Resource context) throws VoidValidatorException {
        for (MetaDataBase child:children){
            if (child.hasRequiredValues(rdf, resource, context)){
                return true;
            }
        }
        return false;
    }
    
    @Override
    boolean isValid(RdfInterface rdf, Resource resource, Resource context) throws VoidValidatorException {
        for (MetaDataBase child:children){
            if (child.isValid(rdf, resource, context)){
                return true;
            }
        }
        return false;
    }


}
