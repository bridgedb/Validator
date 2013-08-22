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

/**
 * Util functions that allows messages to be output.
 * <p>
 * Allows the output format to be changed in one place so changing everywhere.
 * <p>
 * All other System.out calls can then be considered debug commands that should not have stayed in.
 * 
 * @author Christian
 */
public class Reporter {
    
    //static final Logger logger = Logger.getLogger(Reporter.class);
    /**
     * Messages that should always goto the System out stream.
     * This method just for the ease of searching for System,out lines added for debugging.
     * @param message 
     */
    
    public static void println(String message){
        //logger.info(message);
        System.out.println(message);
    }

    public static void error(String message){
        //logger.error(message);
        System.err.println(message);
    }

    static void error(Exception ex) {
        ex.printStackTrace();
    }
}
