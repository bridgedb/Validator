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
package uk.ac.manchester.cs.server.bean;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.ContextStatementImpl;
import org.openrdf.model.impl.StatementImpl;

/**
 *
 * @author Christian
 */
@XmlRootElement(name="Statement")
public class StatementBean {

    private ResourceBean subject;
    private URIBean predicate;
    private ValueBean object;
    private ResourceBean context;
    
    public StatementBean(){
    }
    
    public static Statement asStatement(StatementBean bean) {
        Resource subject = ResourceBean.asResource(bean.getSubject());
        URI predicate = URIBean.asURI(bean.getPredicate());
        Value object = ValueBean.asValue(bean.getObject());
        Resource context = ResourceBean.asResource(bean.getContext());
        return new ContextStatementImpl(subject, predicate, object, context);
    }

    public static List<Statement> asStatements(List<StatementBean> beans) {
        ArrayList<Statement> results = new ArrayList<Statement>();
        for (StatementBean bean:beans){
            results.add(asStatement(bean));
        }
        return results;
    }

    public static List<StatementBean> asBeans(List<Statement> statements) {
        ArrayList<StatementBean> results = new ArrayList<StatementBean>();
        for (Statement statement:statements){
            results.add(asBean(statement));
        }
        return results;
    }

    private static StatementBean asBean(Statement statement) {
        StatementBean bean = new StatementBean();
        bean.setSubject(ResourceBean.asBean(statement.getSubject()));
        bean.setPredicate(URIBean.asBean(statement.getPredicate()));
        bean.setObject(ValueBean.asBean(statement.getObject()));
        bean.setContext(ResourceBean.asBean(statement.getContext()));
        return bean;
    }

    /**
     * @return the subject
     */
    public ResourceBean getSubject() {
        return subject;
    }

    /**
     * @param subject the subject to set
     */
    public void setSubject(ResourceBean subject) {
        this.subject = subject;
    }

    /**
     * @return the predicate
     */
    public URIBean getPredicate() {
        return predicate;
    }

    /**
     * @param predicate the predicate to set
     */
    public void setPredicate(URIBean predicate) {
        this.predicate = predicate;
    }

    /**
     * @return the object
     */
    public ValueBean getObject() {
        return object;
    }

    /**
     * @param object the object to set
     */
    public void setObject(ValueBean object) {
        this.object = object;
    }

    /**
     * @return the context
     */
    public ResourceBean getContext() {
        return context;
    }

    /**
     * @param context the context to set
     */
    public void setContext(ResourceBean context) {
        this.context = context;
    }
    
}
