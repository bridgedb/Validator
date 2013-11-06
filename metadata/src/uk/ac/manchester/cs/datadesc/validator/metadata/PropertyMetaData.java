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

import java.util.List;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import uk.ac.manchester.cs.datadesc.validator.RdfValidator;
import uk.ac.manchester.cs.datadesc.validator.metadata.type.MetaDataType;
import uk.ac.manchester.cs.datadesc.validator.metadata.type.MetaDataTypeFactory;
import uk.ac.manchester.cs.datadesc.validator.rdftools.RdfInterface;
import uk.ac.manchester.cs.datadesc.validator.rdftools.VoidValidatorException;

/**
 *
 * @author Christian
 */
class PropertyMetaData extends CardinalityMetaData {

    private final MetaDataType metaDataType;
    public final static String EXPECTED_TYPE = "Expected type: ";
    
    public PropertyMetaData(URI predicate, String type, int cardinality, RequirementLevel requirementLevel, String objectClass) 
            throws VoidValidatorException {
       super(predicate, cardinality, requirementLevel);
       metaDataType = MetaDataTypeFactory.factory(objectClass);
    }
    
    @Override
    protected boolean appendIncorrectReport(StringBuilder builder, RdfInterface rdf, List<Statement> statements, 
            Resource context, int tabLevel, RdfValidator validator) throws VoidValidatorException {
        boolean appended = false;
        for (Statement statement:statements){
            if (!metaDataType.correctType(statement.getObject())){
                tab(builder, tabLevel);
                builder.append("ERROR: Found: ");
                this.addStatement(builder, statement, context);
                builder.append("\n");            
                tab(builder, tabLevel+1);
                builder.append(EXPECTED_TYPE);            
                builder.append(getType());
                builder.append("\n");
                appended = true;
            }
        }
        return appended;
    }

    @Override
    protected String getType() {
        return metaDataType.getCorrectType();
    }

    @Override
    boolean isValid(RdfInterface rdf, Resource resource, Resource context) throws VoidValidatorException {
        List<Statement> statements = rdf.getStatementList(resource, predicate, null, context);
        if (!correctCardinality(statements)){
            return false;
        }
        for (Statement statement:statements){
            if (!metaDataType.correctType(statement.getObject())){
                return false;
            }
        }
        return true;
    }

    @Override
    void describe(StringBuilder builder, int tabLevel) {
        describeCardinality(builder, tabLevel);
        builder.append(metaDataType.getCorrectType());
    }

}
