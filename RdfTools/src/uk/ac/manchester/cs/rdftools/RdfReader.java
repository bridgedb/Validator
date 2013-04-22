/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.manchester.cs.rdftools;

import info.aduna.lang.FileFormat;
import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParserRegistry;
import org.openrdf.sail.memory.MemoryStore;

/**
 *
 * @author Christian
 */
public class RdfReader {
    
   public static String DEFAULT_BASE_URI = "http://no/BaseURI/Set/";
   private static final boolean EXCLUDE_INFERRED =false;
   private final Repository repository;

   public RdfReader(File inputFile) throws VoidValidatorException{
        RepositoryConnection repositoryConnection = null;
        try {
            repository = new SailRepository(new MemoryStore());
            repository.initialize();
            repositoryConnection = repository.getConnection();
            repositoryConnection.add(inputFile, DEFAULT_BASE_URI, getFormat(inputFile.getName()));
        } catch (Exception ex) {
            throw new VoidValidatorException ("Error parsing RDf file ", ex);
        } finally {
           try {
               repositoryConnection.close();
           } catch (RepositoryException ex) {
               throw new VoidValidatorException ("Error closing repository connection", ex);
           }
        }
    }
   
    public List<Statement> getStatementList(Resource subjectResource, URI predicate, Value object, Resource... contexts) 
            throws VoidValidatorException {
        RepositoryConnection repositoryConnection = getConnection();
        try {
            RepositoryResult<Statement> repositoryResult = 
                    repositoryConnection.getStatements(subjectResource, predicate, object, EXCLUDE_INFERRED, contexts);
            return repositoryResult.asList();
        } catch (Exception ex) {
            throw new VoidValidatorException ("Error getting Type Statements ", ex);
        } finally {
           try {
               repositoryConnection.close();
           } catch (RepositoryException ex) {
               throw new VoidValidatorException ("Error closing repository connection", ex);
           }
        }        
    }


    /*public List<Statement> getTypeStatements() throws VoidValidatorException{
        RepositoryConnection repositoryConnection = getConnection();
        try {
            RepositoryResult<Statement> repositoryResult = repositoryConnection.getStatements(null, RdfConstants.TYPE_URI, null, true);
            return repositoryResult.asList();
        } catch (Exception ex) {
            throw new VoidValidatorException ("Error getting Type Statements ", ex);
        } finally {
           try {
               repositoryConnection.close();
           } catch (RepositoryException ex) {
               throw new VoidValidatorException ("Error closing repository connection", ex);
           }
        }        
    }*/
   
    public RepositoryConnection getConnection() throws VoidValidatorException{
        try {
            return repository.getConnection();
        } catch (Exception ex) {
            throw new VoidValidatorException ("Error getting connection ", ex);
        }        
    }

    public Set<Resource> getResources() throws VoidValidatorException{
        Set<Resource> results = new HashSet<Resource>();
        RepositoryConnection repositoryConnection = null;
        try {
            repositoryConnection = repository.getConnection();
            String queryString = "SELECT x FROM {x} p {y}";
            TupleQuery tupleQuery = repositoryConnection.prepareTupleQuery(QueryLanguage.SERQL, queryString);
            TupleQueryResult queryResult = tupleQuery.evaluate();
            while (queryResult.hasNext()){
                BindingSet bindingSet = queryResult.next();
                Value value = bindingSet.getValue("x");
                System.out.println(value);
                results.add((Resource)value);
            }
        } catch (Exception ex) {
            throw new VoidValidatorException ("Error getting resources ", ex);
        } finally {
           try {
               repositoryConnection.close();
           } catch (RepositoryException ex) {
               throw new VoidValidatorException ("Error closing repository connection", ex);
           }
        }
        return results;
   }

   private static RDFFormat getFormat(String fileName) throws VoidValidatorException{
        if (fileName.endsWith(".n3")){
            fileName = "try.ttl";
        }
        RDFParserRegistry reg = RDFParserRegistry.getInstance();
        FileFormat fileFormat = reg.getFileFormatForFileName(fileName);
        if (fileFormat == null || !(fileFormat instanceof RDFFormat)){
            //added bridgeDB/OPS specific extension here if required.  
            throw new VoidValidatorException("failed");
        } else {
            return (RDFFormat)fileFormat;
        }
    }

}
