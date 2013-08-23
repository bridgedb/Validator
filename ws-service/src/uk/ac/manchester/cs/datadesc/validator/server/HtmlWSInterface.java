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
package uk.ac.manchester.cs.datadesc.validator.server;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import uk.ac.manchester.cs.datadesc.validator.rdftools.VoidValidatorException;
import uk.ac.manchester.cs.datadesc.validator.ws.WSRdfInterface;
import uk.ac.manchester.cs.datadesc.validator.ws.WSValidatorInterface;
import uk.ac.manchester.cs.datadesc.validator.ws.WsValidationConstants;

    
/**
 *
 * @author Christian
 */
public interface HtmlWSInterface extends WSRdfInterface, WSValidatorInterface{
           
    public Response validateHome(@Context HttpServletRequest httpServletRequest) throws VoidValidatorException;
    
    public Response getStatementList(@QueryParam(WsValidationConstants.SUBJECT) String subjectString, 
            @QueryParam(WsValidationConstants.PREDICATE) String predicateString, 
            @QueryParam(WsValidationConstants.OBJECT) String objectString, 
            @QueryParam(WsValidationConstants.CONTEXT) List<String> contextStrings,
            @Context HttpServletRequest httpServletRequest) throws VoidValidatorException;

    public Response getByResource(@QueryParam(WsValidationConstants.RESOURCE) String resourceString,
            @Context HttpServletRequest httpServletRequest) throws VoidValidatorException;
   
    public Response loadURI(@QueryParam(WsValidationConstants.URI) String address, 
            @QueryParam(WsValidationConstants.RDF_FORMAT) String formatName,
            @Context HttpServletRequest httpServletRequest)throws VoidValidatorException;
    
    public Response runSparqlQuery(@QueryParam(WsValidationConstants.QUERY)String query, 
            @QueryParam(WsValidationConstants.FORMAT)String formatName,
            @Context HttpServletRequest httpServletRequest)throws VoidValidatorException;
    
    public Response validate(@QueryParam(WsValidationConstants.TEXT) String text, 
            @QueryParam(WsValidationConstants.URI) String uri,
            @QueryParam(WsValidationConstants.RDF_FORMAT) String rdfFormat, 
            @QueryParam(WsValidationConstants.SPECIFICATION) String specification,
            @QueryParam(WsValidationConstants.INCLUDE_WARNINGS) Boolean includeWarning,
            @Context HttpServletRequest httpServletRequest) throws VoidValidatorException;
    
}


