/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.manchester.cs.metadata.type;

import uk.ac.manchester.cs.constants.OwlConstants;
import uk.ac.manchester.cs.constants.RdfsConstants;
import uk.ac.manchester.cs.constants.SchemaConstants;
import uk.ac.manchester.cs.constants.XMLSchemaConstants;
import uk.ac.manchester.cs.rdftools.VoidValidatorException;

/**
 *
 * @author Christian
 */
public class MetaDataTypeFactory {
    
    public static MetaDataType factory(String objectClass) throws VoidValidatorException{
        if (OwlConstants.THING.equalsIgnoreCase(objectClass)){
            return new UriType();
        }
        if (RdfsConstants.LITERAL.equalsIgnoreCase(objectClass)){
            return new LiteralType();
        }
        if (objectClass.startsWith("xsd:")){
            objectClass = XMLSchemaConstants.PREFIX + objectClass.substring(4);
        }
        if (objectClass.equalsIgnoreCase(XMLSchemaConstants.STRING.stringValue())){
            return new StringType();
        }
        if (objectClass.startsWith(XMLSchemaConstants.PREFIX)){
            return XsdType.getByType(objectClass);
        }
        throw new VoidValidatorException ("Unexpected " + SchemaConstants.CLASS + " " + objectClass);
        
    }
}
