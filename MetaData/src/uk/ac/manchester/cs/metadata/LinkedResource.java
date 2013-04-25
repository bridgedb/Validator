/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.manchester.cs.metadata;

import java.util.List;
import java.util.Set;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import uk.ac.manchester.cs.constants.RdfConstants;
import uk.ac.manchester.cs.rdftools.RdfInterface;
import uk.ac.manchester.cs.rdftools.VoidValidatorException;

/**
 *
 * @author Christian
 */
class LinkedResource extends CardinalityMetaData {
    
    private final Set<URI> linkedTypes;
    private final MetaDataSpecification metaDataSpecification;
  
    public static final String ERROR_SEE_REPORT = " has errors! See report for that Resource! ";
    
    public LinkedResource(URI predicate, String type, int cardinality, RequirementLevel requirementLevel, 
            Set<URI> linkedTypes, MetaDataSpecification metaDataSpecification) {
       super(predicate, cardinality, requirementLevel);
       this.linkedTypes = linkedTypes;
       this.metaDataSpecification = metaDataSpecification;
    }

     @Override
     protected boolean appendIncorrectReport(StringBuilder builder, RdfInterface rdf, List<Statement> statements, 
            Resource context, int tabLevel) throws VoidValidatorException {
        boolean appended = false;
        for (Statement statement:statements){
            if (statement.getObject() instanceof Resource){
                Resource resource = (Resource)statement.getObject();
                boolean unknownType = true;
                List<Statement> typeStatements = rdf.getStatementList(resource, RdfConstants.TYPE_URI, null, statement.getContext());
                for (Statement typeStatement: typeStatements){
                    if (typeStatement.getObject() instanceof URI){
                        URI linkedType = (URI)typeStatement.getObject();
                        if (linkedTypes.contains(linkedType)){
                            if (!this.isValid(rdf, resource, context, linkedType)){
                                appendInvalidLinked(builder, statement, context, tabLevel);
                                appended = true;
                            }
                            unknownType = false;
                        }
                    } else {
                        appendIncorretTypeStatement(builder, typeStatement, context, tabLevel);
                    }
                }
                if (unknownType){
                    for (URI possibleType: linkedTypes){
                        if (unknownType && isValid(rdf, resource, context, possibleType)){
                            unknownType = false;
                        }
                    }
                    if (unknownType){
                        appendNoKnownType(builder, statement, context, tabLevel);
                        appended = true;
                    }
                }
             } else {
                appendNotAResource(builder, statement, context, tabLevel);
                appended = true;
             }
        }
        return appended;
    }

    @Override
    protected String getType() {
        return linkedTypes.toString();
    }

    @Override
    boolean isValid(RdfInterface rdf, Resource resource, Resource context) throws VoidValidatorException {
        List<Statement> statements = rdf.getStatementList(resource, RdfConstants.TYPE_URI, null, context);
        if (!correctCardinality(statements)){
            return false;
        }
        for (Statement statement: statements){
            URI linkedType = (URI)statement.getSubject();
            if (linkedTypes.contains(statement.getSubject())){
                return isValid(rdf, resource, context, linkedType);
            }
        }
        for (URI linkedType: linkedTypes){
            return isValid(rdf, resource, context, linkedType);
        }
        return false;
    }

    private boolean isValid(RdfInterface rdf, Resource resource, Resource context, Resource linkedType) throws VoidValidatorException {
        ResourceMetaData resourceMetaData = metaDataSpecification.getResourceMetaData(linkedType);     
        if (resourceMetaData == null){
            throw new VoidValidatorException ("Unable to ResourceMetaData for " + linkedType);
        }
        return resourceMetaData.isValid(rdf, resource, context);
    }

   private void appendErrorStart(StringBuilder builder, Statement statement, Resource context, int tabLevel) {
        tab(builder, tabLevel);
        builder.append("ERROR: Found: ");
        this.addStatement(builder, statement, context);
        builder.append("\n");            
        tab(builder, tabLevel+1);
    }
   
    private void appendNotAResource(StringBuilder builder, Statement statement, Resource context, int tabLevel) {
        appendErrorStart(builder, statement, context, tabLevel);
        builder.append("Object must be a URI of type: ");            
        builder.append(getType());
        builder.append("\n");
    }

    private void appendInvalidLinked(StringBuilder builder, Statement statement, Resource context, int tabLevel) {
        appendErrorStart(builder, statement, context, tabLevel);
        builder.append(statement.getObject());
        builder.append(ERROR_SEE_REPORT);            
        builder.append("\n");
    }

    private void appendNoKnownType(StringBuilder builder, Statement statement, Resource context, int tabLevel) {
        appendErrorStart(builder, statement, context, tabLevel);
        builder.append(statement.getObject());
        builder.append(" has not been typed, and does not meet the requirements of any known type. ");            
        builder.append("\n");    
    }

    private void appendIncorretTypeStatement(StringBuilder builder, Statement typeStatement, Resource context, int tabLevel) {
        appendErrorStart(builder, typeStatement, context, tabLevel);
        builder.append("Object of this tye statement is not a URI!. ");            
        builder.append("\n");    
    }

 }
