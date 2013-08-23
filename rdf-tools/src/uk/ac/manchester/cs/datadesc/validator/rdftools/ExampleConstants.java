/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.manchester.cs.datadesc.validator.rdftools;

/**
 *
 * @author Christian
 */
public class ExampleConstants {
    
    public static final String EXAMPLE_CONTEXT = "https://github.com/openphacts/Validator/blob/master/RdfTools/test-data/testPart1.ttl";
    public static final String EXAMPLE_RESOURCE = "http://example.com/part1#person2";
    public static final String EXAMPLE_QUERY = "SELECT ?p ?o WHERE { <" + ExampleConstants.EXAMPLE_RESOURCE + "> ?p ?o } ";
    public static final String EXAMPLE_OUTPUT_FORMAT = "SPARQL/CSV";
    
}
