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

import java.util.HashSet;
import java.util.Set;
import org.openrdf.model.Resource;
import uk.ac.manchester.cs.datadesc.validator.metadata.MetaDataSpecification;
import uk.ac.manchester.cs.datadesc.validator.metadata.ResourceMetaData;
import uk.ac.manchester.cs.datadesc.validator.rdftools.VoidValidatorException;

/**
 *
 * @author Christian
 */
public class SpecificationBean {
    
    private String description;
    
    private Set<ResourceBean> resources = new HashSet<ResourceBean>();
    
    public SpecificationBean(){
    }

    public SpecificationBean(MetaDataSpecification specification) throws VoidValidatorException{
        description = specification.getDescription();
        for (Resource resource: specification.getKnownResources()){
            ResourceMetaData metaData = specification.getResourceMetaData(resource);
            resources.add(new ResourceBean(metaData));
        }
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the resources
     */
    public Set<ResourceBean> getResources() {
        return resources;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @param resources the resources to set
     */
    public void setResources(Set<ResourceBean> resources) {
        this.resources = resources;
    }
    
}

