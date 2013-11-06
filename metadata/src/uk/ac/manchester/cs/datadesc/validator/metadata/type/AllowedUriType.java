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
package uk.ac.manchester.cs.datadesc.validator.metadata.type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

/**
 *
 * @author Christian
 */
public class AllowedUriType implements MetaDataType{

    private final Collection<URI> allowedValues;
    private final URI parent;
    
    public AllowedUriType(URI parent, Collection<URI> allowedValues){
        this.parent = parent;
        this.allowedValues = allowedValues;
    }
    
    /*public AllowedUriType(Element element){
        NodeList list = element.getElementsByTagName(SchemaConstants.ALLOWED_VALUE);
        for (int i = 0; i < list.getLength(); i++){
            Node node = list.item(i);
            String stringValue = node.getFirstChild().getNodeValue();
            allowedValues.add(new URIImpl(stringValue));
        }
    }
    */
    @Override
    public boolean correctType(Value value) {
        return allowedValues.contains(value);
    }

    @Override
    public String getCorrectType() {
        return parent + " URI for example: " + allowedValues;
    }

  
}
