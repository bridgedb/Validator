package uk.ac.manchester.cs.rdftools;

import info.aduna.lang.FileFormat;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParserRegistry;
import uk.ac.manchester.cs.utils.UrlReader;

/**
 *
 * @author Christian
 */
public class RdfReader implements RdfInterface{
    
    private static final boolean EXCLUDE_INFERRED =false;
    private final Repository repository;
    private RepositoryConnection connection = null;
    
    public static RdfReader factory(Repository repository) throws VoidValidatorException{
        RdfReader instance = new RdfReader(repository);
        try {
            repository.initialize();
            return instance;
        } catch (Exception ex) {
            throw new VoidValidatorException ("Error parsing RDf file ", ex);
        }       
    } 
   
    private RdfReader(Repository repository) throws VoidValidatorException{
        this.repository = repository; 
    }
   
    public Resource loadFile(File inputFile) throws VoidValidatorException{
        try {
            InputStream stream = new FileInputStream(inputFile);
            return loadInputStream(stream, inputFile.toURI().toString());
        } catch (FileNotFoundException ex) {
            throw new VoidValidatorException("Unable to find file. " + inputFile.getAbsolutePath(), ex);
        }
    }
   
    public Resource loadURI(String address) throws VoidValidatorException {
        if (address.startsWith("file")){
            File file = new File(address);
            return loadFile(file);
        }
        UrlReader urlReader = new UrlReader(address);
        InputStream stream = urlReader.getInputStream();
        return loadInputStream(stream, address);
    }
   
    private Resource loadInputStream(InputStream stream, String address) throws VoidValidatorException{
        try {
            Resource context = new URIImpl(address);
            RepositoryConnection repositoryConnection = getConnection();
            connection.setAutoCommit(false);
            repositoryConnection.add(stream, address, getFormat(address), context);
            connection.commit();
            return context;
        } catch (Exception ex) {
            closeOnError();
            throw new VoidValidatorException ("Error parsing RDf file ", ex);
        }
    }

    @Override
    public List<Statement> getStatementList(Resource subjectResource, URI predicate, Value object, Resource... contexts) 
            throws VoidValidatorException {
        try {
            RepositoryConnection repositoryConnection = getConnection();
            RepositoryResult<Statement> repositoryResult = 
                    repositoryConnection.getStatements(subjectResource, predicate, object, EXCLUDE_INFERRED, contexts);
            return repositoryResult.asList();
        } catch (Exception ex) {
            close();
            throw new VoidValidatorException ("Error getting Type Statements ", ex);
        }        
    }

    private RepositoryConnection getConnection() throws VoidValidatorException{
        try {
            if (connection != null){
                if (connection.isOpen()){
                    return connection;
                }
            
            }
            connection = repository.getConnection();
            return connection;
        } catch (Exception ex) {
            throw new VoidValidatorException ("Error getting connection ", ex);
        }        
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

    public final void close() throws VoidValidatorException {
        try {
            if (connection != null){
                if (connection.isOpen()){
                    connection.close();
                    connection = null;
                }
            
            }
        } catch (Exception ex) {
            throw new VoidValidatorException ("Error shutting down connection", ex);
        }        
    }

    private void closeOnError() {
        try {
            close();
        } catch (VoidValidatorException ex) {
            //do nothing as there is already an error
        }
    }


}
