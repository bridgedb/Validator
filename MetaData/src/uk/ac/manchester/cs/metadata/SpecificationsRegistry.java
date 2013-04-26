package uk.ac.manchester.cs.metadata;

import java.util.HashMap;
import java.util.Properties;
import java.util.Set;
import uk.ac.manchester.cs.rdftools.VoidValidatorException;
import uk.ac.manchester.cs.utils.ConfigReader;

/**
 *
 * @author Christian
 */
public class SpecificationsRegistry {
    
    private static HashMap<String,MetaDataSpecification> register = null;
    private static HashMap<String,String> descriptions = null;
    
    private static final String DESCRIPTION = "description";
    private static final String FILE = "file";
    private static final String SPECIFICATIONS_PREFIX = "specification.";

    private SpecificationsRegistry(){
    }
    
    public static void init() throws VoidValidatorException{
        if (register != null){
            return;
        }
        Properties properties = ConfigReader.getProperties();
        register = new HashMap<String,MetaDataSpecification>();
        descriptions = new HashMap<String,String>();
        Set<String> keys = properties.stringPropertyNames();
        for (String key:keys){
            if (key.startsWith(SPECIFICATIONS_PREFIX)){
                String[] parts = key.split("\\.");
                if (parts.length == 3){
                     if (parts[2].equals(FILE)){
                        MetaDataSpecification specification = new MetaDataSpecification(properties.getProperty(key));
                        if (descriptions.containsKey(parts[1])){
                            specification.setDescription(descriptions.get(parts[1]));
                            descriptions.remove(parts[1]);
                        }
                        register.put(parts[1], specification);
                    } else if (parts[2].equals(DESCRIPTION)){
                        if (register.containsKey(parts[1])){
                            MetaDataSpecification specification = register.get(parts[1]);
                            specification.setDescription(properties.getProperty(key));
                        } else {
                            descriptions.put(parts[1], properties.getProperty(key));
                        }
                    } else {
                        throw new VoidValidatorException ("Unexpected  " + SPECIFICATIONS_PREFIX +  " property." + key );                    
                    }
                } else {
                    throw new VoidValidatorException ("Unexpected " + SPECIFICATIONS_PREFIX +  " property. It should be three dot seperated parts." + key );
                }
            }
        }
        if (!descriptions.isEmpty()){
            throw new VoidValidatorException ("Found " + SPECIFICATIONS_PREFIX + "*." + DESCRIPTION + 
                    " property(ies). " + descriptions + " But no loading instruction.");
        }
   }
    
   public static MetaDataSpecification specificationByName(String name){
       return register.get(name);
       
   }
    
   public static Set<String> getSpecificationNames(){
       return register.keySet();
   }
}
