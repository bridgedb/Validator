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
package uk.ac.manchester.cs.openphacts.valdator.client;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import java.io.InputStream;
import java.util.List;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import uk.ac.manchester.cs.openphacts.valdator.bean.StatementBean;
import uk.ac.manchester.cs.openphacts.valdator.bean.URIBean;
import uk.ac.manchester.cs.openphacts.valdator.rdftools.VoidValidatorException;
import uk.ac.manchester.cs.openphacts.valdator.ws.WSRdfInterface;
import uk.ac.manchester.cs.openphacts.valdator.ws.WSValidatorInterface;
import uk.ac.manchester.cs.openphacts.valdator.ws.WsValidationConstants;

/**
 *
 * @author Christian
 */
public class WSRdfClient implements WSRdfInterface, WSValidatorInterface {

    protected final WebResource webResource;
    
    public WSRdfClient(String serviceAddress){
        //this.serviceAddress = serviceAddress;
        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create(config);
        webResource = client.resource(serviceAddress);        
    }

    public String homePage(){
        //Make service call
        String result = 
                webResource.path(WsValidationConstants.VALIDATE_HOME)
                .accept(MediaType.TEXT_HTML)
                .get(new GenericType<String>() {});
         return result;
    }
    
    @Override
    public List<StatementBean> getStatementList(String subjectString, String predicateString, String objectString,
            List<String> contextStrings) throws VoidValidatorException {        
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        if (subjectString != null){
            params.add(WsValidationConstants.SUBJECT, subjectString);
        }
        if (predicateString != null){
            params.add(WsValidationConstants.PREDICATE, predicateString);
        }
        if (objectString != null){
            params.add(WsValidationConstants.OBJECT, objectString);
        }
        if (contextStrings != null){
            for (String contextString:contextStrings){
                params.add(WsValidationConstants.CONTEXT, contextString);
            }
        }
        //Make service call
        List<StatementBean> result = 
                webResource.path(WsValidationConstants.STATEMENT_LIST)
                .queryParams(params)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<List<StatementBean>>() {});
         return result;
    }

    @Override
    public List<StatementBean> getByResource(String resourceString) throws VoidValidatorException {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add(WsValidationConstants.RESOURCE, resourceString);
        //Make service call
        List<StatementBean> result = 
                webResource.path(WsValidationConstants.BY_RESOURCE)
                .queryParams(params)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<List<StatementBean>>() {});
         return result;
    }

    @Override
    public URIBean loadURI(String address, String formatName) throws VoidValidatorException {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add(WsValidationConstants.URI, address);
        if (formatName != null){
            params.add(WsValidationConstants.RDF_FORMAT, formatName);
        }
        //Make service call
        URIBean result = 
                webResource.path(WsValidationConstants.LOAD_URI)
                .queryParams(params)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<URIBean>() {});
         return result;
    }

    @Override
    public String runSparqlQuery(String query, String formatName) throws VoidValidatorException {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add(WsValidationConstants.QUERY, query);
        if (formatName != null){
            params.add(WsValidationConstants.FORMAT, formatName);
        }
        //Make service call
        String result = 
                webResource.path(WsValidationConstants.SPARQL)
                .queryParams(params)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<String>() {});
         return result;
    }

    @Override
    public String validate(String text, String uri, String rdfFormat, String specification, Boolean includeWarning) throws VoidValidatorException {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        if (text != null){
            params.add(WsValidationConstants.TEXT, text);
        }
        if (uri != null){
            params.add(WsValidationConstants.URI, uri);
        }
        if (rdfFormat != null){
            params.add(WsValidationConstants.RDF_FORMAT, rdfFormat);
        }
        if (specification != null){
            params.add(WsValidationConstants.SPECIFICATION, specification);
        }
        if (includeWarning != null){
            params.add(WsValidationConstants.INCLUDE_WARNINGS, includeWarning.toString());
        }
        //Make service call
        String result = 
                webResource.path(WsValidationConstants.VALIDATE)
                .queryParams(params)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<String>() {});
         return result;
    }

    @Override
    public String validate(InputStream stream, String rdfFormat, String specification, Boolean includeWarning) throws VoidValidatorException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
