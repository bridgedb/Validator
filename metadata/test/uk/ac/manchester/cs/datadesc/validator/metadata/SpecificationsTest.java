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
package uk.ac.manchester.cs.datadesc.validator.metadata;

import java.io.File;
import java.io.IOException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import uk.ac.manchester.cs.datadesc.validator.bean.JacksonMarshaller;
import uk.ac.manchester.cs.datadesc.validator.bean.SpecificationBean;
import uk.ac.manchester.cs.datadesc.validator.rdftools.VoidValidatorException;

/**
 *
 * @author Christian
 */
public class SpecificationsTest {
    
    static MetaDataSpecification specifications;
   
    private static final Resource ALL_SUBJECTS = null;
    private static final URI ALL_PREDICATES = null;
    private static final Value ALL_OBJECTS = null;
    private static final boolean INCLUDE_WARNINGS = true;
    
    @BeforeClass
    public static void setUpClass() throws VoidValidatorException {
       String fileName = "voidInfo1_4.owl";
       MetaDataSpecification.LoadSpecification(fileName, "version1.4", "Test load");
       specifications = MetaDataSpecification.specificationByName("version1.4");
    }

    @Test
    public void testJson() throws IOException, VoidValidatorException{
        SpecificationBean bean = new SpecificationBean(specifications);
        File generated = new File("test-data/version1_4.json");
        JacksonMarshaller.marshal(generated, bean);
        SpecificationBean result = (SpecificationBean)JacksonMarshaller.unmarshal(generated, SpecificationBean.class);
    }
    
 
}
