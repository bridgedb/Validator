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
package uk.ac.manchester.cs.openphacts.valdator.bean;

import javax.xml.bind.annotation.XmlRootElement;
import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.URIImpl;

/**
 *
 * @author Christian
 */
@XmlRootElement(name="Literal")
public class LiteralBean extends ValueBean{

    private String type;
    private String value;
    
    public static Literal asLiteral(LiteralBean bean) {
        if (bean.getType() == null){
            return new LiteralImpl(bean.getValue());
        }
        URI type = new URIImpl(bean.getType());
        return new LiteralImpl(bean.getValue(), type);
    }

    public static Literal asLiteral(String literalString) {
        if (literalString == null || literalString.isEmpty()){
            return null;
        }
        return new LiteralImpl(literalString);
    }

    static LiteralBean asBean(Literal literal) {
        if (literal == null){
            return null;
        }
        LiteralBean bean = new LiteralBean();
        bean.setType(literal.getDatatype());
        bean.setValue(literal.stringValue());
        return bean;
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

    public void setType(URI type) {
        if (type != null){
            this.type = type.stringValue();
        }
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }
    
}
