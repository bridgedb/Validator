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
package uk.ac.manchester.cs.openphacts.valdator.metadata.type;

import uk.ac.manchester.cs.openphacts.valdator.constants.OwlConstants;
import uk.ac.manchester.cs.openphacts.valdator.constants.RdfsConstants;
import uk.ac.manchester.cs.openphacts.valdator.rdftools.VoidValidatorException;

/**
 *
 * @author Christian
 */
public class MetaDataTypeFactory {
    
    public static MetaDataType factory(String objectClass) throws VoidValidatorException{
        if (OwlConstants.THING.equalsIgnoreCase(objectClass)){
            return new UriType();
        }
        if (RdfsConstants.LITERAL.equalsIgnoreCase(objectClass)){
            return new LiteralType();
        }
        if (objectClass.startsWith("xsd:")){
            objectClass = XsdType.URI_PREFIX + objectClass.substring(4);
        }
        if (objectClass.equalsIgnoreCase(XsdType.STRING.getCorrectType())){
            return new StringType();
        }
        if (objectClass.startsWith(XsdType.URI_PREFIX)){
            return XsdType.getByType(objectClass);
        }
        throw new VoidValidatorException ("Unexpected type " + objectClass);
        
    }
}
