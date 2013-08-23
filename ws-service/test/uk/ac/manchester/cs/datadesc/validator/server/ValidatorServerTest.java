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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.ac.manchester.cs.datadesc.validator.metadata.ValidatorTest;
import uk.ac.manchester.cs.datadesc.validator.rdftools.RdfFactory;
import uk.ac.manchester.cs.datadesc.validator.rdftools.VoidValidatorException;
import uk.ac.manchester.cs.datadesc.validator.ws.ValidatorInterfaceToWS;

/**
 *
 * @author Christian
 */
public class ValidatorServerTest extends ValidatorTest{
    
    @BeforeClass
    public static void setUpValidator() throws VoidValidatorException {
        WsValidatorServer ws = new WsFrame(RdfFactory.getMemory());
        validator = new ValidatorInterfaceToWS(ws);
    }
    
    @Test
    @Override
    public void testInputStreamValidate() throws VoidValidatorException, FileNotFoundException, IOException, URISyntaxException {
        //exclude test for not fully implemented function
    }
}
