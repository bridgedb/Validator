// BridgeDb,
// An abstraction layer for identifier mapping services, both local and online.
//
// Copyright      2012  Christian Y. A. Brenninkmeijer
// Copyright      2012  OpenPhacts
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
package uk.ac.manchester.cs.server;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.log4j.Logger;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import uk.ac.manchester.cs.metadata.MetaDataSpecification;
import uk.ac.manchester.cs.metadata.SpecificationsRegistry;
import uk.ac.manchester.cs.rdftools.RdfFactory;
import uk.ac.manchester.cs.rdftools.RdfReader;
import uk.ac.manchester.cs.rdftools.VoidValidatorException;
import uk.ac.manchester.cs.validator.Validator;

/**
 *
 * @author Christian
 */
public class WsValidatorServer {
        
    static final Logger logger = Logger.getLogger(WsValidatorServer.class);
    private static final int DESCRIPTION_WIDTH = 100;
    private static final String DESCRIPTION_FIELD = "Description";
    
    public WsValidatorServer() {
        logger.info("Validator Server setup");
    }
            
    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response welcomeMessage(@Context HttpServletRequest httpServletRequest) throws VoidValidatorException {
        StringBuilder sb = getHeader();

        appendValidationForm(sb, null, null, httpServletRequest);                     
        sb.append("</body></html>");
        return Response.ok(sb.toString(), MediaType.TEXT_HTML).build();
    }

