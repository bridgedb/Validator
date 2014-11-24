// OpenPHACTS RDF Validator,
// A tool for validating and storing RDF.
//
// Copyright 2012-2013  Christian Y. A. Brenninkmeijer
// Copyright 2012-2013  University of Manchester
// Copyright 2012-2013  OpenPhacts
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
package uk.ac.manchester.cs.datadesc.validator;

import java.io.File;
import java.util.List;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.URIImpl;
import uk.ac.manchester.cs.datadesc.validator.metadata.MetaDataSpecification;
import uk.ac.manchester.cs.datadesc.validator.rdftools.RdfFactory;
import uk.ac.manchester.cs.datadesc.validator.rdftools.RdfReader;
import uk.ac.manchester.cs.datadesc.validator.rdftools.Reporter;
import uk.ac.manchester.cs.datadesc.validator.rdftools.VoidValidatorException;

/**
 * This class is a test class but which has not been include in the standard tests due to the dependency on external files.
 * @author Christian
 */
public class ExternalLinkValdiationTest {
    
    private static void validateURIOps(String address, boolean includeWarning) throws VoidValidatorException{
        MetaDataSpecification specifications = MetaDataSpecification.specificationByName("opsVoid");
        validateURI(address, specifications, includeWarning);
    }
    
    private static void validateURISimple(String address, boolean includeWarning) throws VoidValidatorException{
        MetaDataSpecification specifications = MetaDataSpecification.specificationByName("simpleTest");
        validateURI(address, specifications, includeWarning);
    }

    private static void validateFileSimple(String fileName, boolean includeWarning) throws VoidValidatorException{
        MetaDataSpecification specifications = MetaDataSpecification.specificationByName("simpleTest");
        validateFile(fileName, specifications, includeWarning);
    }

    private static void validateURI(String address, MetaDataSpecification specifications, boolean includeWarning) throws VoidValidatorException{
        Reporter.println("Validate: " + address);
        RdfReader reader = RdfFactory.getMemory();
        Resource expectedContext = new URIImpl(address);
        Resource context = reader.loadURI(address);
        if (!context.equals(expectedContext)){
            throw new VoidValidatorException("Context mismatch reading " + address + " is: " + context + " but expected " + expectedContext);
        }
        List<Statement> statements = reader.getDirectOnlyStatementList(null, null, null, context);
        if (statements.isEmpty()){
            throw new VoidValidatorException("No statements read from: " + address);
        }
        String results = RdfValidator.validate(reader, context, specifications, includeWarning);
        Reporter.println(results);
        if (!results.endsWith(RdfValidator.SUCCESS)){
            throw new VoidValidatorException("Validation failed");
        }
        //statements = reader.getStatementList(null, null, null);
        //for (Statement statement:statements){
            //ystem.out.println(statement);
        //}
        reader.close();
   }
    
    private static void validateFile(String fileName, MetaDataSpecification specifications, boolean includeWarning) throws VoidValidatorException{
        Reporter.println("Validate: " + fileName);
        RdfReader reader = RdfFactory.getMemory();
        File file = new File(fileName);
        Resource context = reader.loadFile(file);
        List<Statement> statements = reader.getDirectOnlyStatementList(null, null, null, context);
        if (statements.isEmpty()){
            throw new VoidValidatorException("No statements read from: " + fileName);
        }
        String results = RdfValidator.validate(reader, context, specifications, includeWarning);
        //ystem.out.println(results);
        //statements = reader.getStatementList(null, null, null);
        //for (Statement statement:statements){
            //ystem.out.println(statement);
        //}
        //if (!results.endsWith(Validator.SUCCESS)){
        //    throw new VoidValidatorException("Validation failed");
       // }
        reader.close();
    }

    public static void main(String[] args) throws VoidValidatorException{
       MetaDataSpecification.LoadSpecification(ValidatorExampleConstants.SIMPLE_FILE, 
               ValidatorExampleConstants.SIMPLE_NAME, ValidatorExampleConstants.SIMPLE_DESCRIPTION);
       validateURIOps("http://www.bigcat.unimaas.nl/~egonw/hmdb/3.0.5/hmdb_ls_cas.ttl", true);
       validateURIOps("http://www.bigcat.unimaas.nl/~egonw/hmdb/3.0.5/hmdb_ls_chemspider.ttl", true);
       validateURIOps("http://www.bigcat.unimaas.nl/~egonw/hmdb/3.0.5/hmdb_ls_kegg.ttl", true);
       validateURIOps("http://www.bigcat.unimaas.nl/~egonw/hmdb/3.0.5/hmdb_ls_pubchem.ttl", true);
       validateURIOps("http://www.bigcat.unimaas.nl/~egonw/hmdb/3.0.5/hmdb_ls_wp.void.ttl", true);
       //validateURISimple("https://github.com/openphacts/Validator/blob/master/metadata/test-data/remoteTest.ttl", true);
       //validateFileSimple("test-data/remoteTest2.ttl", true);
 /*      validateURIOps("http://openphacts.cs.man.ac.uk/ims/linkset/version1.3.alpha2/uniprot_ensembl.ttl.gz", true);
       validateURIOps("http://openphacts.cs.man.ac.uk/ims/linkset/version1.3.alpha2/uniprot_flybase.ttl.gz", true);
       validateURIOps("http://openphacts.cs.man.ac.uk/ims/linkset/version1.3.alpha2/uniprot_geneid.ttl.gz", true);
       validateURIOps("http://openphacts.cs.man.ac.uk/ims/linkset/version1.3.alpha2/uniprot_ipi.ttl.gz", true);
       validateURIOps("http://openphacts.cs.man.ac.uk/ims/linkset/version1.3.alpha2/uniprot_mgi.ttl.gz", true);
       validateURIOps("http://openphacts.cs.man.ac.uk/ims/linkset/version1.3.alpha2/uniprot_omim.ttl.gz", true);
       validateURIOps("http://openphacts.cs.man.ac.uk/ims/linkset/version1.3.alpha2/uniprot_pdb.ttl.gz", true);
       validateURIOps("http://openphacts.cs.man.ac.uk/ims/linkset/version1.3.alpha2/uniprot_refseq.ttl.gz", true);
       validateURIOps("http://openphacts.cs.man.ac.uk/ims/linkset/version1.3.alpha2/uniprot_rgd.ttl.gz", true);
       validateURIOps("http://openphacts.cs.man.ac.uk/ims/linkset/version1.3.alpha2/uniprot_sgd.ttl.gz", true);
       validateURIOps("http://openphacts.cs.man.ac.uk/ims/linkset/version1.3.alpha2/uniprot_unigene.ttl.gz", true);
       validateURIOps("http://openphacts.cs.man.ac.uk/ims/linkset/version1.3.alpha2/uniprot_wormbase.ttl.gz", true);
       validateURIOps("http://openphacts.cs.man.ac.uk/ims/linkset/version1.3.alpha2/uniprot_zfin.ttl.gz", true);
       validateURIOps("", true);
*/    
    
    }

}
