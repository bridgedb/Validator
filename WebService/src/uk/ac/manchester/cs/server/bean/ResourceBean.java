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
package uk.ac.manchester.cs.server.bean;

import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

/**
 *
 * @author Christian
 */
@XmlRootElement(name="Resource")
public class ResourceBean extends ValueBean{

    public static Resource asResource(ResourceBean bean) {
        if (bean instanceof URIBean){
            return URIBean.asURI((URIBean)bean);
        }
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public static Resource asResource(String string) {
        if (string == null || string.isEmpty()){
            return null;
        }
        try {
            URI uri = new URIImpl(string);
            return uri;
        } catch (IllegalArgumentException ex){
            //do nothing
        }
        System.out.println(string);
        return null;
    }

    public static Resource[] asResourceArray(List<String> contextStrings) {
        if (contextStrings == null){
            return new Resource[0];
        }
        Resource[] results = new Resource[contextStrings.size()];
        for (int i = 0; i< contextStrings.size(); i++){
            results[i] = asResource(contextStrings.get(i));
        }
        return results;
    }

    public static ResourceBean asBean(Resource result) {
        if (result == null){
            return null;
        }
        if (result instanceof URI){
            return URIBean.asBean((URI)result);
        }
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
