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
import java.util.Iterator;
import java.util.List;
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
import uk.ac.manchester.cs.openphacts.valdator.rdftools.ExampleConstants;
import uk.ac.manchester.cs.openphacts.valdator.rdftools.RdfFactory;
import uk.ac.manchester.cs.openphacts.valdator.rdftools.RdfInterface;
import uk.ac.manchester.cs.openphacts.valdator.rdftools.RdfReader;
import uk.ac.manchester.cs.openphacts.valdator.rdftools.VoidValidatorException;
import uk.ac.manchester.cs.openphacts.valdator.ws.WsValidationConstants;
import uk.ac.manchester.cs.openphacts.validator.RdfValidator;
import uk.ac.manchester.cs.openphacts.validator.Validator;

    
/**
 *
 * @author Christian
 */
public class WsValidatorServer implements HtmlWSInterface{
        
    static final Logger logger = Logger.getLogger(WsValidatorServer.class);
    private static final int DESCRIPTION_WIDTH = 100;
    private static final String DESCRIPTION_FIELD = "Description";
    private static final String NO_ON_SUBMIT = null;
    
    private RdfInterface rdfInterface;
    private FrameInterface frame;
    private Validator validator;
   
    public WsValidatorServer() {
        logger.info("Validator created but not yet setup");
     }

