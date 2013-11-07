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
package uk.ac.manchester.cs.datadesc.validator.metadata;

import java.util.ArrayList;
import org.openrdf.model.Resource;
import uk.ac.manchester.cs.datadesc.validator.RdfValidator;
import uk.ac.manchester.cs.datadesc.validator.rdftools.RdfInterface;
import uk.ac.manchester.cs.datadesc.validator.rdftools.VoidValidatorException;

/**
 *
 * @author Christian
 */
public class MetaDataGroup extends HasChildrenMetaData {

    public static final String INCLUDE_ALL = "Please included all in the list below:\n";
    
    public MetaDataGroup(String name, RequirementLevel requirementLevel, 
            ArrayList<MetaDataBase> childrenMetaData) {
        super(childrenMetaData);
    }
    
    @Override
    void appendRequirement(StringBuilder builder, RdfInterface rdf, Resource resource, Resource context, int tabLevel) throws VoidValidatorException {
        tab(builder,tabLevel);
        builder.append(INCLUDE_ALL);
        for (MetaDataBase child:getChildren()){
            child.appendRequirement(builder, rdf, resource, context, tabLevel + 1);
        }
    }

    @Override
    boolean appendValidate(StringBuilder builder, RdfInterface rdf, Resource resource, Resource context, 
            boolean includeWarnings, int tabLevel, RdfValidator validator) throws VoidValidatorException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    void describe(StringBuilder builder, int tabLevel) {
        tab(builder, tabLevel);
        builder.append("GROUP: ");
        builder.append("\n");
        describeChildren(builder, tabLevel + 1);
    }

}
