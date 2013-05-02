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
package uk.ac.manchester.cs.openphacts.valdator.rdftools;

import java.io.File;
import java.util.Properties;
import org.openrdf.repository.Repository;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;
import org.openrdf.sail.nativerdf.NativeStore;
import uk.ac.manchester.cs.openphacts.valdator.utils.ConfigReader;

/**
 *
 * @author Christian
 */
public class RdfFactory {
   
    public static RdfReader fileReader = null;
    public static final String RDF_DIRECTORY = "SailNativeStore";
    public static final String DEFAULT_DIRECTORY = "../rdf/";
    
    public static RdfReader getMemory() throws VoidValidatorException{
        Repository repository = new SailRepository(new MemoryStore());
        RdfReader rdfReader = RdfReader.factory(repository);
        return rdfReader;
    } 
    
    public static RdfReader getFilebase() throws VoidValidatorException{
        if (fileReader == null) {
            Properties properties = ConfigReader.getProperties();
            String directoryName = properties.getProperty(RDF_DIRECTORY);
            if (directoryName == null){
                directoryName = DEFAULT_DIRECTORY;
            }
            File directory = getDirectory(directoryName);
            Repository repository = new SailRepository(new NativeStore(directory));
            fileReader = RdfReader.factory(repository);
        }
        return fileReader;        
    }

    public static RdfReader getTestFilebase() throws VoidValidatorException{
        if (fileReader == null) {
            Properties properties = ConfigReader.getProperties();
            String directoryName = properties.getProperty(RDF_DIRECTORY);
            if (directoryName == null){
                directoryName = DEFAULT_DIRECTORY;
            }
            File directory = getDirectory(directoryName);
            File testDirectory = new File(directory, "test");
            //delete(testDirectory);
            testDirectory.deleteOnExit();
            Repository repository = new SailRepository(new NativeStore(testDirectory));
            fileReader = RdfReader.factory(repository);
        }
        return fileReader;        
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
