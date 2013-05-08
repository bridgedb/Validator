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
package uk.ac.manchester.cs.openphacts.valdator.ws;

import java.util.ArrayList;
import java.util.List;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.resultio.TupleQueryResultFormat;
import org.openrdf.rio.RDFFormat;
import uk.ac.manchester.cs.openphacts.valdator.rdftools.RdfMinimalInterface;
import uk.ac.manchester.cs.openphacts.valdator.rdftools.VoidValidatorException;
import uk.ac.manchester.cs.openphacts.valdator.bean.ResourceBean;
import uk.ac.manchester.cs.openphacts.valdator.bean.StatementBean;
import uk.ac.manchester.cs.openphacts.valdator.bean.URIBean;

/**
 *
 * @author Christian
 */
public class RdfInterfaceToWS implements RdfMinimalInterface {

    private WSRdfInterface wsInterface;
    
    public RdfInterfaceToWS(WSRdfInterface wsInterface){
        this.wsInterface = wsInterface;
    }
    
    @Override
    public List<Statement> getStatementList(Resource subjectResource, URI predicate, Value object, Resource... contexts) 
            throws VoidValidatorException {
        String subjectString = toString(subjectResource);
        String predicateString = toString(predicate);
        String objectString = toString(object);
        List<String> contextStrings = toStringList(contexts);
        List<StatementBean> beans = wsInterface.getStatementList(subjectString, predicateString, objectString, contextStrings);
        return StatementBean.asStatements(beans);
    }

    @Override
    public List<Statement> getStatementList(Resource resource) throws VoidValidatorException {
        String resourceString = toString(resource);
        List<StatementBean> beans = wsInterface.getByResource(resourceString);
        return StatementBean.asStatements(beans);
    }

    @Override
    public URI loadURI(String address, RDFFormat format) throws VoidValidatorException {
        URIBean bean;
        if (format == null){
            bean = wsInterface.loadURI(address, null);
        } else {
            bean = wsInterface.loadURI(address, format.getName());
        }
        return URIBean.asURI(bean);
    }

    @Override
    public String runSparqlQuery(String query, TupleQueryResultFormat format) throws VoidValidatorException {
        return wsInterface.runSparqlQuery(query, format.getName());
    }
    
    private String toString(Value thing) {
        if (thing == null){
            return null;
        }
        return thing.stringValue();
    }

    private List<String> toStringList(Resource[] contexts) {
        ArrayList<String> results = new ArrayList<String>();
        if (contexts == null){
            return results;
        }
        for (Resource context:contexts){
            results.add(context.stringValue());
        }
        return results;
    }

}
