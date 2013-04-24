package uk.ac.manchester.cs.rdftools.test;

import java.util.List;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.URIImpl;
import uk.ac.manchester.cs.rdftools.RdfFactory;
import uk.ac.manchester.cs.rdftools.RdfReader;
import uk.ac.manchester.cs.rdftools.Reporter;
import uk.ac.manchester.cs.rdftools.VoidValidatorException;

/**
 * This class is a test class but which has not been include in the standard tests due to the dependency on external files.
 * @author Christian
 */
public class ExternalLinkReadTest {
    
    private static void testReadUri(String address) throws VoidValidatorException{
        Reporter.println("Reading: " + address);
        RdfReader instance = RdfFactory.getMemory();
        Resource expectedContext = new URIImpl(address);
        Resource context = instance.loadURI(address);
        if (!context.equals(expectedContext)){
            throw new VoidValidatorException("Context mismatch reading " + address + " is: " + context + " but expected " + expectedContext);
        }
        List<Statement> statements = instance.getDirectStatementList(null, null, null, context);
        if (statements.isEmpty()){
            throw new VoidValidatorException("No statements read from: " + address);
        }
    }
    
    public static void main(String[] args) throws VoidValidatorException{
       testReadUri("https://github.com/openphacts/ops-platform-setup/blob/master/void/chebi/chebi93_void.ttl");
       testReadUri("ftp://ftp.ebi.ac.uk/pub/databases/chembl/ChEMBL-RDF/15.5/void.ttl.gz");
       testReadUri("ftp://ftp.rsc-us.org/OPS/20130314/void_2013-03-14.ttl");
   }

}
