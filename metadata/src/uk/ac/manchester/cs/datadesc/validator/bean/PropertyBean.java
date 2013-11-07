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
package uk.ac.manchester.cs.datadesc.validator.bean;

import org.codehaus.jackson.annotate.JsonTypeName;
import uk.ac.manchester.cs.datadesc.validator.metadata.PropertyMetaData;

/**
 *
 * @author Christian
 */
@JsonTypeName(Names.PROPERTY_NAME)
class PropertyBean extends CardinalityBean{

    private String type;
    
    public PropertyBean(PropertyMetaData metaData) {
        super(metaData);
        type = metaData.getMetaDataType().getCorrectType();
    }
    
    public PropertyBean() {
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

}
