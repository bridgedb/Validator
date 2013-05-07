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
package uk.ac.manchester.cs.bean;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.ContextStatementImpl;

/**
 *
 * @author Christian
 */
@XmlRootElement(name="Statement")
public class StatementBean {

    private URIBean subject;
    private URIBean predicate;
    private URIBean object;
    private LiteralBean literalObject;
    private URIBean context;
    
    public StatementBean(){
    }
    
    public static Statement asStatement(StatementBean bean) {
        URI subject = URIBean.asURI(bean.getSubject());
        URI predicate = URIBean.asURI(bean.getPredicate());
        Value object = URIBean.asURI(bean.getObject());
        if (object == null){
            object = LiteralBean.asLiteral(bean.literalObject);
        }
        URI context = URIBean.asURI(bean.getContext());
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
        Resource subject = statement.getSubject();
        if (subject instanceof URI){
            bean.setSubject(URIBean.asBean((URI)subject)); 
        } else {
            throw new UnsupportedOperationException("Not yet implemented " + subject.getClass());
        }
        bean.setPredicate(URIBean.asBean(statement.getPredicate()));
        Value object = statement.getObject();
        if (object instanceof URI){
            bean.setObject(URIBean.asBean((URI)object)); 
        } else if (object instanceof Literal){
            bean.setLiteralObject(LiteralBean.asBean((Literal)object)); 
        } else {
            throw new UnsupportedOperationException("Not yet implemented " + object.getClass());
        }
        Resource context = statement.getContext();
        if (context != null){
            if (context instanceof URI){
                bean.setContext(URIBean.asBean((URI)context)); 
            } else {
                throw new UnsupportedOperationException("Not yet implemented " + context.getClass());
            }
        }
        return bean;
    }

    /**
     * @return the subject
     */
    public URIBean getSubject() {
        return subject;
    }

    /**
     * @param subject the subject to set
     */
    public void setSubject(URIBean subject) {
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
    public URIBean getObject() {
        return object;
    }

    /**
     * @param object the object to set
     */
    public void setObject(URIBean object) {
        this.object = object;
    }

    /**
     * @return the literalObject
     */
    public LiteralBean getLiteralObject() {
        return literalObject;
    }

    /**
     * @param literalObject the literalObject to set
     */
    public void setLiteralObject(LiteralBean literalObject) {
        this.literalObject = literalObject;
    }

    /**
     * @return the context
     */
    public URIBean getContext() {
        return context;
    }

    /**
     * @param context the context to set
     */
    public void setContext(URIBean context) {
        this.context = context;
    }

}
