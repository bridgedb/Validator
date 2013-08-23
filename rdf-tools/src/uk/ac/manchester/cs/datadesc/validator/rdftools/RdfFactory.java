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
package uk.ac.manchester.cs.datadesc.validator.rdftools;

import java.io.File;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.openrdf.repository.Repository;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;
import org.openrdf.sail.nativerdf.NativeStore;
import uk.ac.manchester.cs.datadesc.validator.utils.PropertiesLoader;

/**
 *
 * @author Christian
 */
public class RdfFactory {
   
    public static RdfReader testFileReader = null;
    public static RdfReader validatorFileReader = null;
    public static final String VALIDATOR_RDF_DIRECTORY = "validatorRdfStore";
    public static final String DEFAULT_VALIDATOR_DIRECTORY = "../../rdf/validator";
    public static RdfReader imsFileReader = null;
    public static final String IMS_RDF_DIRECTORY = "imsRdfStore";
    public static final String DEFAULT_IMS_DIRECTORY = "../../rdf/ims";
    public static final String TEST_RDF_DIRECTORY = "testRdfStore";
    public static final String DEFAULT_TEST_DIRECTORY = "../../rdf/test";
    private static final boolean FILE_BASED = true;
    private static final boolean MEMORY_BASED = false;;
    
    
    static final Logger logger = Logger.getLogger(RdfFactory.class);
    
    public static RdfReader getMemory() throws VoidValidatorException{
        Repository repository = new SailRepository(new MemoryStore());
        RdfReader rdfReader = new RdfReader(repository, MEMORY_BASED);
        return rdfReader;
    } 
    
    public static RdfReader getValidatorFilebase() throws VoidValidatorException{
        if (validatorFileReader == null) {
            PropertiesLoader.configureLogger();
            Properties properties = PropertiesLoader.getProperties();
            String directoryName = properties.getProperty(VALIDATOR_RDF_DIRECTORY);
            if (directoryName == null){
                directoryName = DEFAULT_VALIDATOR_DIRECTORY;
            }
            validatorFileReader = getReader(directoryName);
            Reporter.println("Using Validator RDF store " + directoryName);
        }
        return validatorFileReader;        
    }

   public static RdfReader getImsFilebase() throws VoidValidatorException{
         if (imsFileReader == null) {
            PropertiesLoader.configureLogger();
            Properties properties = PropertiesLoader.getProperties();
            String directoryName = properties.getProperty(IMS_RDF_DIRECTORY);
            if (directoryName == null){
                directoryName = DEFAULT_IMS_DIRECTORY;
            }
            Reporter.println("Using IMS RDF store " + directoryName);
            imsFileReader = getReader(directoryName);
        }
        return imsFileReader;        
    }

    public static RdfReader getTestFilebase() throws VoidValidatorException{
        if (testFileReader == null) {
            Properties properties = PropertiesLoader.getProperties();
            String directoryName = properties.getProperty(TEST_RDF_DIRECTORY);
            if (directoryName == null){
                directoryName = DEFAULT_VALIDATOR_DIRECTORY;
            }
            testFileReader = getReader(directoryName);
        }
        return testFileReader;        
    }

    private static RdfReader getReader(String directoryName) throws VoidValidatorException{
        File directory = getDirectory(directoryName);
        File lockDirectory = new File(directory, "lock");
        if (lockDirectory.exists()){
           delete(lockDirectory);
        }
        NativeStore store = new NativeStore(directory);
        Repository repository = new SailRepository(store);
        RdfReader reader = new RdfReader(repository, FILE_BASED);
        logger.info("RDF store setup at: " + directory);
        return reader;        
    }

    private static File getDirectory(String directoryName) throws VoidValidatorException{
        File directory = new File(directoryName);
        checkDirectory(directory);
        return directory;
    }

    private static void checkDirectory(File directory) throws VoidValidatorException{
        if (directory.isDirectory()){
            return;
        }
        if (directory.isFile()){
            throw new VoidValidatorException("RDF Directory location " + directory.getAbsolutePath() + " holds a file.");
        }
        directory.mkdirs();
        if (directory.isDirectory()){
            return;
        }
        throw new VoidValidatorException("Unable to create RDF Directory " + directory.getAbsolutePath());
    }

    private static void delete(File file) throws VoidValidatorException {
        if (file.isDirectory()){
            File[] children = file.listFiles();
            if (children != null){
                for (File child:children){
                    delete(child);
                }
            }
        }
        file.delete();
        if (file.exists()){
            throw new VoidValidatorException("Unable to delete " + file.getAbsolutePath());        
        }
    }

 }
