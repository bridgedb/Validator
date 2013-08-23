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
import java.util.List;
import javax.xml.datatype.DatatypeConfigurationException;
import static org.hamcrest.Matchers.*;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.ContextStatementImpl;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.URIImpl;
import uk.ac.manchester.cs.datadesc.validator.RdfValidator;
import uk.ac.manchester.cs.datadesc.validator.ValidatorExampleConstants;
import uk.ac.manchester.cs.datadesc.validator.constants.OpsTestConstants;
import uk.ac.manchester.cs.datadesc.validator.constants.RdfConstants;
import uk.ac.manchester.cs.datadesc.validator.metadata.type.XsdType;
import uk.ac.manchester.cs.datadesc.validator.rdftools.RdfFactory;
import uk.ac.manchester.cs.datadesc.validator.rdftools.RdfHolder;
import uk.ac.manchester.cs.datadesc.validator.rdftools.RdfReader;
import uk.ac.manchester.cs.datadesc.validator.rdftools.Reporter;
import uk.ac.manchester.cs.datadesc.validator.rdftools.VoidValidatorException;

/**
 *
 * @author Christian
 */
public class SimpleTest {
    
    static RdfReader minReader;
    static Resource minContext;
    static MetaDataSpecification specifications;
   
    private static final Resource ALL_SUBJECTS = null;
    private static final Resource ALL_OBJECTS = null;
    private static final boolean INCLUDE_WARNINGS = true;
    private static final boolean NO_WARNINGS = false;
    
    public SimpleTest() {
        
    }
    
