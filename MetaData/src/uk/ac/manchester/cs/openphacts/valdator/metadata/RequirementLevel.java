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
package uk.ac.manchester.cs.openphacts.valdator.metadata;

import uk.ac.manchester.cs.openphacts.valdator.rdftools.VoidValidatorException;

/**
 *
 * @author Christian
 */
public enum RequirementLevel {
    MUST, SHOULD, MAY;
    
    public static RequirementLevel parseString(String string) throws VoidValidatorException{
        string = string.trim();
        string = string.replaceAll("\"", "");
        for(RequirementLevel type:RequirementLevel.values()){
            if (type.toString().equalsIgnoreCase(string)){
                return type;
            }
        }
        return null;
     }
    
    public static String valuesString(){
        String result = RequirementLevel.values()[0].toString();
        for (int i = 1; i< RequirementLevel.values().length; i++){
            result = result + ", " + RequirementLevel.values()[i].toString();
        }
        return result;
    }
    

}
