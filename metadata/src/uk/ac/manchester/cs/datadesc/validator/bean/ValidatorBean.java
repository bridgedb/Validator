/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.manchester.cs.datadesc.validator.bean;

import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import uk.ac.manchester.cs.datadesc.validator.metadata.LinkedResource;
import uk.ac.manchester.cs.datadesc.validator.metadata.MetaDataAlternatives;
import uk.ac.manchester.cs.datadesc.validator.metadata.MetaDataBase;
import uk.ac.manchester.cs.datadesc.validator.metadata.MetaDataGroup;
import uk.ac.manchester.cs.datadesc.validator.metadata.PropertyMetaData;
import uk.ac.manchester.cs.datadesc.validator.rdftools.VoidValidatorException;

/**
 *
 * @author Christian
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.WRAPPER_OBJECT, property="type")
@JsonSubTypes({
        @JsonSubTypes.Type(value=AlternativesBean.class, name=Names.ALTERNATIVES_NAME),
        @JsonSubTypes.Type(value=HasChildBean.class, name=Names.GROUPS_NAME),
        @JsonSubTypes.Type(value=LinkedBean.class, name=Names.LINKED_RESOURCE_NAME),
        @JsonSubTypes.Type(value=PropertyBean.class, name=Names.PROPERTY_NAME)
    }) 
public abstract class ValidatorBean {
    
    public static ValidatorBean convertToBean(MetaDataBase metaData) throws VoidValidatorException {
        if (metaData instanceof PropertyMetaData){
            return new PropertyBean((PropertyMetaData)metaData);
        }
        if (metaData instanceof LinkedResource){
            return new LinkedBean((LinkedResource)metaData);
        }
        if (metaData instanceof MetaDataAlternatives){
            return new AlternativesBean((MetaDataAlternatives)metaData);
        }
        if (metaData instanceof MetaDataGroup){
            return new HasChildBean((MetaDataGroup)metaData);
        }
        throw new VoidValidatorException("Unexpected MetaData type " + metaData.getClass());
    }
    
}
