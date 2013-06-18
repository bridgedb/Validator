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
package uk.ac.manchester.cs.openphacts.validator;

import java.io.InputStream;
import org.openrdf.model.Resource;
import org.openrdf.rio.RDFFormat;
import uk.ac.manchester.cs.openphacts.valdator.metadata.MetaDataSpecification;
import uk.ac.manchester.cs.openphacts.valdator.rdftools.RdfFactory;
import uk.ac.manchester.cs.openphacts.valdator.rdftools.RdfInterface;
import uk.ac.manchester.cs.openphacts.valdator.rdftools.RdfReader;
import uk.ac.manchester.cs.openphacts.valdator.rdftools.VoidValidatorException;

/**
 *
 * @author Christian
 */
public class ValidatorImpl implements Validator{

    private final RdfInterface parent;
    
    public ValidatorImpl(){
        parent = null;
    }
    
    public ValidatorImpl(RdfInterface parent){
        this.parent = parent;
    }
    
    @Override
    public String validateText(String text, String formatName, String specificationName, Boolean includeWarning) 
            throws VoidValidatorException {
        RdfReader rdf = RdfFactory.getMemory();
        if (parent != null){
            rdf.addOtherSource(parent);
        }
        RDFFormat rdfFormat = RDFFormat.valueOf(formatName);
        Resource context = rdf.loadString(text, rdfFormat);
        MetaDataSpecification specifications = MetaDataSpecification.specificationByName(specificationName);
        return RdfValidator.validate(rdf, context, specifications, includeWarning);
    }

    @Override
    public String validateUri(String Uri, String formatName, String specificationName, Boolean includeWarning) throws VoidValidatorException {
        RdfReader rdf = RdfFactory.getMemory();
        if (parent != null){
            rdf.addOtherSource(parent);
        }
        Resource context;
        if (formatName != null){
            RDFFormat rdfFormat = RDFFormat.valueOf(formatName);
            context = rdf.loadURI(Uri, rdfFormat);
        } else {
            context = rdf.loadURI(Uri);
        }
        MetaDataSpecification specifications = MetaDataSpecification.specificationByName(specificationName);
        return RdfValidator.validate(rdf, context, specifications, includeWarning);
    }

    @Override
    public String validateInputStream(InputStream stream, String formatName, String specificationName, Boolean includeWarning) throws VoidValidatorException {
        RdfReader rdf = RdfFactory.getMemory();
        if (parent != null){
            rdf.addOtherSource(parent);
        }
        Resource context;
        RDFFormat rdfFormat = RDFFormat.valueOf(formatName);
        context = rdf.loadInputStream(stream, rdfFormat);
        MetaDataSpecification specifications = MetaDataSpecification.specificationByName(specificationName);
        String rawValidation = RdfValidator.validate(rdf, context, specifications, includeWarning);
        rawValidation = rawValidation.replaceAll(RdfReader.DEFAULT_BASE_URI, "");
        return rawValidation;
    }

     
}
