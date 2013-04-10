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
import uk.ac.manchester.cs.rdftools.RdfReader;
import uk.ac.manchester.cs.rdftools.RequirementLevel;
import uk.ac.manchester.cs.rdftools.VoidValidatorException;

/**
 *
 * @author Christian
 */
class PropertyMetaData extends MetaDataBase {

    private final URI predicate;
    private final MetaDataType metaDataType;
    private final int cardinality;
    private final RequirementLevel requirementLevel;
    
    public PropertyMetaData(URI predicate, String type, int cardinality, RequirementLevel requirementLevel, String objectClass) 
            throws VoidValidatorException {
       this.predicate = predicate;
       metaDataType = MetaDataTypeFactory.factory(objectClass);
       this.cardinality = cardinality;
       this.requirementLevel = requirementLevel;
    }
    
    @Override
    boolean appendError(StringBuilder builder, RdfReader rdf, Resource resource, int tabLevel) throws VoidValidatorException {
        List<Statement> statements = rdf.getStatementList(resource, predicate, null);
        boolean result = appendIncorrectType(builder, statements, tabLevel);
        if (cardinality != NO_CARDINALITY && statements.size() >= cardinality) {
            //Found too many statements so this is always an error.
            if (incorrectNumberOfStatements(builder, statements, tabLevel)){
                result = true;
            }
        }
        return result;
    }
    
    @Override
    boolean appendValidate(StringBuilder builder, RdfReader rdf, Resource resource, boolean includeWarnings, 
            int tabLevel) throws VoidValidatorException {
        List<Statement> statements = rdf.getStatementList(resource, predicate, null);
        boolean result = appendIncorrectType(builder, statements, tabLevel);
        if (appendCardinalityReport(builder, statements, includeWarnings, tabLevel)){
            result = true;
        }
        return result;
    }

    @Override
    void appendRequirement(StringBuilder builder, RdfReader rdf, Resource resource, int tabLevel) throws VoidValidatorException {
        List<Statement> statements = rdf.getStatementList(resource, predicate, null);
        if (this.hasRequiredValues(statements)){
            tab(builder, tabLevel);
            builder.append("You already correctly have ");
            builder.append(statements.size() );
            builder.append(" statements ");
            builder.append(" with predicate  ");
            builder.append(predicate);
            builder.append("\n");
        } else {
            appendRequirement(builder, tabLevel);
        }
    }
        
    private void appendRequirement(StringBuilder builder, int tabLevel) {
        tab(builder, tabLevel);
        if (cardinality == NO_CARDINALITY){
            builder.append("Please add one or more statement with predicate ");
        } else if (cardinality == 1){
            builder.append("Please add exactly one statement with predicate ");
        } else {
            builder.append("Please add exactly ");
            builder.append(cardinality);
            builder.append(" statements with predicate ");
        }
        this.addValue(builder, predicate);
        builder.append(" and type ");
        builder.append(metaDataType.getCorrectType());
        builder.append("\n");
    }
    
    private boolean appendIncorrectType(StringBuilder builder, List<Statement> statements, int tabLevel) {
        boolean appended = false;
        for (Statement statement:statements){
            if (!metaDataType.correctType(statement.getObject())){
                tab(builder, tabLevel);
                builder.append("ERROR: Found: ");
                this.addStatement(builder, statement);
                builder.append("\n");            
                tab(builder, tabLevel+1);
                builder.append("Expected type: ");            
                builder.append(metaDataType.getCorrectType());
                builder.append("\n");
                appended = true;
            }
        }
        return appended;
    }

    private boolean appendCardinalityReport(StringBuilder builder, List<Statement> statements, boolean includeWarnings, 
            int tabLevel) throws VoidValidatorException{
        if (statements.isEmpty()){
            switch (requirementLevel){
                case MUST: return appendNoStatements(builder, "ERROR:", tabLevel);
                case SHOULD: {
                    if (includeWarnings){
                        return appendNoStatements(builder, "Warning:", tabLevel);
                    } else {
                        return false; //No request to report a warning
                    }
                }
                case MAY:return false; //No need to report a missing optioal
                default: throw new VoidValidatorException ("Unexpected RequirementLevel; " + requirementLevel);
            }
        }
        if (cardinality == NO_CARDINALITY || statements.size() == cardinality) {
            return false; // Found correct number of state
        }
        //Found some statements but not the right number so always an ERROR:
        return incorrectNumberOfStatements(builder, statements, tabLevel);
    }
    
    private boolean appendNoStatements(StringBuilder builder, String level, int tabLevel) {
        tab(builder, tabLevel);
        builder.append(level);
        builder.append(" No statements found with predicate: " );
        this.addValue(builder, predicate);
        builder.append("\n");
        appendRequirement(builder, tabLevel + 1);
        return true;
    }

    private boolean incorrectNumberOfStatements(StringBuilder builder, List<Statement> statements, int tabLevel) {
        tab(builder, tabLevel);
        builder.append("ERROR:Looking for " );
        builder.append(cardinality);
        builder.append(" statements with type ");
        this.addValue(builder, predicate);
        builder.append("\n");
        tab(builder, tabLevel + 1);
        if (cardinality > statements.size()){
            builder.append("Only found ");
            builder.append(statements.size());
            builder.append(". Please add ");
            builder.append(cardinality - statements.size());
            builder.append(" statement(s).\n");
        } else {
            builder.append("However found ");
            builder.append(statements.size());
            builder.append(". Please remove ");
            builder.append(statements.size() - cardinality);
            builder.append(" statement(s).\n");            
        }
        return true;
    }

    @Override
    boolean hasRequiredValues(RdfReader rdf, Resource resource) throws VoidValidatorException {
        if (requirementLevel == RequirementLevel.MUST){
            List<Statement> statements = rdf.getStatementList(resource, predicate, null);
            return hasRequiredValues(statements);
        } else {
            return true;
        }
    }
     
    boolean hasRequiredValues(List<Statement> statements) throws VoidValidatorException {
        if (statements.isEmpty()){
            return false;
        }
        if (cardinality == NO_CARDINALITY || cardinality == statements.size()){
            return true;
        } else {
            return false;
        }
    }
}
