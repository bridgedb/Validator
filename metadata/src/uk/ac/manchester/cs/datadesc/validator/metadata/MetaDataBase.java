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

import java.util.Collection;
import java.util.Set;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import uk.ac.manchester.cs.datadesc.validator.RdfValidator;
import uk.ac.manchester.cs.datadesc.validator.rdftools.RdfInterface;
import uk.ac.manchester.cs.datadesc.validator.rdftools.VoidValidatorException;
import uk.ac.manchester.cs.datadesc.validator.result.Report;

/**
 *
 * @author Christian
 */
public abstract class MetaDataBase {
    public static int NO_CARDINALITY = -1;
    String name;

    abstract boolean appendValidate(StringBuilder builder, RdfInterface rdf, Resource resource, Resource context, boolean includeWarnings, 
            int tabLevel, RdfValidator validator)  throws VoidValidatorException;
    
    abstract boolean appendError(StringBuilder builder, RdfInterface rdf, Resource resource, Resource context, int tabLeve, RdfValidator validator)
            throws VoidValidatorException;

    abstract void appendRequirement(StringBuilder builder, RdfInterface rdf, Resource resource, Resource context, int tabLevel) 
            throws VoidValidatorException;

    abstract boolean hasRequiredValues(RdfInterface rdf, Resource resource, Resource context) throws VoidValidatorException;

    abstract boolean isValid(RdfInterface rdf, Resource resource, Resource context) throws VoidValidatorException;
 
    abstract void describe(StringBuilder builder, int tabLevel);

    abstract Set<URI> getPredicates();
    
    //abstract Report validate(RdfInterface rdf, Resource resource, Resource context) throws VoidValidatorException;
            
    final void addValue(StringBuilder builder, Value value, Resource context){
        if (value instanceof URI){
           URI uri = (URI)value;
           if (uri.getNamespace().startsWith(context.stringValue())){
               builder.append(uri.getLocalName());
           } else {
               builder.append("<");
               builder.append(uri.stringValue());
               builder.append(">");
           }
        } else{
            builder.append(value);        
        }
    }
    
    final void addStatement(StringBuilder builder, Statement statement, Resource context){
        addValue(builder, statement.getSubject(), context);
        builder.append(" ");
        addValue(builder, statement.getPredicate(), context);
        builder.append(" ");
        addValue(builder, statement.getObject(), context);
    }
    
    final void tab(StringBuilder builder, int tabLevel){
        for (int i = 0; i < tabLevel; i++){
            builder.append("\t");
        }
    }
    
}
