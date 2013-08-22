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
import org.openrdf.model.Resource;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LiteralImpl;

/**
 *
 * @author Christian
 */
public class ValueBean {

    public static Value asValue(ValueBean bean) {
        if (bean instanceof ResourceBean){
            return ResourceBean.asResource((ResourceBean)bean);
        }
        if (bean instanceof LiteralBean){
            return LiteralBean.asLiteral((LiteralBean)bean);
        }
        throw new UnsupportedOperationException("Not yet implemented " + bean.getClass());
    }

    public static Value asValue(String valueString) {
        if (valueString == null || valueString.isEmpty()){
            return null;
        }
        Resource resource = ResourceBean.asResource(valueString);
        if (resource != null){
            return resource;
        }
        return new LiteralImpl(valueString);
    }

    static ValueBean asBean(Value value) {
        if (value == null){
            return null;
        }
        if (value instanceof Resource){
            return ResourceBean.asBean((Resource)value);
        }
        if (value instanceof Literal){
            return LiteralBean.asBean((Literal)value);
        }
        throw new UnsupportedOperationException("Not yet implemented");
    }
    
}
