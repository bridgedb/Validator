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
package uk.ac.manchester.cs.datadesc.validator;

import java.io.InputStream;
import uk.ac.manchester.cs.datadesc.validator.rdftools.VoidValidatorException;

/**
 *
 * @author Christian
 */
public interface Validator {
    String validateText(String text, String formatName, String specificationName, Boolean includeWarning) throws VoidValidatorException;
    
    String validateUri(String uri, String formatName, String specificationName, Boolean includeWarning) throws VoidValidatorException;

    String validateInputStream(InputStream stream, String formatName, String specificationName, Boolean includeWarning) throws VoidValidatorException;
}
