/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.manchester.cs.metadata;

import java.util.List;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import uk.ac.manchester.cs.rdftools.RdfInterface;
import uk.ac.manchester.cs.rdftools.VoidValidatorException;

/**
 *
 * @author Christian
 */
abstract class CardinalityMetaData extends MetaDataBase {

    protected final URI predicate;
    private final int cardinality;
    private final RequirementLevel requirementLevel;
    public static final String NOT_FOUND = " No statements found with predicate: ";
    public static final String REMOVE = ". Please remove ";
    
    public CardinalityMetaData(URI predicate, int cardinality, RequirementLevel requirementLevel) {
       this.predicate = predicate;
       this.cardinality = cardinality;
       this.requirementLevel = requirementLevel;
    }
    
    @Override
    boolean appendValidate(StringBuilder builder, RdfInterface rdf, Resource resource, Resource context, boolean includeWarnings, 
            int tabLevel) throws VoidValidatorException {
        List<Statement> statements = rdf.getStatementList(resource, predicate, null, context);
        boolean result = appendIncorrectReport(builder, rdf, statements, context, tabLevel);
        if (appendCardinalityReport(builder, statements, context, includeWarnings, tabLevel)){
            result = true;
        }
        return result;
    }

    @Override
    boolean appendError(StringBuilder builder, RdfInterface rdf, Resource resource, Resource context, int tabLevel) throws VoidValidatorException {
        List<Statement> statements = rdf.getStatementList(resource, predicate, null, context);
        boolean result = appendIncorrectReport(builder, rdf, statements,  context, tabLevel);
        if (cardinality != NO_CARDINALITY && statements.size() >= cardinality) {
            //Found too many statements so this is always an error.
            if (incorrectNumberOfStatements(builder, statements, context, tabLevel)){
                result = true;
            }
        }
        return result;
    }
    
    @Override
    void appendRequirement(StringBuilder builder, RdfInterface rdf, Resource resource, Resource context, int tabLevel) throws VoidValidatorException {
        List<Statement> statements = rdf.getStatementList(resource, predicate, null, context);
        if (this.hasRequiredValues(statements)){
            tab(builder, tabLevel);
            builder.append("You already correctly have ");
            builder.append(statements.size() );
            builder.append(" statements ");
            builder.append(" with predicate  ");
            builder.append(predicate);
            builder.append("\n");
        } else {
            appendRequirement(builder, context, tabLevel);
        }
    }
        
    protected void appendRequirement(StringBuilder builder, Resource context, int tabLevel) {
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
        this.addValue(builder, predicate, context);
        builder.append(" and type ");
        builder.append(getType());
        builder.append("\n");
    }
    
    protected abstract String getType();
    
    protected abstract boolean appendIncorrectReport(StringBuilder builder, RdfInterface rdf, List<Statement> statements, 
            Resource context, int tabLevel) throws VoidValidatorException;

    protected boolean appendCardinalityReport(StringBuilder builder, List<Statement> statements, Resource context,
            boolean includeWarnings, 
            int tabLevel) throws VoidValidatorException{
        if (statements.isEmpty()){
            switch (requirementLevel){
                case MUST: return appendNoStatements(builder, context, "ERROR:", tabLevel);
                case SHOULD: {
                    if (includeWarnings){
                        return appendNoStatements(builder, context, "Warning:", tabLevel);
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
        return incorrectNumberOfStatements(builder, statements, context, tabLevel);
    }
    
    private boolean appendNoStatements(StringBuilder builder, Resource context, String level, int tabLevel) {
        tab(builder, tabLevel);
        builder.append(level);
        builder.append(NOT_FOUND);
        this.addValue(builder, predicate, context);
        builder.append("\n");
        appendRequirement(builder, context, tabLevel + 1);
        return true;
    }

    private boolean incorrectNumberOfStatements(StringBuilder builder, List<Statement> statements, Resource context, int tabLevel) {
        tab(builder, tabLevel);
        builder.append("ERROR:Looking for " );
        builder.append(cardinality);
        builder.append(" statements with type ");
        this.addValue(builder, context, predicate);
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
            builder.append(REMOVE);
            builder.append(statements.size() - cardinality);
            builder.append(" statement(s).\n");            
        }
        return true;
    }

    @Override
    boolean hasRequiredValues(RdfInterface rdf, Resource resource, Resource context) throws VoidValidatorException {
        if (requirementLevel == RequirementLevel.MUST){
            List<Statement> statements = rdf.getStatementList(resource, predicate, null, context);
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
