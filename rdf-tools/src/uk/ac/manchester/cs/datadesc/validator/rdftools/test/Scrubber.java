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
package uk.ac.manchester.cs.datadesc.validator.rdftools.test;

import info.aduna.lang.FileFormat;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParserRegistry;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.turtle.TurtleWriter;
import org.openrdf.sail.memory.MemoryStore;
import uk.ac.manchester.cs.datadesc.validator.rdftools.Reporter;
import uk.ac.manchester.cs.datadesc.validator.rdftools.VoidValidatorException;

/**
 *
 * @author Christian
 */
public class Scrubber {
    
   public static String DEFAULT_BASE_URI = "http://no/BaseURI/Set/";
   
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

    private static void writeRDF(RepositoryConnection repositoryConnection, File file) 
            throws IOException, RDFHandlerException, RepositoryException{
        Writer writer = new FileWriter (file);
        TurtleWriter turtleWriter = new TurtleWriter(writer);
        writeRDF(repositoryConnection, turtleWriter);
        writer.close();
    }
    
    private static void writeRDF(RepositoryConnection repositoryConnection, RDFWriter rdfWriter) 
            throws IOException, RDFHandlerException, RepositoryException{ 
        rdfWriter.handleNamespace("", DEFAULT_BASE_URI);
        rdfWriter.startRDF();
        RepositoryResult<Statement> statements = 
                repositoryConnection.getStatements(null, null, null, true);
        while (statements.hasNext()) {
            Statement statement = statements.next();
            rdfWriter.handleStatement(statement);
        }
        rdfWriter.endRDF();
    }
    
    public static void scrub(RepositoryConnection repositoryConnection) throws RepositoryException{
        int count = 0;
        Resource[] rsrc = new Resource[0];
        RepositoryResult<Statement> statements = repositoryConnection.getStatements(null, null, null, true, rsrc);
        while (statements.hasNext()){
            Statement statement = statements.next();
            if (statement.getObject().toString().contains("-")){
                if (statement.getPredicate().toString().equals("http://rdfs.org/ns/void#inDataset")){
                    //ystem.out.println("ignoring void:inDataset statement");
                } else {
                    repositoryConnection.remove(statement, rsrc);
                    count++;
                }
            }
        }
        Reporter.println("\tRemoved " + count + " statements with \"-\" in the object");
    }
   
    public static void clean(File inputFile, File outputFile) throws Exception {
        Reporter.println("Parsing " + inputFile.getAbsolutePath());
        Repository repository = null;
        RepositoryConnection repositoryConnection = null;
        try {
            repository = new SailRepository(new MemoryStore());
            repository.initialize();
            repositoryConnection = repository.getConnection();
            repositoryConnection.add(inputFile, DEFAULT_BASE_URI, getFormat(inputFile.getName()));
            scrub(repositoryConnection);
            writeRDF(repositoryConnection, outputFile);
        } catch (Exception ex) {
            throw new VoidValidatorException ("Error parsing RDf file ", ex);
        } finally {
            repositoryConnection.close();
        }
    }

