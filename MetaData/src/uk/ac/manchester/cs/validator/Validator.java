package uk.ac.manchester.cs.validator;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import uk.ac.manchester.cs.constants.RdfConstants;
import uk.ac.manchester.cs.metadata.MetaDataSpecification;
import uk.ac.manchester.cs.metadata.ResourceMetaData;
import uk.ac.manchester.cs.rdftools.RdfInterface;
import uk.ac.manchester.cs.rdftools.VoidValidatorException;

/**
 *
 * @author Christian
 */
public class Validator {
    
    private final RdfInterface reader;
    private final Resource context;
    private final MetaDataSpecification specifications;
    private final StringBuilder builder;
    private final Set<Resource> resourcesToCheck;
    private final Set<Resource> resourcesChecked;
    private final Set<Resource> extraResourcesToCheck;
 
    public static String FAILED = "Validation Failed!";
    public static String SUCCESS = "Validation Successfull!";
    
    public static String validate(RdfInterface reader, Resource context, MetaDataSpecification specifications) throws VoidValidatorException{
        Validator validator = new Validator(reader, context, specifications);
        validator.validate();
        return validator.builder.toString();
    }

    public void addResourceToValidate(Resource resource){
        if (resourcesToCheck.contains(resource)){
            return;
        }
        if (resourcesChecked.contains(resource)){
            return;
        }
        extraResourcesToCheck.add(resource);
    }
    
    private Validator(RdfInterface reader, Resource context, MetaDataSpecification specifications) throws VoidValidatorException{
        this.reader = reader;
        this.context = context;
        this.specifications = specifications;
        builder = new StringBuilder();
        resourcesToCheck = getResourcesToCheck();
        resourcesChecked = new HashSet<Resource>();
        extraResourcesToCheck = new HashSet<Resource>();
    }
    
    private Set<Resource> getResourcesToCheck() throws VoidValidatorException {
        HashSet<Resource> resourcesToCheck = new HashSet<Resource>();
        List<Statement> typeStatements = reader.getStatementList(null, RdfConstants.TYPE_URI, null, context);
        for (Statement typeStatement:typeStatements){
            resourcesToCheck.add(typeStatement.getSubject());
        }
        return resourcesToCheck;
   }
    
   private void validate() throws VoidValidatorException{
        boolean error = false;
        while (!resourcesToCheck.isEmpty()){
            for (Resource resource:resourcesToCheck){
                 if (appendValidate(resource)){
                    error = true;
                }
            }
            resourcesToCheck.addAll(extraResourcesToCheck);
            extraResourcesToCheck.clear();
            resourcesToCheck.removeAll(resourcesChecked);
        }
        if (error){
            builder.append(FAILED);
        } else {
            builder.append(SUCCESS);            
        }
    }

    private boolean appendValidate(Resource resource) throws VoidValidatorException{
        this.resourcesChecked.add(resource);
        List<Statement> typeStatements = reader.getStatementList(resource, RdfConstants.TYPE_URI, null, context);
        boolean error = false;
        boolean unknownType = true;
        for (Statement typeStatement:typeStatements){
            ResourceMetaData specs = specifications.getResourceMetaData((URI) typeStatement.getObject());
            if (specs != null){
                if (specs.appendValidate(builder, reader, typeStatement.getSubject(), context, false, 0, this)){
                    error = true;
                }
                unknownType = false;
            } 
        }
        if (unknownType){
            builder.append("Unable to validate ");
            builder.append(resource);
            builder.append(" as has no known type. \n");
            error = true;
        }
        return error;
    }

 }
