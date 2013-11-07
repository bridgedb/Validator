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
import uk.ac.manchester.cs.datadesc.validator.metadata.CardinalityMetaData;

/**
 *
 * @author Christian
 */
@JsonTypeName(Names.PROPERTY_NAME)
class CardinalityBean extends ValidatorBean{

    //@XmlElement(name="RequirementLevel")
    private String level;

    //@XmlElement(name="Predicate")
    private String predicate;

    //@XmlElement(name="Cardinality")
    private Integer cardinality;
    
    public CardinalityBean(CardinalityMetaData metaData) {
        level = metaData.getRequirementLevel().name();
        predicate = metaData.getPredicate().stringValue();
        if (metaData.getCardinality() > 0){
            cardinality = metaData.getCardinality();
        }
    }
    
    public CardinalityBean() {
    }

    /**
     * @return the level
     */
    public String getLevel() {
        return level;
    }

    /**
     * @param level the level to set
     */
    public void setLevel(String level) {
        this.level = level;
    }

    /**
     * @return the predicate
     */
    public String getPredicate() {
        return predicate;
    }

    /**
     * @param predicate the predicate to set
     */
    public void setPredicate(String predicate) {
        this.predicate = predicate;
    }

    /**
     * @return the cardinality
     */
    public Integer getCardinality() {
        return cardinality;
    }

    /**
     * @param cardinality the cardinality to set
     */
    public void setCardinality(Integer cardinality) {
        this.cardinality = cardinality;
    }
}