    public static void main(String[] args) throws Exception {
        clean(new File("C:/Dropbox/linksets/version1.3.alpha4/ensembl/bos_taurus_core_71_31_ensembl_EntrezGeneLinkSets.ttl"), 
                new File("C:/Dropbox/linksets/version1.3.alpha4/ensembl/bos_taurus_core_71_31_ensembl_EntrezGeneLinkSets-scrubbed.ttl"));
        clean(new File("C:/Dropbox/linksets/version1.3.alpha4/ensembl/caenorhabditis_elegans_core_71_235_ensembl_EntrezGeneLinkSets.ttl"), 
                new File("C:/Dropbox/linksets/version1.3.alpha4/ensembl/caenorhabditis_elegans_core_71_235_ensembl_EntrezGeneLinkSets-scrubbed.ttl"));
        clean(new File("C:/Dropbox/linksets/version1.3.alpha4/ensembl/canis_familiaris_core_71_31_ensembl_EntrezGeneLinkSets.ttl"), 
                new File("C:/Dropbox/linksets/version1.3.alpha4/ensembl/canis_familiaris_core_71_31_ensembl_EntrezGeneLinkSets-scrubbed.ttl"));
        clean(new File("C:/Dropbox/linksets/version1.3.alpha4/ensembl/danio_rerio_core_71_9_ensembl_EntrezGeneLinkSets.ttl"), 
                new File("C:/Dropbox/linksets/version1.3.alpha4/ensembl/danio_rerio_core_71_9_ensembl_EntrezGeneLinkSets-scrubbed.ttl"));
        clean(new File("C:/Dropbox/linksets/version1.3.alpha4/ensembl/drosophila_melanogaster_core_71_546_ensembl_EntrezGeneLinkSets.ttl"), 
                new File("C:/Dropbox/linksets/version1.3.alpha4/ensembl/drosophila_melanogaster_core_71_546_ensembl_EntrezGeneLinkSets-scrubbed.ttl"));
        clean(new File("C:/Dropbox/linksets/version1.3.alpha4/ensembl/equus_caballus_core_71_2_ensembl_EntrezGeneLinkSets.ttl"), 
                new File("C:/Dropbox/linksets/version1.3.alpha4/ensembl/equus_caballus_core_71_2_ensembl_EntrezGeneLinkSets-scrubbed.ttl"));
        clean(new File("C:/Dropbox/linksets/version1.3.alpha4/ensembl/gallus_gallus_core_71_4_ensembl_EntrezGeneLinkSets.ttl"), 
                new File("C:/Dropbox/linksets/version1.3.alpha4/ensembl/gallus_gallus_core_71_4_ensembl_EntrezGeneLinkSets-scrubbed.ttl"));
        clean(new File("C:/Dropbox/linksets/version1.3.alpha4/ensembl/homo_sapiens_core_71_37_ensembl_EntrezGeneLinkSets.ttl"), 
                new File("C:/Dropbox/linksets/version1.3.alpha4/ensembl/homo_sapiens_core_71_37_ensembl_EntrezGeneLinkSets-scrubbed.ttl"));
        clean(new File("C:/Dropbox/linksets/version1.3.alpha4/ensembl/mus_musculus_core_71_38_ensembl_EntrezGeneLinkSets.ttl"), 
                new File("C:/Dropbox/linksets/version1.3.alpha4/ensembl/mus_musculus_core_71_38_ensembl_EntrezGeneLinkSets-scrubbed.ttl"));
        clean(new File("C:/Dropbox/linksets/version1.3.alpha4/ensembl/pan_troglodytes_core_71_214_ensembl_EntrezGeneLinkSets.ttl"), 
                new File("C:/Dropbox/linksets/version1.3.alpha4/ensembl/pan_troglodytes_core_71_214_ensembl_EntrezGeneLinkSets-scrubbed.ttl"));
        clean(new File("C:/Dropbox/linksets/version1.3.alpha4/ensembl/rattus_norvegicus_core_71_5_ensembl_EntrezGeneLinkSets.ttl"), 
                new File("C:/Dropbox/linksets/version1.3.alpha4/ensembl/rattus_norvegicus_core_71_5_ensembl_EntrezGeneLinkSets-scrubbed.ttl"));
        clean(new File("C:/Dropbox/linksets/version1.3.alpha4/ensembl/saccharomyces_cerevisiae_core_71_4_ensembl_EntrezGeneLinkSets.ttl"), 
                new File("C:/Dropbox/linksets/version1.3.alpha4/ensembl/saccharomyces_cerevisiae_core_71_4_ensembl_EntrezGeneLinkSets-scrubbed.ttl"));
        clean(new File("C:/Dropbox/linksets/version1.3.alpha4/ensembl/rattus_norvegicus_core_71_5_ensembl_RGDLinkSets.ttl"), 
                new File("C:/Dropbox/linksets/version1.3.alpha4/ensembl/rattus_norvegicus_core_71_5_ensembl_RGDLinkSets-scrubbed.ttl"));
    }
}
