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

import java.util.List;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import uk.ac.manchester.cs.openphacts.valdator.rdftools.RdfInterface;
import uk.ac.manchester.cs.openphacts.valdator.rdftools.VoidValidatorException;
import uk.ac.manchester.cs.openphacts.validator.Validator;

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
    public boolean appendValidate(StringBuilder builder, RdfInterface rdf, Resource resource, Resource context, 
            boolean includeWarning, int tabLevel, Validator validator) throws VoidValidatorException {
        tab(builder, tabLevel);
        builder.append("Validation report for: (");
        this.addValue(builder, type, context);
        builder.append(") ");
        this.addValue(builder, resource, context);
        builder.append("\n");
        boolean ok = true;
        for (MetaDataBase child:children){
            if (child.appendValidate(builder, rdf, resource, context, includeWarning, tabLevel + 1, validator)){
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
