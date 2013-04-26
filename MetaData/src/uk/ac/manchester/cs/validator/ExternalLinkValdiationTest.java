package uk.ac.manchester.cs.validator;

import java.util.List;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.URIImpl;
import uk.ac.manchester.cs.metadata.MetaDataSpecification;
import uk.ac.manchester.cs.rdftools.RdfFactory;
import uk.ac.manchester.cs.rdftools.RdfReader;
import uk.ac.manchester.cs.rdftools.Reporter;
import uk.ac.manchester.cs.rdftools.VoidValidatorException;

/**
 * This class is a test class but which has not been include in the standard tests due to the dependency on external files.
 * @author Christian
 */
public class ExternalLinkValdiationTest {
    
    private static void validateURI(String address) throws VoidValidatorException{
        Reporter.println("Validate: " + address);
        RdfReader instance = RdfFactory.getMemory();
        Resource expectedContext = new URIImpl(address);
        Resource context = instance.loadURI(address);
        if (!context.equals(expectedContext)){
            throw new VoidValidatorException("Context mismatch reading " + address + " is: " + context + " but expected " + expectedContext);
        }
        List<Statement> statements = instance.getStatementList(null, null, null, context);
        if (statements.isEmpty()){
            throw new VoidValidatorException("No statements read from: " + address);
        }
        MetaDataSpecification specifications = new MetaDataSpecification("resources/VoidInfo.owl");
        String result = Validator.validate(instance, context, specifications);
        if (!result.endsWith(Validator.SUCCESS)){
            Reporter.println(result);
            throw new VoidValidatorException("Validation failed");
        }
 
    }
    
    public static void main(String[] args) throws VoidValidatorException{
       validateURI("https://github.com/openphacts/ops-platform-setup/blob/master/void/chebi/chebi93_void.ttl");
       //validateURI("ftp://ftp.ebi.ac.uk/pub/databases/chembl/ChEMBL-RDF/15.5/void.ttl.gz");
       validateURI("ftp://ftp.rsc-us.org/OPS/20130314/void_2013-03-14.ttl");
        validateURI("ftp://ftp.rsc-us.org/OPS/20130314/CHEMBL/LINKSET_EXACT_CHEMBL20130314.ttl.gz");
      //  checkLinkset("ftp://ftp.rsc-us.org/OPS/20130314/DRUGBANK/LINKSET_EXACT_DRUGBANK20130314.ttl.gz");
      //  checkLinkset("ftp://ftp.rsc-us.org/OPS/20130314/MESH/LINKSET_EXACT_MESH20130314.ttl.gz");
      //  checkLinkset("ftp://ftp.rsc-us.org/OPS/20130314/PDB/LINKSET_EXACT_PDB20130314.ttl.gz");
      //  checkLinkset("ftp://ftp.rsc-us.org/OPS/20130314/PDB/LINKSET_RELATED_PDB20130314.ttl.gz");
   }

}