    public void setUp(RdfInterface rdfInterface, Validator validator, FrameInterface frame) throws VoidValidatorException{
        this.frame = frame;
        this.rdfInterface = rdfInterface;
        this.validator = validator;
        if (this.frame == null){
            throw new VoidValidatorException("Illegal call to setup with null frame");
        }
        if (this.rdfInterface == null){
            throw new VoidValidatorException("Illegal call to setup with null rdfInterface");
        }
        if (this.validator == null){
            throw new VoidValidatorException("Illegal call to setup with null validator");
        }
        logger.info("Validator Server setup");
    }
    
//Super class calls    
    protected void addValidatorSideBar(StringBuilder sb, HttpServletRequest httpServletRequest) {
        sb.append("<div class=\"menugroup\">OPS Validation Service</div>");
        frame.addSideBarItem(sb, WsValidationConstants.VALIDATE_HOME, "Home", httpServletRequest);
        frame.addSideBarItem(sb, WsValidationConstants.VALIDATE,WsValidationConstants.VALIDATE, httpServletRequest);
        frame.addSideBarItem(sb, WsValidationConstants.STATEMENT_LIST, WsValidationConstants.STATEMENT_LIST,  httpServletRequest);
        frame.addSideBarItem(sb, WsValidationConstants.BY_RESOURCE, WsValidationConstants.BY_RESOURCE,  httpServletRequest);
        frame.addSideBarItem(sb, WsValidationConstants.SPARQL, WsValidationConstants.SPARQL, httpServletRequest);
        frame.addSideBarItem(sb, WsValidationConstants.LOAD_URI, WsValidationConstants.LOAD_URI, httpServletRequest);
    }
    
//Public calls 
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path(WsValidationConstants.VALIDATE_HOME)
    @Override
    public Response validateHome(@Context HttpServletRequest httpServletRequest) throws VoidValidatorException {
        Thread tread = Thread.currentThread();
        logger.info(tread);
        if (logger.isDebugEnabled()){
            logger.debug("ValidateHome called");
        }
        if (errorState()){
            return errorReport();
        }
        StringBuilder sb = frame.topAndSide("Validation Service ",  httpServletRequest);
        sb.append("<h1> Welcome to the OpenPhacts Validation Service.</h1>");
        sb.append("<p>The form below gives an example of how to use the Validation service.</p>");
        sb.append("<p>Select another service from the side.</p>");
        sb.append("<p>To Try a different ontology please contact Christian.</p>");
        sb.append("<hr/>");
        formValidation(sb, null, null, null, null, true, httpServletRequest);                     
        frame.footerAndEnd(sb);
        if (logger.isDebugEnabled()){
            logger.debug("ValidateHome returning");
        }
        return Response.ok(sb.toString(), MediaType.TEXT_HTML).build();
    }
    
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path(WsValidationConstants.STATEMENT_LIST)
    @Override
    public List<StatementBean> getStatementList(@QueryParam(WsValidationConstants.SUBJECT) String subjectString, 
            @QueryParam(WsValidationConstants.PREDICATE) String predicateString, 
            @QueryParam(WsValidationConstants.OBJECT) String objectString, 
            @QueryParam(WsValidationConstants.CONTEXT) List<String> contextStrings) throws VoidValidatorException {
        if (logger.isDebugEnabled()){
            logger.debug("json getStatementList called");
        }
        checkErrorState();
        List<Statement> statements = getStatementListImplementation(subjectString, predicateString, objectString, contextStrings);
        if (logger.isDebugEnabled()){
            logger.debug("json getStatementList returning");
        }
        return StatementBean.asBeans(statements);
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path(WsValidationConstants.STATEMENT_LIST)
    @Override
    public Response getStatementList(@QueryParam(WsValidationConstants.SUBJECT) String subjectString, 
            @QueryParam(WsValidationConstants.PREDICATE) String predicateString, 
            @QueryParam(WsValidationConstants.OBJECT) String objectString, 
            @QueryParam(WsValidationConstants.CONTEXT) List<String> contextStrings,
            @Context HttpServletRequest httpServletRequest) throws VoidValidatorException {
        if (logger.isDebugEnabled()){
            logger.debug("getStatementList called");
        }
        if (errorState()){
            return errorReport();
        }
        if (contextStrings != null){
            Iterator<String> iterator = contextStrings.iterator();
            while (iterator.hasNext()){
                String check = iterator.next();
                if (check == null || check.isEmpty()){
                    iterator.remove();
                }
            }
        }
        StringBuilder sb = frame.topAndSide("Validation Service ",  httpServletRequest);
        formStatementList(sb, subjectString, predicateString, objectString, contextStrings, httpServletRequest); 
        if ((subjectString != null && !subjectString.isEmpty()) || 
                (predicateString != null && !predicateString.isEmpty()) || 
                (objectString != null && !objectString.isEmpty()) || 
                (contextStrings != null && !contextStrings.isEmpty())){
            List<Statement> statements = getStatementListImplementation(subjectString, predicateString, objectString, contextStrings);
            appendStatements(sb, statements, httpServletRequest);
        } else {
            appendExampleButton(sb, WsValidationConstants.STATEMENT_LIST, httpServletRequest, 
                    WsValidationConstants.SUBJECT, frame.getExampleResource());
        }
        frame.footerAndEnd(sb);
        if (logger.isDebugEnabled()){
            logger.debug("getStatementList returning");
        }
        return Response.ok(sb.toString(), MediaType.TEXT_HTML).build();  
    }

    @Override
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path(WsValidationConstants.BY_RESOURCE)
    public List<StatementBean> getByResource(@QueryParam(WsValidationConstants.RESOURCE) String resourceString) 
            throws VoidValidatorException{
        if (logger.isDebugEnabled()){
            logger.debug("json getByResource called");
        }
        checkErrorState();
        List<Statement> statements = this.getByResourceImplmentation(resourceString);
        if (logger.isDebugEnabled()){
            logger.debug("json getByResource returning");
        }
        return StatementBean.asBeans(statements);
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path(WsValidationConstants.BY_RESOURCE)
    @Override
    public Response getByResource(@QueryParam(WsValidationConstants.RESOURCE) String resourceString,
            @Context HttpServletRequest httpServletRequest) throws VoidValidatorException{        
        if (logger.isDebugEnabled()){
            logger.debug("getByResource called");
        }
        if (errorState()){
            return errorReport();
        }
        StringBuilder sb = frame.topAndSide("Validation Service ",  httpServletRequest);
        formByResource(sb, resourceString, httpServletRequest); 
        if (resourceString!= null && !resourceString.isEmpty()){
            List<Statement> statements = this.getByResourceImplmentation(resourceString);
            appendStatements(sb, statements, httpServletRequest);
        } else {
            appendExampleButton(sb, WsValidationConstants.BY_RESOURCE, httpServletRequest, 
                    WsValidationConstants.RESOURCE, frame.getExampleResource());
        }
        frame.footerAndEnd(sb);
        if (logger.isDebugEnabled()){
            logger.debug("getByResource returning");
        }
        return Response.ok(sb.toString(), MediaType.TEXT_HTML).build();  
    }    
    
    @Override
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path(WsValidationConstants.LOAD_URI)
    public URIBean loadURI(@QueryParam(WsValidationConstants.URI) String address, 
        @QueryParam(WsValidationConstants.RDF_FORMAT) String formatName)throws VoidValidatorException {
        if (logger.isDebugEnabled()){
            logger.debug("json loadURI called");
        }
        checkErrorState();
        URI result = this.loadURIImplementation(address, formatName);
        if (logger.isDebugEnabled()){
            logger.debug("json loadURI returning");
        }
        return URIBean.asBean(result);
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path(WsValidationConstants.LOAD_URI)
    @Override
    public Response loadURI(@QueryParam(WsValidationConstants.URI) String address, 
            @QueryParam(WsValidationConstants.RDF_FORMAT) String formatName,
            @Context HttpServletRequest httpServletRequest)throws VoidValidatorException {
        if (logger.isDebugEnabled()){
            logger.debug("loadURI called");
        }
        if (errorState()){
            return errorReport();
        }
        StringBuilder sb = frame.topAndSide("Validation Service ",  httpServletRequest);
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
        } else {
            appendExampleButton(sb, WsValidationConstants.LOAD_URI, httpServletRequest, 
                   WsValidationConstants.URI, frame.getExampleURI());
        }
        frame.footerAndEnd(sb);
        if (logger.isDebugEnabled()){
            logger.debug("loadURI returning");
        }
        return Response.ok(sb.toString(), MediaType.TEXT_HTML).build();  
    }

    @Override
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path(WsValidationConstants.SPARQL)
    public String runSparqlQuery(@QueryParam(WsValidationConstants.QUERY)String query, 
            @QueryParam(WsValidationConstants.FORMAT)String formatName) throws VoidValidatorException{
        if (logger.isDebugEnabled()){
            logger.debug("String runSparqlQuery called");
        }
        checkErrorState();
        String result = this.runSparqlQueryImplmentation(query, formatName);
        if (logger.isDebugEnabled()){
            logger.debug("String runSparqlQuery retruning");
        }
        return result;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path(WsValidationConstants.SPARQL)
    @Override
    public Response runSparqlQuery(@QueryParam(WsValidationConstants.QUERY)String query, 
            @QueryParam(WsValidationConstants.FORMAT)String formatName,
            @Context HttpServletRequest httpServletRequest)throws VoidValidatorException {
        if (logger.isDebugEnabled()){
            logger.debug("runSparqlQuery called");
        }
        if (errorState()){
            return errorReport();
        }
        logger.info("runSparqlQuery called query = " + query + " formatName = " + formatName);
        StringBuilder sb = frame.topAndSide("SPARQL Service ",  httpServletRequest);
        formSparql(sb, query, formatName, httpServletRequest); 
        if (query != null && !query.isEmpty() && formatName != null && !formatName.isEmpty()){
            logger.info("running Sparql Query");
            String result = this.runSparqlQueryImplmentation(query, formatName);
            logger.info("run Sparql Query");
            this.generateTextarea(sb, "Query Result", result);
            logger.info("generated text area");
         } else {
            appendExampleButton(sb, WsValidationConstants.SPARQL, httpServletRequest, 
                    WsValidationConstants.FORMAT, ExampleConstants.EXAMPLE_OUTPUT_FORMAT,
                    WsValidationConstants.QUERY, frame.getExampleQuery());
         }
        frame.footerAndEnd(sb);
        logger.info("returning");
        if (logger.isDebugEnabled()){
            logger.debug("runSparqlQuery returning");
        }
        return Response.ok(sb.toString(), MediaType.TEXT_HTML).build();  
    }
 
    @Override
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path(WsValidationConstants.VALIDATE)
    public String validate(@QueryParam(WsValidationConstants.TEXT) String text, 
            @QueryParam(WsValidationConstants.URI) String uri, 
            @QueryParam(WsValidationConstants.RDF_FORMAT) String rdfFormat,
            @QueryParam(WsValidationConstants.SPECIFICATION) String specification,
            @QueryParam(WsValidationConstants.INCLUDE_WARNINGS) Boolean includeWarning) throws VoidValidatorException {
        if (logger.isDebugEnabled()){
            logger.debug("json validate called");
        }
        checkErrorState();
        return this.validateImplmentation(text, uri, rdfFormat, specification, includeWarning);
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path(WsValidationConstants.VALIDATE)
    @Override
    public Response validate(@QueryParam(WsValidationConstants.TEXT) String text, 
            @QueryParam(WsValidationConstants.URI) String uri, 
            @QueryParam(WsValidationConstants.RDF_FORMAT) String rdfFormat,
            @QueryParam(WsValidationConstants.SPECIFICATION) String specification,
            @QueryParam(WsValidationConstants.INCLUDE_WARNINGS) Boolean includeWarning,
            @Context HttpServletRequest httpServletRequest) throws VoidValidatorException {        
        if (logger.isDebugEnabled()){
            logger.debug("validate called");
        }
        if (errorState()){
            return errorReport();
        }
        StringBuilder sb = frame.topAndSide("Validation Service", httpServletRequest);
        boolean validated = getAndShowValidationResult(sb, text, uri, rdfFormat, specification, includeWarning);
        formValidation(sb, rdfFormat, text, uri, specification, includeWarning, httpServletRequest); 
        if (!validated){
            appendButton(sb, "URI example!", WsValidationConstants.VALIDATE, httpServletRequest, 
                    WsValidationConstants.URI, frame.getExampleURI(),
                    WsValidationConstants.SPECIFICATION, frame.getExampleSpecificationName());          
            appendButton(sb, "Text Example (witrh an error)", WsValidationConstants.VALIDATE, httpServletRequest, 
                    WsValidationConstants.TEXT, frame.getExampleText(),
                    WsValidationConstants.RDF_FORMAT,  RDFFormat.TURTLE.getName(), 
                    WsValidationConstants.SPECIFICATION, frame.getExampleSpecificationName());          
        }
        frame.footerAndEnd(sb);
        if (logger.isDebugEnabled()){
            logger.debug("validate returning");
        }
        return Response.ok(sb.toString(), MediaType.TEXT_HTML).build();        
    }

    @POST
    @Produces(MediaType.TEXT_HTML)
    @Path(WsValidationConstants.VALIDATE)
    public Response validatePost(@QueryParam(WsValidationConstants.TEXT) String text, 
            @QueryParam(WsValidationConstants.URI) String uri,
            @QueryParam(WsValidationConstants.RDF_FORMAT) String rdfFormat,  
            @QueryParam(WsValidationConstants.SPECIFICATION) String specification,
            @QueryParam(WsValidationConstants.INCLUDE_WARNINGS) Boolean includeWarning,
            @Context HttpServletRequest httpServletRequest) throws VoidValidatorException {        
        if (logger.isDebugEnabled()){
            logger.debug("validate Post called");
        }
        if (errorState()){
            return errorReport();
        }
        return this.validate(text, uri, rdfFormat, specification, includeWarning, httpServletRequest);
    }
    
// Implementations
    private List<Statement> getStatementListImplementation(String subjectString, String predicateString, String objectString, 
            List<String> contextStrings) throws VoidValidatorException {
        Resource subject = ResourceBean.asResource(subjectString);
        URI predicate = URIBean.asURI(predicateString);
        Value object = ValueBean.asValue(objectString);
        Resource[] contexts = ResourceBean.asResourceArray(contextStrings);
        if (subject == null && predicate == null && object == null && (contexts == null || contexts.length == 0)){
            return new ArrayList<Statement>();
        } else {
            List<Statement> result = rdfInterface.getStatementList(subject, predicate, object, contexts);
            rdfInterface.close();
            return result;
        }
    }
    
    private List<Statement> getByResourceImplmentation(@QueryParam(WsValidationConstants.RESOURCE) String resourceString) 
            throws VoidValidatorException{
        if (resourceString == null){
            throw new VoidValidatorException ("Missing " + WsValidationConstants.RESOURCE + " parameter!");
        }
        Resource resource = ResourceBean.asResource(resourceString);
        List<Statement> result = rdfInterface.getStatementList(resource);
        rdfInterface.close();
        return result;
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
        URI result = rdfInterface.loadURI(address, format);
        rdfInterface.close();
        return result;
    }

    private String runSparqlQueryImplmentation(String query, String formatName) throws VoidValidatorException{
        TupleQueryResultFormat format = null;
        for (TupleQueryResultFormat check:TupleQueryResultFormat.values()){
            if (check.getName().equalsIgnoreCase(formatName)){
                format = check;
            }
        }
        if (format == null){
            throw new VoidValidatorException("No format known for " + formatName);
        }
        String result = rdfInterface.runSparqlQuery(query, format);
        rdfInterface.close();
        return result;
    }
    
    private String validateImplmentation(String text, String uri, String rdfFormat, String specification, 
            Boolean includeWarning) throws VoidValidatorException{
        if (text == null || text.isEmpty()){
            if (uri == null || uri.isEmpty()){
                throw new VoidValidatorException("");
            } else {
                return validator.validateUri(uri, rdfFormat, specification, includeWarning);
            }
        } else {
            if (uri == null || uri.isEmpty()){
                return validator.validateText(text, rdfFormat, specification, includeWarning);
            } else {
                throw new VoidValidatorException("");
            }   
        }
    }
    
//Forms
      private void formValidation(StringBuilder sb, String format, String text, String uri, String specification, 
            Boolean includeWarnings, HttpServletRequest httpServletRequest) throws VoidValidatorException {
        String scriptName = insertCheckValidateScript(sb);
     	generateFormStart(sb, WsValidationConstants.VALIDATE, scriptName , httpServletRequest);
        generateRDFFormat(sb, format);
        generateIncludeWarnings(sb, includeWarnings);
        generateTextarea(sb, WsValidationConstants.TEXT, text);
        generateURIInput(sb, uri);
    	generateSpecificationsSelector(sb, specification);
    	generateFormEnd(sb, httpServletRequest);
        
   }
    
   private void formByResource(StringBuilder sb, String resourceString, HttpServletRequest httpServletRequest) {
        String scriptName = insertCheckByResourceScript(sb);
      	generateFormStart(sb, WsValidationConstants.STATEMENT_LIST,  scriptName, httpServletRequest);
        generateInput(sb, WsValidationConstants.RESOURCE, resourceString);
     	generateFormEnd(sb, httpServletRequest);
   }

   private void formStatementList(StringBuilder sb, String subjectString, String predicateString, String objectString, List<String> contextStrings, HttpServletRequest httpServletRequest) {
        String scriptName = insertCheckStatementListScript(sb);
        generateFormStart(sb, WsValidationConstants.STATEMENT_LIST, scriptName, httpServletRequest);
        generateInput(sb, WsValidationConstants.SUBJECT, subjectString);
        generateInput(sb, WsValidationConstants.PREDICATE, predicateString);
        generateInput(sb, WsValidationConstants.OBJECT, objectString);
        generateInput(sb, WsValidationConstants.CONTEXT, "");
    	generateFormEnd(sb, httpServletRequest);
   }

    private void formLoadUri(StringBuilder sb, String uri, String formatName, HttpServletRequest httpServletRequest) {
        String scriptName = insertCheckLoadScript(sb);
     	generateFormStart(sb, WsValidationConstants.LOAD_URI, scriptName, httpServletRequest);
        generateRDFFormat(sb, formatName);
        generateURIInput(sb, uri);
    	generateFormEnd(sb, httpServletRequest);      
    }

    private void formSparql(StringBuilder sb, String query, String formatName, HttpServletRequest httpServletRequest) {
        String scriptName = insertSparqlScript(sb);
     	generateFormStart(sb, WsValidationConstants.SPARQL, scriptName, httpServletRequest);
        generateOutputFormat(sb, formatName);
        generateTextarea(sb, WsValidationConstants.QUERY, query);
    	generateFormEnd(sb, httpServletRequest);
    }

//None form things  
    
    private void appendExampleButton(StringBuilder sb, String page, HttpServletRequest httpServletRequest, String... parameters) {
        this.appendButton(sb, "Example!", page, httpServletRequest, parameters);
    }

    private void appendButton(StringBuilder sb, String buttonTitle, String page, HttpServletRequest httpServletRequest, String... parameters) {
        sb.append("<a href=\"");
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
    	sb.append("\"> <button>").append(buttonTitle).append("</button> </a>\n");
    }
    
//Form parts    
    private void generateSpecificationsSelector(StringBuilder sb, String specification) throws VoidValidatorException {
		Set<String> names = MetaDataSpecification.getSpecificationNames();
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
            MetaDataSpecification specs = MetaDataSpecification.specificationByName(name);
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
           MetaDataSpecification specs = MetaDataSpecification.specificationByName(specification); 
            sb.append(specs.getDescription());
        }
        sb.append("</textarea>\n");
   }

    private void generateFormStart(StringBuilder sb, String action, String onsubmit, HttpServletRequest httpServletRequest) {
     	sb.append("<form method=\"get\" accept-charset=\"UTF-8\" action=\"");
        sb.append(httpServletRequest.getContextPath());
        sb.append("/");
    	sb.append(action);
        if (onsubmit != null){
            sb.append("\" onsubmit=\"return ").append(onsubmit).append("(this);");
        }
     	sb.append("\">");
    	sb.append("<fieldset>");
    	sb.append("<legend>").append(action).append(" input</legend>\n");
    } 
 
    private void generateFormEnd(StringBuilder sb, HttpServletRequest httpServletRequest) {
    	sb.append("<p><input type=\"submit\" value=\"Submit\"/></p>");
    	sb.append("</fieldset></form>\n");
    	sb.append("<p>Note: If the new page does not open click on the address bar and press enter</p>\n");
    }
 
    private void generateURIInput(StringBuilder sb, String value) { 
        generateInput(sb, WsValidationConstants.URI, value);
        sb.append("<p>").append(WsValidationConstants.URI).append(" must include the schema such as (http://).<br/>");
        sb.append(WsValidationConstants.URI).append(" may be in gz format.<br/>");
        sb.append(WsValidationConstants.URI).append(" can be password protected if password is know to the system.<br/>");
    }    
    
    private void generateInput(StringBuilder sb, String label, String value) {     
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
    
    private void generateRDFFormat(StringBuilder sb, String format) {
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
        } 
        sb.append("<option SELECTED value=\"\">Pick RdfFormat based on file extension</option>");
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

    private void generateOutputFormat(StringBuilder sb, String formatName) {
        TupleQueryResultFormat format = null;
        if (formatName != null && !formatName.isEmpty()){
            for (TupleQueryResultFormat check:TupleQueryResultFormat.values()){
                if (check.getName().equals(formatName)){
                    format = check;
                }
            }
        } 
        sb.append("<p>");
    	sb.append(WsValidationConstants.FORMAT);
        sb.append("<select name=\"");
    	sb.append(WsValidationConstants.FORMAT);
    	sb.append("\">");
        int maxDescription = 0;
        sb.append("<option SELECTED value=\"\">Select a TupleQueryResultFormat.</option>");
        for (TupleQueryResultFormat aFormat:TupleQueryResultFormat.values()){
			sb.append("<option value=\"");
			sb.append(aFormat.getName());
            if (aFormat.equals(format)){
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

    private void generateIncludeWarnings(StringBuilder sb, Boolean includeWarnings) {
        sb.append("<input type=\"checkbox\" name=\"");
    	sb.append(WsValidationConstants.INCLUDE_WARNINGS);
        sb.append("\" value=\"true\"");
        if (includeWarnings == null || includeWarnings){
            sb.append(" checked");
        } 
        sb.append(" >Include warnings.<br>");
    }

    private void generateTextarea(StringBuilder sb, String fieldName, String text) {
        sb.append("<p>").append(fieldName);
    	sb.append("<br/><textarea rows=\"15\" name=\"").append(fieldName)
                .append("\" style=\"width:100%; background-color: #EEEEFF;\">");
        if (text != null){
            sb.append(text);
        }
        sb.append("</textarea></p>\n");
    }

//Result viewers      
    
    private boolean getAndShowValidationResult(StringBuilder sb, String text, String uri, String format, String specification, 
            Boolean includeWarnings) throws VoidValidatorException  { 
        boolean ok = checkRdfFormat(sb, format) && checkSpecification(sb, specification);
        if (text == null || text.isEmpty()){
            if (uri == null || uri.isEmpty()){
                //both empty is ok nothing to show
                return false;
            } else {
                //no text but uri supplied format may be null
            }
        } else {
            if (uri == null || uri.isEmpty()){
                //text but no uri  so must check rdf format
                if (format == null || format.isEmpty()){
                    sb.append("You must supply an ");
                    sb.append(WsValidationConstants.RDF_FORMAT);
                    sb.append(" parameter when using a ");
                    sb.append(WsValidationConstants.TEXT);
                    sb.append(" parameter.<br>\n");
                    return false; 
                }
            } else {
                //both oops
                sb.append("Please clear either the ");
                sb.append(WsValidationConstants.TEXT);
                sb.append(" or the ");
                sb.append(WsValidationConstants.URI);
                sb.append("parameter! <br>");   
                return false;
            }
        }
        if (specification == null || specification.isEmpty()){
            sb.append(WsValidationConstants.SPECIFICATION);
            sb.append(" parameter missing!<br> Please select one from the dropdown list.<br>");
            return false;
        }
        if (!ok){
            return false;
        }
        String results;
        if (text == null || text.isEmpty()){
           results =  validator.validateUri(uri, format, specification, includeWarnings);
        } else {
            results =  validator.validateText(text, format, specification, includeWarnings);
        }
        this.showValidationResult(sb, results);
        return true;
    }  

    private boolean checkRdfFormat(StringBuilder sb, String format) throws VoidValidatorException  {   
        if (format == null || format.isEmpty()){
            return true;
        }
        for (RDFFormat check:RDFFormat.values()){
            if (check.getName().equals(format)){
                return true;
            }
        }
        sb.append(WsValidationConstants.RDF_FORMAT);
        sb.append(" ");
        sb.append(format);
        sb.append(" is not known. Please select one from the dropdown list.<br>");
        return false;
    }       

    private boolean checkSpecification(StringBuilder sb, String specification) throws VoidValidatorException  {   
        if (specification != null && !specification.isEmpty()){
            return true;
        }
        try {
            MetaDataSpecification specs = MetaDataSpecification.specificationByName(specification);
            if (specs == null){
                sb.append("Sorry no ");
                sb.append(WsValidationConstants.SPECIFICATION);
                sb.append(" with name ");
                sb.append(specification);
                sb.append(" known!<br>");  
                return false;
            } else {
                return true;
            }
        } catch (Exception ex){
            sb.append("Error getting  ");
            sb.append(WsValidationConstants.SPECIFICATION);
            sb.append(" with name ");
            sb.append(specification);
            sb.append("<br>");            
            sb.append(ex.getMessage());
            return false;
        }
    }

    private void showValidationResult(StringBuilder sb, String results) {
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

    private void appendStatements(StringBuilder sb, List<Statement> statements, HttpServletRequest httpServletRequest) {
        sb.append("<hr/>\n");
        sb.append("<table border=\"1\">\n");
        sb.append("<tr>");
        sb.append("<th>Subject</th>");
        sb.append("<th>Predicate</th>");
        sb.append("<th>Object</th>");
        sb.append("<th>Context/ Source</th>");
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
                try {
                    sb.append(URLEncoder.encode(value.stringValue(), "UTF-8"));
                } catch (UnsupportedEncodingException ex) {
                    sb.append(value.stringValue());
                }
            } else {
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

    private void showException(StringBuilder sb, Exception ex) throws VoidValidatorException {
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
    
    
//Javascripts    
    private void insertSpecificationsScript(StringBuilder sb) throws VoidValidatorException {
		Set<String> names = MetaDataSpecification.getSpecificationNames();
        sb.append(   "function populateData(sel){\n");
        sb.append(      "var form = sel.form,\n");
        sb.append(         "value = sel.options[sel.selectedIndex].value;\n");
        sb.append(      "switch(value){\n");
		for (String name : names) {
            MetaDataSpecification specs = MetaDataSpecification.specificationByName(name);
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

    private String insertCheckValidateScript(StringBuilder sb) throws VoidValidatorException {
        sb.append("<script>\n");
        insertSpecificationsScript(sb);
        String name = "checkValidateForm";
        sb.append("function ").append(name).append(" ( form )	{\n");
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
        sb.append("</script>\n");
        return name;
    }

    private String insertCheckLoadScript(StringBuilder sb) {
        sb.append("<script>\n");
        String CHECK_LOAD_FORM = "checkLoadForm";
        sb.append("function " + CHECK_LOAD_FORM + " ( form )	{\n");
            //Check if URI selected.
            sb.append("if (form.").append(WsValidationConstants.URI).append(".value == \"\"){\n");
                sb.append("alert(\"Please provided the ").append(WsValidationConstants.URI).append(" to validate \" );\n");
                sb.append("form.").append(WsValidationConstants.URI).append(".focus();\n");
                sb.append("return false ;\n");
            sb.append("}\n");
        sb.append("}\n");  
        sb.append("</script>\n");
        return CHECK_LOAD_FORM;
    }

    private String insertSparqlScript(StringBuilder sb) {
        sb.append("<script>\n");
        String scriptName = "checkSparqlForm";
        sb.append("function " + scriptName + " ( form )	{\n");
            //Check if Query provided.
            sb.append("if (form.").append(WsValidationConstants.QUERY).append(".value == \"\"){\n");
                sb.append("alert(\"Please provided the ").append(WsValidationConstants.QUERY).append(" to run \" );\n");
                sb.append("form.").append(WsValidationConstants.QUERY).append(".focus();\n");
                sb.append("return false ;\n");
            sb.append("}\n");
            //Check if Query provided.
            sb.append("if (form.").append(WsValidationConstants.FORMAT).append(".value == \"\"){\n");
                sb.append("alert(\"Please specify the output ").append(WsValidationConstants.FORMAT).append("\" );\n");
                sb.append("form.").append(WsValidationConstants.FORMAT).append(".focus();\n");
                sb.append("return false ;\n");
            sb.append("}\n");
        sb.append("}\n");  
        sb.append("</script>\n");
        return scriptName;
    }

    private String insertCheckStatementListScript(StringBuilder sb) {
        sb.append("<script>\n");
        String scriptName = "checkStatementListForm";
        sb.append("function " + scriptName + " ( form )	{\n");
            //Check if Subject provided.
            sb.append("if (form.").append(WsValidationConstants.SUBJECT).append(".value != \"\"){\n");
                sb.append("return true ;\n");
            sb.append("}\n");
            //Check if PREDICAT provided.
            sb.append("if (form.").append(WsValidationConstants.PREDICATE).append(".value != \"\"){\n");
                sb.append("return true ;\n");
            sb.append("}\n");
            //Check if OBJECT provided.
            sb.append("if (form.").append(WsValidationConstants.OBJECT).append(".value != \"\"){\n");
                sb.append("return true ;\n");
            sb.append("}\n");
            //Check if Subject provided.
            sb.append("if (form.").append(WsValidationConstants.CONTEXT).append(".value != \"\"){\n");
                sb.append("return true ;\n");
            sb.append("}\n");
            sb.append("alert(\"Please provided at least one selection criteria!\" );\n");
            sb.append("form.").append(WsValidationConstants.SUBJECT).append(".focus();\n");
            sb.append("return false ;\n");
        sb.append("}\n");  
        sb.append("</script>\n");
        return scriptName;
    }

    private String insertCheckByResourceScript(StringBuilder sb) {
        sb.append("<script>\n");
        String scriptName = "checkLoadForm";
        sb.append("function " + scriptName + " ( form )	{\n");
            //Check if URI selected.
            sb.append("if (form.").append(WsValidationConstants.RESOURCE).append(".value == \"\"){\n");
                sb.append("alert(\"Please provided the ").append(WsValidationConstants.RESOURCE).append(" to get data for! \" );\n");
                sb.append("form.").append(WsValidationConstants.RESOURCE).append(".focus();\n");
                sb.append("return false ;\n");
            sb.append("}\n");
        sb.append("}\n");  
        sb.append("</script>\n");
        return scriptName;
    }

    private boolean errorState() {
        if (rdfInterface == null){
            return true;
        }
        if (frame == null){
            return true;
        }
        return false;
    }
 
    private Response errorReport() {
        return Response.serverError().build();
    }

    private void checkErrorState() throws VoidValidatorException {
        if (errorState()){
            if (rdfInterface == null){
                logger.warn("Call failed due to no rdfInterface Set");
            }
            if (frame == null){
                logger.warn("Call failed due to no frame Set");
            }
            throw new VoidValidatorException("Service not intitalized correctly");
        }
    }

 }


