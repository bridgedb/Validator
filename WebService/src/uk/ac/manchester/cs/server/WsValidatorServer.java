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
package uk.ac.manchester.cs.server;

import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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
import org.openrdf.rio.RDFFormat;
import uk.ac.manchester.cs.openphacts.valdator.metadata.MetaDataSpecification;
import uk.ac.manchester.cs.openphacts.valdator.metadata.SpecificationsRegistry;
import uk.ac.manchester.cs.openphacts.valdator.rdftools.RdfFactory;
import uk.ac.manchester.cs.openphacts.valdator.rdftools.RdfReader;
import uk.ac.manchester.cs.openphacts.valdator.rdftools.VoidValidatorException;
import uk.ac.manchester.cs.openphacts.validator.Validator;

    
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

        appendValidationForm(sb, null, null, null, null, true, httpServletRequest);                     
        sb.append("</body></html>");
        return Response.ok(sb.toString(), MediaType.TEXT_HTML).build();
    }

    private StringBuilder getHeader(){
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\"?>");
        sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" "
                + "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">");
        sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\">");
        sb.append("<meta http-equiv=\"content-type\" content=\"text/html; charset=ISO-8859-1\"/>\n");
        sb.append("<head><title>OPS Validator</title></head>\n<body>");
        sb.append("<h1>Open PHACTS Validator</h1>\n");        
        return sb;
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path(WsValidationConstants.VALIDATE)
    public Response validateGet(@QueryParam(WsValidationConstants.RDF_FORMAT) String rdfFormat, 
            @QueryParam(WsValidationConstants.TEXT) String text, 
            @QueryParam(WsValidationConstants.URI) String uri, 
            @QueryParam(WsValidationConstants.SPECIFICATION) String specification,
            @QueryParam(WsValidationConstants.INCLUDE_WARNINGS) boolean includeWarning,
            @Context HttpServletRequest httpServletRequest) throws VoidValidatorException {
        return validate(rdfFormat, text, uri, specification, includeWarning, httpServletRequest);
    }
    
    @POST
    @Produces(MediaType.TEXT_HTML)
    @Path(WsValidationConstants.VALIDATE)
    public Response validate(@QueryParam(WsValidationConstants.RDF_FORMAT) String rdfFormat, 
            @QueryParam(WsValidationConstants.TEXT) String text, 
            @QueryParam(WsValidationConstants.URI) String uri, 
            @QueryParam(WsValidationConstants.SPECIFICATION) String specification,
            @QueryParam(WsValidationConstants.INCLUDE_WARNINGS) Boolean includeWarning,
            @Context HttpServletRequest httpServletRequest) throws VoidValidatorException {        
        StringBuilder sb = getHeader();
        appendValidationResult(sb, rdfFormat, text, uri, specification, includeWarning);
        appendValidationForm(sb, rdfFormat, text, uri, specification, includeWarning, httpServletRequest);                             
        sb.append("</body></html>");
        return Response.ok(sb.toString(), MediaType.TEXT_HTML).build();        
    }

    private void appendValidationResult(StringBuilder sb, String format, String text, String uri, String specification, 
            Boolean includeWarnings) throws VoidValidatorException  {   
        boolean error = false;
        RDFFormat rdfFormat = null;
        if (format != null && !format.isEmpty()){
            for (RDFFormat check:RDFFormat.values()){
                if (check.getName().equals(format)){
                    rdfFormat = check;
                }
            }
            if (rdfFormat == null){
                sb.append(WsValidationConstants.RDF_FORMAT);
                sb.append(" ");
                sb.append(format);
                sb.append(" is not known. Please select one from the dropdown list.<br>");
                error = true;
            }
        }       
        if (text == null){
            text = "";
        }
        URI URI = null;
        if (uri != null && !uri.isEmpty()){
            try {
                URI = new URIImpl(uri);
            } catch (Exception ex){
                sb.append("Error with ");
                sb.append(WsValidationConstants.URI);
                sb.append(" \"");
                sb.append(uri);
                sb.append("\"<br>");
                sb.append(ex.getMessage());
                sb.append("<br>");
                error = true;
            }
        }
        MetaDataSpecification specs = null;
        if (specification != null && !specification.isEmpty()){
            try {
                specs = SpecificationsRegistry.specificationByName(specification);
                if (specs == null){
                    sb.append("Sorry no ");
                    sb.append(WsValidationConstants.SPECIFICATION);
                    sb.append(" with name ");
                    sb.append(specification);
                    sb.append(" known!<br>");  
                    error = true;
                }
            } catch (Exception ex){
                sb.append("Error getting  ");
                sb.append(WsValidationConstants.SPECIFICATION);
                sb.append(" with name ");
                sb.append(specification);
                sb.append("<br>");            
                sb.append(ex.getMessage());
                sb.append("<br>");
                error = true;
            }
        }
        if (error){
            return;
        }
        if (text.isEmpty() && URI == null){
            return;
        }
        if (specs == null){
            sb.append("Please specify a ");
            sb.append(WsValidationConstants.SPECIFICATION);
            sb.append("<br>");
            return;
        }
       if (URI == null){
            appendValidationResult(sb, rdfFormat, text, specs, includeWarnings);
        } else {
            if (text.isEmpty()){
                appendValidationResult(sb, rdfFormat, URI, specs, includeWarnings);
            } else {
                sb.append("Please clear either the ");
                sb.append(WsValidationConstants.TEXT);
                sb.append(" or the ");
                sb.append(WsValidationConstants.URI);
                sb.append("parameter! <br>");         
            }
        }      
    }
    
    private void appendValidationResult(StringBuilder sb, RDFFormat rdfFormat, String text, 
            MetaDataSpecification specifications, Boolean includeWarnings) throws VoidValidatorException {
        try {
            if (rdfFormat == null){
                sb.append("You must supply an ");
                sb.append(WsValidationConstants.RDF_FORMAT);
                sb.append(" parameter when using a ");
                sb.append(WsValidationConstants.TEXT);
                sb.append("parameter.<br>\n");
                return;
            }
             
            RdfReader reader = RdfFactory.getMemory();
            Resource context = reader.loadString(text, rdfFormat);
            String results = Validator.validate(reader, context, specifications, includeWarnings);
            appendValidationResult(sb, results);
        } catch (Exception ex){
            appendException(sb, ex);
        }     
     }
    
     private void appendValidationResult(StringBuilder sb, RDFFormat rdfFormat, URI URI, 
             MetaDataSpecification specifications, Boolean includeWarnings) throws VoidValidatorException {
         try {
            RdfReader reader = RdfFactory.getMemory();
            Resource context = reader.loadURI(URI.stringValue(), rdfFormat);
            String results = Validator.validate(reader, context, specifications, includeWarnings);
            appendValidationResult(sb, results);
        } catch (Exception ex){
            appendException(sb, ex);
        }     
     }
     
     private void appendValidationResult(StringBuilder sb, String results) {
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
    }

     private void appendException(StringBuilder sb, Exception ex) throws VoidValidatorException {
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
    
    private void appendValidationForm(StringBuilder sb, String format, String text, String uri, String specification, 
            Boolean includeWarnings, HttpServletRequest httpServletRequest) throws VoidValidatorException {
     	sb.append("<form method=\"get\" action=\"");
        sb.append(httpServletRequest.getContextPath());
        sb.append("/");
    	sb.append(WsValidationConstants.VALIDATE);
    	sb.append("\" onsubmit=\"return checkform(this);\"");
    	sb.append("\">");
    	sb.append("<fieldset>");
    	sb.append("<legend>Validator Input</legend>\n");
        appendRDFFormat(sb, format);
        appendIncludeWarnings(sb, includeWarnings);
        appendText(sb, text);
        appendUri(sb, uri);
    	generateSpecificationsSelector(sb, specification);
    	sb.append("<p><input type=\"submit\" value=\"Submit\"/></p>");
    	sb.append("</fieldset></form>\n");
    	sb.append("<p>Note: If the new page does not open click on the address bar and press enter</p>");
        generateScripts(sb);
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
            sb.append("<option SELECTED value=\"\">Please Select</option>");
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

    private void generateScripts(StringBuilder sb) throws VoidValidatorException {
        sb.append("<script>\n");
        generateSpecificationsScript(sb);
        generateCheckForm(sb);
        sb.append("</script>\n");
    }

    private void generateSpecificationsScript(StringBuilder sb) throws VoidValidatorException {
		Set<String> names = SpecificationsRegistry.getSpecificationNames();
        sb.append(   "function populateData(sel){\n");
        sb.append(      "var form = sel.form,\n");
        sb.append(         "value = sel.options[sel.selectedIndex].value;\n");
        sb.append(      "switch(value){\n");
		for (String name : names) {
            MetaDataSpecification specs = SpecificationsRegistry.specificationByName(name);
            sb.append("case '");
            sb.append(name);
            sb.append("':\n");
            sb.append("form.");
            sb.append(DESCRIPTION_FIELD);
            sb.append(".value = '");
            sb.append(specs.getDescription());
            sb.append("';\n");
            sb.append("break;\n");
        }
        sb.append("default:\n");
        sb.append("}\n");
        sb.append("}\n");
    }

    private void generateCheckForm(StringBuilder sb) {
        sb.append("function checkform ( form )	{\n");
            // Check a specification selected 
            sb.append("if (form.");
                    sb.append(WsValidationConstants.SPECIFICATION);
                    sb.append(".value == \"\") {\n");
                sb.append("alert( \"Please select the ");
                        sb.append(WsValidationConstants.SPECIFICATION);
                        sb.append(" to use.\" );\n");
                sb.append("form.");
                        sb.append(WsValidationConstants.SPECIFICATION);
                       sb.append(".focus();\n");
            sb.append("return false ;\n");
            sb.append("}\n");
            //Check if URI or text selected. And if text RdfFormat is provided. **
            sb.append("if (form.");
                   sb.append(WsValidationConstants.TEXT);
                   sb.append(".value == \"\") {\n");
                sb.append("if (form.");
                        sb.append(WsValidationConstants.TEXT);
                        sb.append(".value == \"\"){\n");
                    sb.append("alert(\"Please provided either the ");
                            sb.append(WsValidationConstants.TEXT);
                            sb.append(" to validate or a ");
                            sb.append(WsValidationConstants.TEXT);
                            sb.append(" to the text.\" );\n");
                    sb.append("form.");
                            sb.append(WsValidationConstants.TEXT);
                            sb.append(".focus();\n");
                    sb.append("return false ;\n");
                sb.append("} else {\n");
                    sb.append("return true;\n");
                sb.append("}\n");
            sb.append("} else {\n");
                sb.append("if (form.");
                        sb.append(WsValidationConstants.URI);
                        sb.append(".value == \"\"){\n");
                    sb.append("if (form.");
                            sb.append(WsValidationConstants.RDF_FORMAT);
                            sb.append(".value == \"\"){\n");
                        sb.append("alert( \"Please select the ");
                                sb.append(WsValidationConstants.RDF_FORMAT);
                                sb.append(" to use.\" );\n");
                        sb.append("form.rdfFormat");
                                sb.append(WsValidationConstants.RDF_FORMAT);
                                sb.append(".focus();\n");
                        sb.append("return false ;\n");
                    sb.append("} else {\n");
                        sb.append("return true;\n");
                    sb.append("}\n");
                sb.append("}  else {\n");
                    sb.append("alert(\"Validate works on either the text to validate or a url to the text. Please clear one of the values.\" );\n");
                       sb.append(WsValidationConstants.TEXT);
                    sb.append("form.");
                            sb.append(WsValidationConstants.TEXT);
                            sb.append(".focus();\n");
                    sb.append("return false ;\n");
                sb.append("}\n");
            sb.append("}\n");
        sb.append("}\n");         
    }
    
    private void appendUri(StringBuilder sb, String uri) {     
    	sb.append("<p><label for=\"");
    	sb.append(WsValidationConstants.URI);
    	sb.append("\">Input URI</label>");
    	sb.append("<input type=\"text\" id=\"");
    	sb.append(WsValidationConstants.URI);
    	sb.append("\" name=\"");
    	sb.append(WsValidationConstants.URI);
    	sb.append("\" style=\"width:80%");
        if (uri != null){
        	sb.append("\" value=\"");
            sb.append(uri);            
        }
    	sb.append("\"/></p>\n");
    }
    
    private void appendRDFFormat(StringBuilder sb, String format) {
        sb.append("<p>");
    	sb.append(WsValidationConstants.RDF_FORMAT);
        sb.append("<select name=\"");
    	sb.append(WsValidationConstants.RDF_FORMAT);
    	sb.append("\">");
        int maxDescription = 0;
        RDFFormat rdfFormat = null;
        if (format != null && !format.isEmpty()){
            for (RDFFormat check:RDFFormat.values()){
                if (check.getName().equals(format)){
                    rdfFormat = check;
                }
            }
        } else {
            sb.append("<option SELECTED value=\"\">Please Select</option>");
        }
        for (RDFFormat aFormat:RDFFormat.values()){
			sb.append("<option value=\"");
			sb.append(aFormat.getName());
            if (aFormat.equals(rdfFormat)){
                sb.append("\" SELECTED >");                
            } else {
                sb.append("\">");
            }
			sb.append(aFormat);
			sb.append("</option>");
  		}
    	sb.append("</select>\n");
    	sb.append("<br/>\n");
    }

    private void appendIncludeWarnings(StringBuilder sb, Boolean includeWarnings) {
        sb.append("<input type=\"checkbox\" name=\"");
    	sb.append(WsValidationConstants.INCLUDE_WARNINGS);
        sb.append("\" value=\"true\"");
        if (includeWarnings == null || includeWarnings){
            sb.append(" checked");
        } 
        sb.append(" >Include warnings.<br>");
    }

    private void appendText(StringBuilder sb, String text) {
        sb.append("<p><textarea rows=\"15\" name=\"text\" style=\"width:100%; background-color: #EEEEFF;\">");
        if (text != null){
            sb.append(text);
        }
        sb.append("</textarea></p>\n");
    }

}


