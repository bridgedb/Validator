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
package uk.ac.manchester.cs.openphacts.valdator.server;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
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
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.resultio.TupleQueryResultFormat;
import org.openrdf.rio.RDFFormat;
import uk.ac.manchester.cs.openphacts.valdator.bean.ResourceBean;
import uk.ac.manchester.cs.openphacts.valdator.bean.StatementBean;
import uk.ac.manchester.cs.openphacts.valdator.bean.URIBean;
import uk.ac.manchester.cs.openphacts.valdator.bean.ValueBean;
import uk.ac.manchester.cs.openphacts.valdator.metadata.MetaDataSpecification;
import uk.ac.manchester.cs.openphacts.valdator.metadata.SpecificationsRegistry;
import uk.ac.manchester.cs.openphacts.valdator.rdftools.ExampleConstants;
import uk.ac.manchester.cs.openphacts.valdator.rdftools.RdfFactory;
import uk.ac.manchester.cs.openphacts.valdator.rdftools.RdfInterface;
import uk.ac.manchester.cs.openphacts.valdator.rdftools.RdfReader;
import uk.ac.manchester.cs.openphacts.valdator.rdftools.VoidValidatorException;
import uk.ac.manchester.cs.openphacts.valdator.ws.WSRdfInterface;
import uk.ac.manchester.cs.openphacts.valdator.ws.WsValidationConstants;
import uk.ac.manchester.cs.openphacts.validator.Validator;

    
/**
 *
 * @author Christian
 */
public abstract class WsValidatorServer implements WSRdfInterface{
        
    static final Logger logger = Logger.getLogger(WsValidatorServer.class);
    private static final int DESCRIPTION_WIDTH = 100;
    private static final String DESCRIPTION_FIELD = "Description";
    private static final String NO_ON_SUBMIT = null;
    
    private final RdfInterface rdfInterface;
    
    public WsValidatorServer() throws VoidValidatorException {
        logger.info("Validator Server setup");
        rdfInterface = RdfFactory.getFilebase();
    }
        
