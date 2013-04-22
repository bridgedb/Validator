/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.manchester.cs.validator;

import java.util.List;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import uk.ac.manchester.cs.constants.RdfConstants;
import uk.ac.manchester.cs.metadata.MetaDataSpecification;
import uk.ac.manchester.cs.metadata.ResourceMetaData;
import uk.ac.manchester.cs.rdftools.RdfReader;
import uk.ac.manchester.cs.rdftools.VoidValidatorException;

/**
 *
 * @author Christian
 */
public class Validator {
    
    final RdfReader reader;
    final MetaDataSpecification specifications;
    
    public Validator(RdfReader reader, MetaDataSpecification specifications){
        this.reader = reader;
        this.specifications = specifications;
    }
    
    public String validate() throws VoidValidatorException{
        StringBuilder builder = new StringBuilder();
        List<Statement> typeStatements = reader.getStatementList(null, RdfConstants.TYPE_URI, null);
        for (Statement typeStatement:typeStatements){
            System.out.println(typeStatement);
            ResourceMetaData specs = specifications.getResourceMetaData((URI) typeStatement.getObject());
            if (specs != null){
                specs.appendValidate(builder, reader, typeStatement.getSubject(), false, 0);
            } else {
                builder.append("Unable to validate ");
                builder.append(typeStatement.getSubject());
                builder.append(" as no specifications found for ");
                builder.append(typeStatement.getObject());
                builder.append("\n");
            }
        }
        return builder.toString();
    }
}
