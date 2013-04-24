/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.manchester.cs.metadata;

import java.util.List;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import uk.ac.manchester.cs.metadata.type.MetaDataType;
import uk.ac.manchester.cs.metadata.type.MetaDataTypeFactory;
import uk.ac.manchester.cs.rdftools.RdfInterface;
import uk.ac.manchester.cs.rdftools.VoidValidatorException;

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
    
    protected boolean appendIncorrectReport(StringBuilder builder, RdfInterface rdf, List<Statement> statements, 
            Resource context, int tabLevel) throws VoidValidatorException {
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
        List<Statement> statements = rdf.getDirectStatementList(resource, predicate, null, context);
        for (Statement statement:statements){
            if (!metaDataType.correctType(statement.getObject())){
                return false;
            }
        }
        return true;
    }

}