    @BeforeClass
    public static void setUpClass() throws VoidValidatorException {
        File file = new File ("test-data/testSimple.ttl");
        minReader = RdfFactory.getMemory();
        minContext = minReader.loadFile(file);
        MetaDataSpecification.LoadSpecification(ValidatorExampleConstants.SIMPLE_FILE, 
               ValidatorExampleConstants.SIMPLE_NAME, ValidatorExampleConstants.SIMPLE_DESCRIPTION);
       specifications = MetaDataSpecification.specificationByName(ValidatorExampleConstants.SIMPLE_NAME);
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void minFileValidate() throws VoidValidatorException {
        Reporter.println("minFileValidate");
        String result = RdfValidator.validate(minReader, minContext, specifications, INCLUDE_WARNINGS);
        assertThat(result,  endsWith(RdfValidator.SUCCESS));       
    }
    
    @Test
    public void missingValueValidate() throws VoidValidatorException   {
        Reporter.println("missingValueValidate");
        RdfHolder holder = new RdfHolder(minReader, minContext);
        List<Statement> remove = 
                minReader.getDirectOnlyStatementList(ALL_SUBJECTS, OpsTestConstants.HAS_WEBSITE, ALL_OBJECTS, minContext);
        assertThat( remove.size(), greaterThan(0));
        holder.removeStatements(remove);
        String result = RdfValidator.validate(holder, minContext, specifications, INCLUDE_WARNINGS);
        assertThat(result, containsString(CardinalityMetaData.NOT_FOUND));
        assertThat(result,  endsWith(RdfValidator.FAILED));
    }
   
    @Test
    public void missingAllAlternativesValidate() throws VoidValidatorException   {
        Reporter.println("missingAllAlternativesValidate");
        RdfHolder holder = new RdfHolder(minReader, minContext);
        List<Statement> remove = 
                minReader.getDirectOnlyStatementList(ALL_SUBJECTS, OpsTestConstants.HAS_PHONE_NUMBER, ALL_OBJECTS, minContext);
        assertThat(remove.size(), greaterThan(0));
        holder.removeStatements(remove);
        String result = RdfValidator.validate(holder, minContext, specifications, INCLUDE_WARNINGS);
        assertThat(result, containsString(MetaDataAlternatives.INCLUDE_ALTERNATIVE));
        assertThat(result,  endsWith(RdfValidator.FAILED));
    }

    @Test   
    public void extraValueTestValidate() throws VoidValidatorException   {
        Reporter.println("extraValueTestValidate");
        RdfHolder holder = new RdfHolder(minReader, minContext);
        String result = RdfValidator.validate(holder, minContext, specifications, INCLUDE_WARNINGS);
        assertThat(result, containsString(ResourceMetaData.NO_ERRORS));
        assertThat(result,  endsWith(RdfValidator.SUCCESS));
        List<Statement> oldStatements = 
                minReader.getDirectOnlyStatementList(ALL_SUBJECTS, OpsTestConstants.HAS_PHONE_NUMBER, ALL_OBJECTS, minContext);
        Statement oldStatement = oldStatements.get(0);
        Value newObject = new LiteralImpl("new");
        Statement newStatement = 
                new ContextStatementImpl(oldStatement.getSubject(), oldStatement.getPredicate(), newObject, minContext);
        holder.addStatement(newStatement);
        String result2 = RdfValidator.validate(holder, minContext, specifications, INCLUDE_WARNINGS);
        assertEquals(result, result2);
   }
    
    @Test
    public void extraValueWithCarinalityOneValidate() throws VoidValidatorException   {
        Reporter.println("extraValueWithCarinalityOneValidate");
        RdfHolder holder = new RdfHolder(minReader, minContext);
        List<Statement> oldStatements = 
                minReader.getDirectOnlyStatementList(ALL_SUBJECTS, OpsTestConstants.HAS_WEBSITE, ALL_OBJECTS, minContext);
        Statement oldStatement = oldStatements.get(0);
        URI newObject = new URIImpl(oldStatement.getObject().stringValue() + "new");
        Statement newStatement = 
                new ContextStatementImpl(oldStatement.getSubject(), oldStatement.getPredicate(), newObject, minContext);
        holder.addStatement(newStatement);
        String result = RdfValidator.validate(holder, minContext, specifications, INCLUDE_WARNINGS);
        assertThat(result, containsString(CardinalityMetaData.REMOVE));
        assertThat(result,  endsWith(RdfValidator.FAILED));
   }

    @Test
    public void uriAsStringValidate() throws VoidValidatorException   {
        Reporter.println("uriAsStringValidate");
        RdfHolder holder = new RdfHolder(minReader, minContext);
        List<Statement> oldStatements = 
                minReader.getDirectOnlyStatementList(ALL_SUBJECTS, OpsTestConstants.HAS_WEBSITE, ALL_OBJECTS, minContext);
        holder.removeStatements(oldStatements);
        Statement oldStatement = oldStatements.get(0);
        Value newObject = new LiteralImpl("this is a String");
        Statement newStatement = 
                new ContextStatementImpl(oldStatement.getSubject(), oldStatement.getPredicate(), newObject, minContext);
        holder.addStatement(newStatement);
        String result = RdfValidator.validate(holder, minContext, specifications, INCLUDE_WARNINGS);
        assertThat(result, containsString(PropertyMetaData.EXPECTED_TYPE));
        assertThat(result,  endsWith(RdfValidator.FAILED));
   }
    
    @Test
    public void dataTimeAsDateValidate() throws VoidValidatorException, DatatypeConfigurationException   {
        Reporter.println("dataTimeAsDateValidate");
        RdfHolder holder = new RdfHolder(minReader, minContext);
        List<Statement> oldStatements = 
                minReader.getDirectOnlyStatementList(ALL_SUBJECTS, OpsTestConstants.HAS_BIRTHDATE, ALL_OBJECTS, minContext);
        Statement oldStatement = oldStatements.get(0);
        Value newObject = new LiteralImpl("2013-01-17", XsdType.DATE.asURI());
        Statement newStatement = 
                new ContextStatementImpl(oldStatement.getSubject(), oldStatement.getPredicate(), newObject, minContext);
        holder.addStatement(newStatement);
        String result = RdfValidator.validate(holder, minContext, specifications, INCLUDE_WARNINGS);
        assertThat(result, containsString(PropertyMetaData.EXPECTED_TYPE));
        assertThat(result,  endsWith(RdfValidator.FAILED));
   }
        
    @Test
    public void missingOneOfGroupValidate() throws VoidValidatorException   {
        Reporter.println("missingOneOfGroupValidate");
        RdfHolder holder = new RdfHolder(minReader, minContext);
        List<Statement> remove = 
                minReader.getDirectOnlyStatementList(ALL_SUBJECTS, OpsTestConstants.HAS_HOUSE_NUMBER, ALL_OBJECTS, minContext);
        assertThat(remove.size(), greaterThan(0));
        holder.removeStatements(remove);
        String result = RdfValidator.validate(holder, minContext, specifications, INCLUDE_WARNINGS);
        assertThat(result, containsString(MetaDataGroup.INCLUDE_ALL));       
        assertThat(result,  endsWith(RdfValidator.FAILED));
    }

    @Test
    public void missingOneOfGroupButHaveAlternativeValidate() throws VoidValidatorException   {
        Reporter.println("missingOneOfGroupButHaveAlternativeValidate");
        RdfHolder holder = new RdfHolder(minReader, minContext);
        List<Statement> oldStatements = 
                minReader.getDirectOnlyStatementList(ALL_SUBJECTS, OpsTestConstants.HAS_HOUSE_NUMBER, ALL_OBJECTS, minContext);
        holder.removeStatements(oldStatements);
        Statement oldStatement = oldStatements.get(0);
        Statement newStatement = new ContextStatementImpl(
                oldStatement.getSubject(), OpsTestConstants.HAS_HOUSE_NUMBER, oldStatement.getObject(), minContext);
        holder.addStatement(newStatement);       
        String result = RdfValidator.validate(holder, minContext, specifications, INCLUDE_WARNINGS);
        assertThat(result,  endsWith(RdfValidator.SUCCESS));
    }

    @Test
    public void missingLinkedType() throws VoidValidatorException   {
        Reporter.println("missingLinkedType");
        RdfHolder holder = new RdfHolder(minReader, minContext);
        List<Statement> remove = minReader.getDirectOnlyStatementList(ALL_SUBJECTS, RdfConstants.TYPE_URI , OpsTestConstants.PERSON, minContext);
        assertThat(remove.size(), greaterThan(0));
        holder.removeStatements(remove);
          
        String result = RdfValidator.validate(holder, minContext, specifications, INCLUDE_WARNINGS);
        assertThat(result,  endsWith(RdfValidator.SUCCESS));
    }
    
    @Test
    public void missingLinkedTypeBad() throws VoidValidatorException   {
        Reporter.println("missingLinkedTypeBad");
        RdfHolder holder = new RdfHolder(minReader, minContext);
        List<Statement> remove = minReader.getDirectOnlyStatementList(ALL_SUBJECTS, RdfConstants.TYPE_URI , OpsTestConstants.PERSON, minContext);
        assertThat(remove.size(), greaterThan(0));
        holder.removeStatements(remove);
        remove = 
                minReader.getDirectOnlyStatementList(ALL_SUBJECTS, OpsTestConstants.HAS_HOUSE_NUMBER, ALL_OBJECTS, minContext);
        assertThat(remove.size(), greaterThan(0));
        holder.removeStatements(remove);
         
        String result = RdfValidator.validate(holder, minContext, specifications, INCLUDE_WARNINGS);
        assertThat(result, containsString(LinkedResource.NO_KNOWN_TYPE));       
        assertThat(result,  endsWith(RdfValidator.FAILED));
    }
 
    @Test
    public void testNoWarnings() throws VoidValidatorException {
        Reporter.println("NoWarnings");
        RdfHolder holder = new RdfHolder(minReader, minContext);
        List<Statement> remove = minReader.getDirectOnlyStatementList(ALL_SUBJECTS, OpsTestConstants.HAS_WEDDING_DATE , ALL_OBJECTS, minContext);
        assertThat(remove.size(), greaterThan(0));
        holder.removeStatements(remove);
        String result = RdfValidator.validate(holder, minContext, specifications, INCLUDE_WARNINGS);
        assertThat(result, containsString(CardinalityMetaData.WARNING));          
        assertThat(result,  endsWith(RdfValidator.SUCCESS));     
        result = RdfValidator.validate(holder, minContext, specifications, NO_WARNINGS);
        assertThat(result, not(containsString(CardinalityMetaData.WARNING)));          
        assertThat(result,  endsWith(RdfValidator.SUCCESS));     
    }

    @Test
    public void testBadFormatShould() throws VoidValidatorException {
        Reporter.println("BadFormatShould");
        RdfHolder holder = new RdfHolder(minReader, minContext);
        List<Statement> oldStatements = minReader.getDirectOnlyStatementList(ALL_SUBJECTS, OpsTestConstants.HAS_WEDDING_DATE , ALL_OBJECTS, minContext);
        Statement oldStatement = oldStatements.get(0);
        holder.removeStatement(oldStatement);
        Value newObject = new LiteralImpl("2013-01-17", XsdType.DATE.asURI());
        Statement newStatement = 
                new ContextStatementImpl(oldStatement.getSubject(), oldStatement.getPredicate(), newObject, minContext);
        holder.addStatement(newStatement);
        String result = RdfValidator.validate(holder, minContext, specifications, INCLUDE_WARNINGS);
        assertThat(result, not(containsString(CardinalityMetaData.WARNING)));          
        assertThat(result,  endsWith(RdfValidator.FAILED));     
        assertThat(result, containsString(PropertyMetaData.EXPECTED_TYPE));
        String result2 = RdfValidator.validate(holder, minContext, specifications, NO_WARNINGS);
        assertEquals(result, result2);
     }
 
    @Test
    public void testDoubleShould() throws VoidValidatorException {
        Reporter.println("DoubleShould");
        RdfHolder holder = new RdfHolder(minReader, minContext);
        List<Statement> oldStatements = minReader.getDirectOnlyStatementList(ALL_SUBJECTS, OpsTestConstants.HAS_WEDDING_DATE , ALL_OBJECTS, minContext);
        Statement oldStatement = oldStatements.get(0);
        Value newObject = new LiteralImpl("2002-11-17T15:00:00Z", XsdType.DATE_TIME.asURI());
        Statement newStatement = 
                new ContextStatementImpl(oldStatement.getSubject(), oldStatement.getPredicate(), newObject, minContext);
        holder.addStatement(newStatement);
        String result = RdfValidator.validate(holder, minContext, specifications, INCLUDE_WARNINGS);
        assertThat(result, not(containsString(CardinalityMetaData.WARNING)));          
        assertThat(result,  endsWith(RdfValidator.FAILED));     
        assertThat(result, containsString(CardinalityMetaData.HOWEVER_FOUND));
        String result2 = RdfValidator.validate(holder, minContext, specifications, NO_WARNINGS);
        assertEquals(result, result2);
     }
}