    private StringBuilder getHeader(){
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\"?>");
        sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" "
                + "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">");
        sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\">");
        sb.append("<meta http-equiv=\"content-type\" content=\"text/html; charset=ISO-8859-1\"/>");
        sb.append("<head><title>OPS Validator</title></head><body>");
        sb.append("<h1>Open PHACTS Validator</h1>");        
        return sb;
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path(WsValidationConstants.VALIDATE)
    public Response validate(@QueryParam(WsValidationConstants.URI)String uri, 
            @QueryParam(WsValidationConstants.SPECIFICATION)String specification,
            @Context HttpServletRequest httpServletRequest) throws VoidValidatorException {
        StringBuilder sb = getHeader();
        appendValidationResult(sb, uri, specification);
        appendValidationForm(sb, uri, specification, httpServletRequest);                             
        sb.append("</body></html>");
        return Response.ok(sb.toString(), MediaType.TEXT_HTML).build();        
    }

    private void appendValidationResult(StringBuilder sb, String uri, String specification) throws VoidValidatorException  {
        if (uri == null && specification == null){
            return;
        }
        if (uri == null){
            sb.append("Please specify the URI to validate.<br>");
        }
        URI URI = null;
        try {
            URI = new URIImpl(uri);
        } catch (Exception ex){
            sb.append(ex.getMessage());
            sb.append("<br>");
        }
        MetaDataSpecification specs = null;
        if (specification == null){
            sb.append("Please specify the specification you want to use.<br>");
        } else {
            try {
                specs = SpecificationsRegistry.specificationByName(specification);
            } catch (Exception ex){
                sb.append(ex.getMessage());
                sb.append("<br>");
            }
            if (specs == null){
                sb.append("Sorry no specification with name ");
                sb.append(specification);
                sb.append(" known!<br>");            
            }
        }
        appendValidationResult(sb, URI, specs);
    }
    
    private void appendValidationResult(StringBuilder sb, URI URI, MetaDataSpecification specifications) throws VoidValidatorException {
        if (URI == null){
            return;
        }
        if (specifications == null){
            return;
        }
        try {
            RdfReader reader = RdfFactory.getMemory();
            Resource context = reader.loadURI(URI.stringValue());
            String results = Validator.validate(reader, context, specifications);
            String[] lines = results.split("\\r?\\n");
            int maxWidth = 0;
            for (String line:lines){
                if (line.length() > maxWidth){
                    maxWidth = line.length();
                }
            }
            sb.append("<fieldset><legend>Validator Results</legend>");
            sb.append("<textarea rows=\"");
            sb.append(lines.length);
            sb.append("\" cols=\"");
            sb.append(maxWidth);
            sb.append("\" readonly >");
            sb.append(results);
            sb.append("</textarea></fieldset>");
        } catch (Exception ex){
            sb.append(ex.getMessage());
            sb.append("<br/>");
            Throwable throwable = ex.getCause();
            while (throwable != null){
                sb.append("Caused by:");
                sb.append(throwable.getMessage());
                sb.append("<br/>");
                throwable = throwable.getCause();
            }
         }     
    }
    
    private void appendValidationForm(StringBuilder sb, String URI, String specification, HttpServletRequest httpServletRequest) throws VoidValidatorException {
     	sb.append("<form method=\"get\" action=\"");
        sb.append(httpServletRequest.getContextPath());
    	sb.append("/");
    	sb.append(WsValidationConstants.VALIDATE);
    	sb.append("\">");
    	sb.append("<fieldset>");
    	sb.append("<legend>Validator Input</legend>");
    	sb.append("<p><label for=\"");
    	sb.append(WsValidationConstants.URI);
    	sb.append("\">Input URI</label>");
    	sb.append("<input type=\"text\" id=\"");
    	sb.append(WsValidationConstants.URI);
    	sb.append("\" name=\"");
    	sb.append(WsValidationConstants.URI);
    	sb.append("\" style=\"width:80%\"");
        if (URI != null){
        	sb.append("\" value=\"");
            sb.append(URI);            
        }
    	sb.append("\"/></p>");
    	generateSpecificationsSelector(sb, specification);
    	sb.append("<p><input type=\"submit\" value=\"Submit\"/></p>");
    	sb.append("<p>Note: If the new page does not open click on the address bar and press enter</p>");
    	sb.append("</fieldset></form>\n");
        generateSpecificationsScript(sb);
   }

    private void generateSpecificationsSelector(StringBuilder sb, String specification) throws VoidValidatorException {
		Set<String> names = SpecificationsRegistry.getSpecificationNames();
        sb.append("<p>");
    	sb.append(WsValidationConstants.SPECIFICATION);
        sb.append("<select name=\"");
    	sb.append(WsValidationConstants.SPECIFICATION);
    	sb.append("\" onchange=\"populateData(this)\">");
        int maxDescription = 0;
        if (!names.contains(specification)){
            sb.append("<option SELECTED value=\"blank\">Please Select</option>");
        }
		for (String name : names) {
			sb.append("<option value=\"");
			sb.append(name);
            if (name.equals(specification)){
                sb.append("\" SELECTED >");                
            } else {
                sb.append("\">");
            }
			sb.append(name);
			sb.append("</option>");
            MetaDataSpecification specs = SpecificationsRegistry.specificationByName(name);
            String description = specs.getDescription();
            if (description.length() > maxDescription){
                maxDescription = description.length();
            }
		}
    	sb.append("</select>\n");
    	sb.append("<br/>\n");
        int rows = maxDescription / DESCRIPTION_WIDTH + 1;
        sb.append("<textarea name=\"");
        sb.append(DESCRIPTION_FIELD);
        sb.append("\" rows=\"");
        sb.append(rows);
        sb.append("\" cols=\"");
        sb.append(DESCRIPTION_WIDTH);      
        sb.append("\" disabled=\"disabled\"/>");
        if (names.contains(specification)){
           MetaDataSpecification specs = SpecificationsRegistry.specificationByName(specification); 
            sb.append(specs.getDescription());
        }
        sb.append("</textarea>\n");
   }

    private void generateSpecificationsScript(StringBuilder sb) throws VoidValidatorException {
		Set<String> names = SpecificationsRegistry.getSpecificationNames();
        sb.append("<script>");
        sb.append(   "function populateData(sel){");
        sb.append(      "var form = sel.form,");
        sb.append(         "value = sel.options[sel.selectedIndex].value;");
        sb.append(      "switch(value){");
		for (String name : names) {
            MetaDataSpecification specs = SpecificationsRegistry.specificationByName(name);
            sb.append("case '");
            sb.append(name);
            sb.append("':");
            sb.append("form.");
            sb.append(DESCRIPTION_FIELD);
            sb.append(".value = '");
            sb.append(specs.getDescription());
            sb.append("';");
            sb.append("break;");
        }
        sb.append("default:");
        sb.append("}");
        sb.append("}");
        sb.append("</script>");
    }

}


