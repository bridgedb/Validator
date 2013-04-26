package uk.ac.manchester.cs.validator;

import java.io.File;
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
    
    private static void validateURIOps(String address) throws VoidValidatorException{
        MetaDataSpecification specifications = new MetaDataSpecification("resources/VoidInfo.owl");
        validateURI(address, specifications);
    }
    
    private static void validateURISimple(String address) throws VoidValidatorException{
        MetaDataSpecification specifications = new MetaDataSpecification("test-data/simpleOntology.owl");
        validateURI(address, specifications);
    }

    private static void validateFileSimple(String fileName) throws VoidValidatorException{
        MetaDataSpecification specifications = new MetaDataSpecification("test-data/simpleOntology.owl");
        validateFile(fileName, specifications);
    }

    private static void validateURI(String address, MetaDataSpecification specifications) throws VoidValidatorException{
        Reporter.println("Validate: " + address);
        RdfReader reader = RdfFactory.getMemory();
        Resource expectedContext = new URIImpl(address);
        Resource context = reader.loadURI(address);
        if (!context.equals(expectedContext)){
            throw new VoidValidatorException("Context mismatch reading " + address + " is: " + context + " but expected " + expectedContext);
        }
        List<Statement> statements = reader.getStatementList(null, null, null, context);
        if (statements.isEmpty()){
            throw new VoidValidatorException("No statements read from: " + address);
        }
        String results = Validator.validate(reader, context, specifications);
        System.out.println(results);
        if (!results.endsWith(Validator.SUCCESS)){
            throw new VoidValidatorException("Validation failed");
        }
        //statements = reader.getStatementList(null, null, null);
        //for (Statement statement:statements){
            //ystem.out.println(statement);
        //}
   }
    
    private static void validateFile(String fileName, MetaDataSpecification specifications) throws VoidValidatorException{
        Reporter.println("Validate: " + fileName);
        RdfReader reader = RdfFactory.getMemory();
        File file = new File(fileName);
        Resource context = reader.loadFile(file);
        List<Statement> statements = reader.getStatementList(null, null, null, context);
        if (statements.isEmpty()){
            throw new VoidValidatorException("No statements read from: " + fileName);
        }
        String results = Validator.validate(reader, context, specifications);
        System.out.println(results);
        //statements = reader.getStatementList(null, null, null);
        //for (Statement statement:statements){
            //ystem.out.println(statement);
        //}
        //if (!results.endsWith(Validator.SUCCESS)){
        //    throw new VoidValidatorException("Validation failed");
       // }
    }

    public static void main(String[] args) throws VoidValidatorException{
       validateURISimple("https://github.com/openphacts/Validator/blob/master/MetaData/test-data/remoteTest.ttl");
       validateFileSimple("test-data/remoteTest2.ttl");
    //   validateURIOps("https://github.com/openphacts/ops-platform-setup/blob/master/void/chebi/chebi93_void.ttl");
       //validateURI("ftp://ftp.ebi.ac.uk/pub/databases/chembl/ChEMBL-RDF/15.5/void.ttl.gz");
       //validateURIOps("ftp://ftp.rsc-us.org/OPS/20130314/void_2013-03-14.ttl");
       // validateURIOps("ftp://ftp.rsc-us.org/OPS/20130314/CHEMBL/LINKSET_EXACT_CHEMBL20130314.ttl.gz");
      //  checkLinkset("ftp://ftp.rsc-us.org/OPS/20130314/DRUGBANK/LINKSET_EXACT_DRUGBANK20130314.ttl.gz");
      //  checkLinkset("ftp://ftp.rsc-us.org/OPS/20130314/MESH/LINKSET_EXACT_MESH20130314.ttl.gz");
      //  checkLinkset("ftp://ftp.rsc-us.org/OPS/20130314/PDB/LINKSET_EXACT_PDB20130314.ttl.gz");
      //  checkLinkset("ftp://ftp.rsc-us.org/OPS/20130314/PDB/LINKSET_RELATED_PDB20130314.ttl.gz");
   }

}
