// BridgeDb,
// An abstraction layer for identifier mapping services, both local and online.
//
// Copyright 2006-2009  BridgeDb developers
// Copyright 2012-2013  Christian Y. A. Brenninkmeijer
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

import java.util.ArrayList;
import java.util.List;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.URIImpl;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import uk.ac.manchester.cs.openphacts.valdator.constants.SchemaConstants;

/**
 *
 * @author Christian
 */
public class AllowedUriType implements MetaDataType{

    List<URI> allowedValues = new ArrayList<URI>();
    
    public AllowedUriType(Element element){
        NodeList list = element.getElementsByTagName(SchemaConstants.ALLOWED_VALUE);
        for (int i = 0; i < list.getLength(); i++){
            Node node = list.item(i);
            String stringValue = node.getFirstChild().getNodeValue();
            allowedValues.add(new URIImpl(stringValue));
        }
    }
    
    @Override
    public boolean correctType(Value value) {
        return allowedValues.contains(value);
    }

    @Override
    public String getCorrectType() {
        return " URI in " + allowedValues;
    }

  
}