    public WsValidatorServer(RdfInterface rdfInterface) throws VoidValidatorException {
        logger.info("Validator Server setup");
        this.rdfInterface = rdfInterface;
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path(WsValidationConstants.VALIDATE_HOME)
    public Response validateHome(@Context HttpServletRequest httpServletRequest) throws VoidValidatorException {
        StringBuilder sb = topAndSide("Validation Service ",  httpServletRequest);
        sb.append("<h1> Welcome to the OpenPhacts Validation Service.</h1>");
        sb.append("<p>The form below gives an example of how to use the Validation service.</p>");
        sb.append("<p>Select another service from the side.</p>");
        sb.append("<p>To Try a different ontology please contact Christian.</p>");
        sb.append("<hr/>");
        formValidation(sb, null, null, null, null, true, httpServletRequest);                     
        footerAndEnd(sb);
        return Response.ok(sb.toString(), MediaType.TEXT_HTML).build();
    }
    
     protected void addValidatorSideBar(StringBuilder sb, HttpServletRequest httpServletRequest) {
        sb.append("<div class=\"menugroup\">OPS Validation Service</div>");
        addSideBarItem(sb, WsValidationConstants.VALIDATE_HOME, "Home", httpServletRequest);
        addSideBarItem(sb, WsValidationConstants.VALIDATE,WsValidationConstants.VALIDATE, httpServletRequest);
        addSideBarItem(sb, WsValidationConstants.STATEMENT_LIST, WsValidationConstants.STATEMENT_LIST,  httpServletRequest);
        addSideBarItem(sb, WsValidationConstants.BY_RESOURCE, WsValidationConstants.BY_RESOURCE,  httpServletRequest);
        addSideBarItem(sb, WsValidationConstants.SPARQL, WsValidationConstants.SPARQL, httpServletRequest);
        addSideBarItem(sb, WsValidationConstants.LOAD_URI, WsValidationConstants.LOAD_URI, httpServletRequest);
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path(WsValidationConstants.VALIDATE)
    public Response validate(@QueryParam(WsValidationConstants.RDF_FORMAT) String rdfFormat, 
            @QueryParam(WsValidationConstants.TEXT) String text, 
            @QueryParam(WsValidationConstants.URI) String uri, 
            @QueryParam(WsValidationConstants.SPECIFICATION) String specification,
            @QueryParam(WsValidationConstants.INCLUDE_WARNINGS) Boolean includeWarning,
            @Context HttpServletRequest httpServletRequest) throws VoidValidatorException {        
        StringBuilder sb = topAndSide("Validation Service", httpServletRequest);
        appendValidationResult(sb, rdfFormat, text, uri, specification, includeWarning);
        formValidation(sb, rdfFormat, text, uri, specification, includeWarning, httpServletRequest);                             
        footerAndEnd(sb);
        return Response.ok(sb.toString(), MediaType.TEXT_HTML).build();        
    }

    @POST
    @Produces(MediaType.TEXT_HTML)
    @Path(WsValidationConstants.VALIDATE)
    public Response validatePost(@QueryParam(WsValidationConstants.RDF_FORMAT) String rdfFormat, 
            @QueryParam(WsValidationConstants.TEXT) String text, 
            @QueryParam(WsValidationConstants.URI) String uri, 
            @QueryParam(WsValidationConstants.SPECIFICATION) String specification,
            @QueryParam(WsValidationConstants.INCLUDE_WARNINGS) Boolean includeWarning,
            @Context HttpServletRequest httpServletRequest) throws VoidValidatorException {        
        return this.validate(rdfFormat, text, uri, specification, includeWarning, httpServletRequest);
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
    
    void formValidation(StringBuilder sb, String format, String text, String uri, String specification, 
            Boolean includeWarnings, HttpServletRequest httpServletRequest) throws VoidValidatorException {
     	appendFormStart(sb, WsValidationConstants.VALIDATE, "return " + WsValidationConstants.CHECK_VALIDATE_FORM + "(this);", httpServletRequest);
        appendRDFFormat(sb, format);
        appendIncludeWarnings(sb, includeWarnings);
        appendText(sb, text);
        appendInput(sb, WsValidationConstants.URI, uri);
    	generateSpecificationsSelector(sb, specification);
    	appendFormEnd(sb, httpServletRequest);
        generateScripts(sb);
   }
    
   private void formByResource(StringBuilder sb, String resourceString, HttpServletRequest httpServletRequest) {
      	appendFormStart(sb, WsValidationConstants.STATEMENT_LIST,  NO_ON_SUBMIT, httpServletRequest);
        appendInput(sb, WsValidationConstants.RESOURCE, resourceString);
     	appendFormEnd(sb, httpServletRequest);
   }

   private void formStatementList(StringBuilder sb, String subjectString, String predicateString, String objectString, List<String> contextStrings, HttpServletRequest httpServletRequest) {
      	appendFormStart(sb, WsValidationConstants.STATEMENT_LIST,  NO_ON_SUBMIT, httpServletRequest);
        appendInput(sb, WsValidationConstants.SUBJECT, subjectString);
        appendInput(sb, WsValidationConstants.PREDICATE, predicateString);
        appendInput(sb, WsValidationConstants.OBJECT, objectString);
        appendInput(sb, WsValidationConstants.CONTEXT, "");
    	appendFormEnd(sb, httpServletRequest);
   }

    private void formLoadUri(StringBuilder sb, String uri, String formatName, HttpServletRequest httpServletRequest) {
     	appendFormStart(sb, WsValidationConstants.LOAD_URI, "return " + WsValidationConstants.CHECK_LOAD_FORM + "(this);", httpServletRequest);
        appendRDFFormat(sb, formatName);
        appendInput(sb, WsValidationConstants.URI, uri);
    	appendFormEnd(sb, httpServletRequest);
        generateCheckLoadForm(sb);
    }
    
    private void appendFormStart(StringBuilder sb, String action, String onsubmit, HttpServletRequest httpServletRequest) {
     	sb.append("<form method=\"get\" action=\"");
        sb.append(httpServletRequest.getContextPath());
        sb.append("/");
    	sb.append(action);
        if (onsubmit != null){
            sb.append("\" onsubmit=\"");
            sb.append(onsubmit);
        }
     	sb.append("\">");
    	sb.append("<fieldset>");
    	sb.append("<legend>Validator Input</legend>\n");
    }
 
    private void appendFormEnd(StringBuilder sb, HttpServletRequest httpServletRequest) {
    	sb.append("<p><input type=\"submit\" value=\"Submit\"/></p>");
    	sb.append("</fieldset></form>\n");
    	sb.append("<p>Note: If the new page does not open click on the address bar and press enter</p>\n");
    }
    
    private void appendExampleButton(StringBuilder sb, String page, HttpServletRequest httpServletRequest, String... parameters) {
        sb.append("<p><a href=\"");
        sb.append(httpServletRequest.getContextPath());
        sb.append("/");
    	sb.append(page);
        sb.append("?");
    	sb.append(parameters[0]);
        sb.append("=");
        try {
            sb.append(URLEncoder.encode(parameters[1], "UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            sb.append(parameters[1]);
        }
        for (int i = 2; i < parameters.length; i=i+2){
            sb.append("&");
        	sb.append(parameters[i]);
            sb.append("=");
            try {
                sb.append(URLEncoder.encode(parameters[i+1], "UTF-8"));
            } catch (UnsupportedEncodingException ex) {
                sb.append(parameters[i+1]);
            }
        }
    	sb.append("\"> <button> Example! </button> </a></p>\n");
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
        generateCheckValidateForm(sb);
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

    private void generateCheckValidateForm(StringBuilder sb) {
        sb.append("function " + WsValidationConstants.CHECK_VALIDATE_FORM + " ( form )	{\n");
            // Check a specification selected 
            sb.append("if (form.").append(WsValidationConstants.SPECIFICATION).append(".value == \"\") {\n");
                sb.append("alert( \"Please select the ").append(WsValidationConstants.SPECIFICATION).append(" to use.\" );\n");
                sb.append("form.").append(WsValidationConstants.SPECIFICATION).append(".focus();\n");
                sb.append("return false ;\n");
            sb.append("}\n");
            //Check if URI or text selected. And if text RdfFormat is provided. **
            sb.append("if (form.").append(WsValidationConstants.TEXT).append(".value == \"\") {\n");
                sb.append("if (form.").append(WsValidationConstants.URI).append(".value == \"\"){\n");
                    sb.append("alert(\"Please provided either the ").append(WsValidationConstants.TEXT)
                            .append(" to validate or a ").append(WsValidationConstants.URI).append(" to the text.\" );\n");
                    sb.append("form.").append(WsValidationConstants.TEXT).append(".focus();\n");
                    sb.append("return false ;\n");
                sb.append("} else {\n");
                    sb.append("return true;\n");
                sb.append("}\n");
            sb.append("} else {\n");
                sb.append("if (form.").append(WsValidationConstants.URI).append(".value == \"\"){\n");
                    sb.append("if (form.").append(WsValidationConstants.RDF_FORMAT).append(".value == \"\"){\n");
                        sb.append("alert( \"Please select the ").append(WsValidationConstants.RDF_FORMAT).append(" to use.\" );\n");
                        sb.append("form.rdfFormat").append(WsValidationConstants.RDF_FORMAT).append(".focus();\n");
                        sb.append("return false ;\n");
                    sb.append("} else {\n");
                        sb.append("return true;\n");
                    sb.append("}\n");
                sb.append("}  else {\n");
                    sb.append("alert(\"Validate works on either the ").append(WsValidationConstants.TEXT)
                            .append(" to validate or a ").append(WsValidationConstants.URI)
                            .append(" to the text. Please clear one of the values.\" );\n");
                     sb.append("form.").append(WsValidationConstants.TEXT).append(".focus();\n");
                    sb.append("return false ;\n");
                sb.append("}\n");
            sb.append("}\n");
        sb.append("}\n");         
    }

    private void generateCheckLoadForm(StringBuilder sb) {
        sb.append("function " + WsValidationConstants.CHECK_LOAD_FORM + " ( form )	{\n");
            //Check if URI selected.
            sb.append("if (form.").append(WsValidationConstants.URI).append(".value == \"\"){\n");
                sb.append("alert(\"Please provided the ").append(WsValidationConstants.URI).append(" to validate \" );\n");
                sb.append("form.").append(WsValidationConstants.URI).append(".focus();\n");
                sb.append("return false ;\n");
            sb.append("}\n");
        sb.append("}\n");         
    }
    
    private void appendInput(StringBuilder sb, String label, String value) {     
    	sb.append("<p><label for=\"");
    	sb.append(label);
    	sb.append("\">");
    	sb.append(label);        
    	sb.append("</label>");
    	sb.append("<input type=\"text\" id=\"");
    	sb.append(label);
    	sb.append("\" name=\"");
    	sb.append(label);
    	sb.append("\" style=\"width:80%");
        if (value != null){
        	sb.append("\" value=\"");
            sb.append(value);            
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

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path(WsValidationConstants.STATEMENT_LIST)
    @Override
    public List<StatementBean> getStatementList(@QueryParam(WsValidationConstants.SUBJECT) String subjectString, 
            @QueryParam(WsValidationConstants.PREDICATE) String predicateString, 
            @QueryParam(WsValidationConstants.OBJECT) String objectString, 
            @QueryParam(WsValidationConstants.CONTEXT) List<String> contextStrings) throws VoidValidatorException {
        List<Statement> statements = getStatementListImplementation(subjectString, predicateString, objectString, contextStrings);
        return StatementBean.asBeans(statements);
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path(WsValidationConstants.STATEMENT_LIST)
    public Response getStatementList(@QueryParam(WsValidationConstants.SUBJECT) String subjectString, 
            @QueryParam(WsValidationConstants.PREDICATE) String predicateString, 
            @QueryParam(WsValidationConstants.OBJECT) String objectString, 
            @QueryParam(WsValidationConstants.CONTEXT) List<String> contextStrings,
            @Context HttpServletRequest httpServletRequest) throws VoidValidatorException {
        StringBuilder sb = topAndSide("Validation Service ",  httpServletRequest);
        formStatementList(sb, subjectString, predicateString, objectString, contextStrings, httpServletRequest); 
        if ((subjectString != null && !subjectString.isEmpty()) || 
                (predicateString != null && !predicateString.isEmpty()) || 
                (objectString != null && !objectString.isEmpty()) || 
                (contextStrings != null &&!contextStrings.isEmpty())){
            List<Statement> statements = getStatementListImplementation(subjectString, predicateString, objectString, contextStrings);
            appendStatements(sb, statements, httpServletRequest);
        } else {
            appendExampleButton(sb, WsValidationConstants.STATEMENT_LIST, httpServletRequest, 
                    WsValidationConstants.SUBJECT, ExampleConstants.EXAMPLE_RESOURCE);
        }
        footerAndEnd(sb);
        return Response.ok(sb.toString(), MediaType.TEXT_HTML).build();  
    }
    
    private List<Statement> getStatementListImplementation(String subjectString, String predicateString, String objectString, 
            List<String> contextStrings) throws VoidValidatorException {
        Resource subject = ResourceBean.asResource(subjectString);
        URI predicate = URIBean.asURI(predicateString);
        Value object = ValueBean.asValue(objectString);
        Resource[] contexts = ResourceBean.asResourceArray(contextStrings);
        return rdfInterface.getStatementList(subject, predicate, object, contexts);
    }

    @Override
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path(WsValidationConstants.BY_RESOURCE)
    public List<StatementBean> getByResource(@QueryParam(WsValidationConstants.RESOURCE) String resourceString) 
            throws VoidValidatorException{
        List<Statement> statements = this.getByResourceImplmentation(resourceString);
        return StatementBean.asBeans(statements);
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path(WsValidationConstants.BY_RESOURCE)
    public Response getByResource(@QueryParam(WsValidationConstants.RESOURCE) String resourceString,
            @Context HttpServletRequest httpServletRequest) throws VoidValidatorException{        
        StringBuilder sb = topAndSide("Validation Service ",  httpServletRequest);
        formByResource(sb, resourceString, httpServletRequest); 
        if (resourceString!= null && !resourceString.isEmpty()){
            List<Statement> statements = this.getByResourceImplmentation(resourceString);
            appendStatements(sb, statements, httpServletRequest);
        } else {
            appendExampleButton(sb, WsValidationConstants.BY_RESOURCE, httpServletRequest, 
                    WsValidationConstants.RESOURCE, ExampleConstants.EXAMPLE_RESOURCE);
        }
        footerAndEnd(sb);
        return Response.ok(sb.toString(), MediaType.TEXT_HTML).build();  
    }

    public List<Statement> getByResourceImplmentation(@QueryParam(WsValidationConstants.RESOURCE) String resourceString) 
            throws VoidValidatorException{
        if (resourceString == null){
            throw new VoidValidatorException ("Missing " + WsValidationConstants.RESOURCE + " parameter!");
        }
        Resource resource = ResourceBean.asResource(resourceString);
        return rdfInterface.getStatementList(resource);
    }
    
    @Override
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path(WsValidationConstants.LOAD_URI)
    public URIBean loadURI(@QueryParam(WsValidationConstants.URI) String address, 
        @QueryParam(WsValidationConstants.RDF_FORMAT) String formatName)throws VoidValidatorException {
        URI result = this.loadURIImplementation(address, formatName);
        return URIBean.asBean(result);
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path(WsValidationConstants.LOAD_URI)
    public Response loadURI(@QueryParam(WsValidationConstants.URI) String address, 
            @QueryParam(WsValidationConstants.RDF_FORMAT) String formatName,
            @Context HttpServletRequest httpServletRequest)throws VoidValidatorException {
        StringBuilder sb = topAndSide("Validation Service ",  httpServletRequest);
        formLoadUri(sb, address, formatName, httpServletRequest); 
        if (address!= null && !address.isEmpty()){
            URI context = this.loadURIImplementation(address, formatName);
            sb.append("Succfully loaded the following statements from ");
            sb.append(address);
            sb.append("<br/>\n");
            ArrayList<String> contexts = new ArrayList<String>();
            contexts.add(context.stringValue());
            List<Statement> statements = getStatementListImplementation(null, null, null, contexts);
            appendStatements(sb, statements, httpServletRequest);
        }
        footerAndEnd(sb);
        return Response.ok(sb.toString(), MediaType.TEXT_HTML).build();  
    }

    private URI loadURIImplementation(String address, String formatName)throws VoidValidatorException {
        if (address == null){
            throw new VoidValidatorException("Missing " + WsValidationConstants.URI + " parameter!");
        }
        RDFFormat format;
        if (formatName == null || formatName.isEmpty()){
            format = null;
        } else {
            format = RDFFormat.valueOf(formatName);            
            if (format == null){
                throw new VoidValidatorException("No format known for " + formatName);
            }
        }
        return rdfInterface.loadURI(address, format);
    }

    @Override
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path(WsValidationConstants.SPARQL)
    public String runSparqlQuery(@QueryParam(WsValidationConstants.QUERY)String query, 
            @QueryParam(WsValidationConstants.FORMAT)String formatName) throws VoidValidatorException{
        TupleQueryResultFormat format = null;
        for (TupleQueryResultFormat check:TupleQueryResultFormat.values()){
            if (check.getName().equalsIgnoreCase(formatName)){
                format = check;
            }
        }
        if (format == null){
            throw new VoidValidatorException("No format known for " + formatName);
        }
        return rdfInterface.runSparqlQuery(query, format);
    }
    
   private void appendStatements(StringBuilder sb, List<Statement> statements, HttpServletRequest httpServletRequest) {
        sb.append("<hr/>\n");
        sb.append("<table border=\"1\">\n");
        sb.append("<tr>");
        sb.append("<th>Subject</th>");
        sb.append("<th>Predicate</th>");
        sb.append("<th>Object</th>");
        sb.append("<th>Context</th>");
        sb.append("</tr>\n");
        for (Statement statement:statements){
            sb.append("<tr>");
            appendValueCell(sb, statement.getSubject(), false, httpServletRequest);
            appendValueCell(sb, statement.getPredicate(), true, httpServletRequest);
            appendValueCell(sb, statement.getObject(), false, httpServletRequest);
            appendValueCell(sb, statement.getContext(), true, httpServletRequest);
            sb.append("</tr>");
        }
        sb.append("</table>\n");
    }
    
    private void appendValueCell(StringBuilder sb, Value value, boolean direct, HttpServletRequest httpServletRequest) {
        sb.append("<td>");
        if (value instanceof URI){
            sb.append("<a href=\"");
            if (!direct){
                sb.append(httpServletRequest.getContextPath());
                sb.append("/");
                sb.append(WsValidationConstants.BY_RESOURCE);
                sb.append("?");
                sb.append(WsValidationConstants.RESOURCE);
                sb.append("=");
            }
            try {
                sb.append(URLEncoder.encode(value.stringValue(), "UTF-8"));
            } catch (UnsupportedEncodingException ex) {
                sb.append(value.stringValue());
            }
            sb.append("\"</a>&lt;");
            sb.append(value.stringValue());
            sb.append("&gt;");
        } else {
            sb.append(value.toString());
        }
        sb.append("</td>\n");
    }

    protected abstract StringBuilder topAndSide(String header, HttpServletRequest httpServletRequest);
    
    protected abstract void footerAndEnd(StringBuilder sb);
    
   /**
     * Adds an item to the SideBar for this service
     */
    protected abstract void addSideBarItem(StringBuilder sb, String page, String name, HttpServletRequest httpServletRequest);

 

 
}


