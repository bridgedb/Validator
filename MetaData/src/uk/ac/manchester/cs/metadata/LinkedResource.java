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
import uk.ac.manchester.cs.rdftools.RdfReader;
import uk.ac.manchester.cs.rdftools.VoidValidatorException;

/**
 *
 * @author Christian
 */
class LinkedResource extends CardinalityMetaData {
    
    private final Set<URI> linkedTypes;
    private final MetaDataSpecification metaDataSpecification;
  
    public LinkedResource(URI predicate, String type, int cardinality, RequirementLevel requirementLevel, 
            Set<URI> linkedTypes, MetaDataSpecification metaDataSpecification) {
       super(predicate, cardinality, requirementLevel);
       this.linkedTypes = linkedTypes;
       this.metaDataSpecification = metaDataSpecification;
    }

    @Override
     protected boolean appendIncorrectReport(StringBuilder builder, RdfReader rdf, List<Statement> statements, int tabLevel) throws VoidValidatorException {
        boolean appended = false;
        for (Statement statement:statements){
            if (statement.getObject() instanceof Resource){
                Resource resource = (Resource)statement.getObject();
                List<Statement> typeStatements = rdf.getStatementList(resource, RdfConstants.TYPE_URI, null);
                for (Statement typeStatement: typeStatements){
                    URI linkedType = (URI)typeStatement.getSubject();
                    boolean unknownType = true;
                    if (linkedTypes.contains(linkedType)){
                        if (!this.isValid(rdf, resource, linkedType)){
                            appendInvalidLinked(builder, statement, tabLevel);
                            appended = true;
                            unknownType = false;
                        }
                    }
                    if (unknownType){
                        for (URI possibleType: linkedTypes){
                            if (unknownType && isValid(rdf, resource, possibleType)){
                                unknownType = false;
                            }
                        }
                        if (unknownType){
                            appendNoKnownType(builder, statement, tabLevel);
                        }
                    }
                }
            } else {
                appendNotAResource(builder, statement, tabLevel);
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
    boolean isValid(RdfReader rdf, Resource resource) throws VoidValidatorException {
        List<Statement> statements = rdf.getStatementList(resource, RdfConstants.TYPE_URI, null);
        for (Statement statement: statements){
            URI linkedType = (URI)statement.getSubject();
            if (linkedTypes.contains(statement.getSubject())){
                return isValid(rdf, resource, linkedType);
            }
        }
        for (URI linkedType: linkedTypes){
            return isValid(rdf, resource, linkedType);
        }
        return false;
    }

    private boolean isValid(RdfReader rdf, Resource resource, Resource linkedType) throws VoidValidatorException {
        ResourceMetaData resourceMetaData = metaDataSpecification.getResourceMetaData(linkedType);     
        if (resourceMetaData == null){
            throw new VoidValidatorException ("Unable to ResourceMetaData for " + linkedType);
        }
        return resourceMetaData.isValid(rdf, resource);
    }

   private void appendErrorStart(StringBuilder builder, Statement statement, int tabLevel) {
        tab(builder, tabLevel);
        builder.append("ERROR: Found: ");
        this.addStatement(builder, statement);
        builder.append("\n");            
        tab(builder, tabLevel+1);
    }
   
    private void appendNotAResource(StringBuilder builder, Statement statement, int tabLevel) {
        appendErrorStart(builder, statement, tabLevel);
        builder.append("Object must be a URI of type: ");            
        builder.append(getType());
        builder.append("\n");
    }

    private void appendInvalidLinked(StringBuilder builder, Statement statement, int tabLevel) {
        appendErrorStart(builder, statement, tabLevel);
        builder.append(statement.getObject());
        builder.append(" has errors! See report for that Resource! ");            
        builder.append("\n");
    }

    private void appendNoKnownType(StringBuilder builder, Statement statement, int tabLevel) {
        appendErrorStart(builder, statement, tabLevel);
        builder.append(statement.getObject());
        builder.append(" has not been typed, and does not meet the requirements of any known type. ");            
        builder.append("\n");    
    }

 }
