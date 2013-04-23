/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.manchester.cs.rdftools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

/**
 * This is mainly for testing.
 * @author Christian
 */
public class RdfHolder implements RdfInterface, Cloneable{

    private final List<Statement> statements;
    
    public RdfHolder(){
        statements = new ArrayList<Statement>();
    }
    
    public RdfHolder(List<Statement> incomingStatements){
        this.statements = incomingStatements;
    }

    public RdfHolder(RdfInterface other) throws VoidValidatorException{
        statements = new ArrayList<Statement>(other.getStatementList(null, null, null));
    }

    @Override
    public RdfHolder clone(){
        RdfHolder other = new RdfHolder();
        other.statements.addAll(this.statements);
        return other;
    }
    
    public void addStatement(Statement statement){
        statements.add(statement);
    }
    
    public void addStatements(Collection<Statement> newStatements) {
        statements.addAll(newStatements);
    }
    
    public void removeStatement(Statement statement){
        statements.remove(statement);
    }

     public void removeStatements(Collection<Statement> removeStatements){
        statements.removeAll(removeStatements);
    }

    @Override
    public List<Statement> getStatementList(Resource subjectResource, URI predicate, Value object, Resource... contexts) throws VoidValidatorException {
        List<Statement> results = new ArrayList<Statement>();
        for (Statement statement:statements){
            if (requested(statement, subjectResource, predicate, object, contexts)){ 
                results.add(statement);
            }
        }
        return results;
    }
    
    private boolean requested(Statement statement, Resource subjectResource, URI predicate, Value object, 
            Resource... contexts){
        if (subjectResource != null && !statement.getSubject().equals(subjectResource)){
            return false;
        }
        if (predicate != null && !statement.getPredicate().equals(predicate)){
            return false;
        }
        if (object != null && !statement.getObject().equals(object)){
            return false;
        }
        if (contexts.length == 0){
            return true;
        }
        for (int i = 0; i < contexts.length; i++){
            if (contexts[i].equals(statement.getContext())){
                return true;
            }
        }
        return false;
    }

 }
