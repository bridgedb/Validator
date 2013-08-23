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

import javax.xml.bind.annotation.XmlRootElement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

/**
 *
 * @author Christian
 */
@XmlRootElement(name="URI")
public class URIBean extends ResourceBean {

    private String address;
    
    public static URI asURI(URIBean bean) {
        if (bean == null){
            return null;
        }
        return new URIImpl(bean.getAddress());
    }

    public static URI asURI(String string) {
        if (string == null || string.isEmpty()){
            return null;
        }
        try {
            URI uri = new URIImpl(string);
            return uri;
        } catch (IllegalArgumentException ex){
            //do nothing
        }
        return null;
    }

    public static URIBean asBean(URI uri) {
        if (uri == null){
            return null;
        }
        URIBean bean = new URIBean();
        bean.setAddress(uri.stringValue());
        return bean;
    }

    /**
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param address the address to set
     */
    public void setAddress(String address) {
        this.address = address;
    }
    
}
