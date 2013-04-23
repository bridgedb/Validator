/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.manchester.cs.metadata;

import java.io.File;
import java.util.List;
import javax.xml.datatype.DatatypeConfigurationException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.ac.manchester.cs.rdftools.RdfHolder;
import uk.ac.manchester.cs.rdftools.RdfReader;
import uk.ac.manchester.cs.rdftools.Reporter;
import uk.ac.manchester.cs.rdftools.VoidValidatorException;
import uk.ac.manchester.cs.validator.Validator;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import uk.ac.manchester.cs.constants.OpsTestConstants;
import uk.ac.manchester.cs.constants.PavConstants;
import uk.ac.manchester.cs.constants.RdfConstants;
import uk.ac.manchester.cs.constants.VoidConstants;
import uk.ac.manchester.cs.metadata.type.XsdType;

/**
 *
 * @author Christian
 */
public class SimpleTest {
    
    static RdfReader minReader;
    static MetaDataSpecification specifications;
   
    public SimpleTest() {
        
    }
    
    @BeforeClass
    public static void setUpClass() throws VoidValidatorException {
        File file = new File ("test-data/testSimple.ttl");
        minReader = new RdfReader(file);
        specifications = new MetaDataSpecification("test-data/simpleOntology.owl");
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
        Validator validator = new Validator(minReader, specifications);
        String result = validator.validate();
        System.out.println(result);
        assertThat(result,  endsWith(Validator.SUCCESS));
    }
    
    @Test
    public void missingValueValidate() throws VoidValidatorException   {
        Reporter.println("missingValueValidate");
        RdfHolder holder = new RdfHolder(minReader);
        List<Statement> remove = minReader.getStatementList(null, OpsTestConstants.HAS_WEBSITE, null);
        assertThat( remove.size(), greaterThan(0));
        holder.removeStatements(remove);
        Validator validator = new Validator(holder, specifications);
        String result = validator.validate();
        System.out.println(result);
        assertThat(result, containsString(CardinalityMetaData.NOT_FOUND));
        assertThat(result,  endsWith(Validator.FAILED));
    }
   
    @Test
    public void missingAllAlternativesValidate() throws VoidValidatorException   {
        Reporter.println("noDataDumpValidate");
        RdfHolder holder = new RdfHolder(minReader);
        List<Statement> remove = minReader.getStatementList(null, OpsTestConstants.HAS_PHONE_NUMBER, null);
        assertThat(remove.size(), greaterThan(0));
        holder.removeStatements(remove);
        Validator validator = new Validator(holder, specifications);
        String result = validator.validate();
        assertThat(result, containsString(MetaDataAlternatives.INCLUDE_ALTERNATIVE));
        assertThat(result,  endsWith(Validator.FAILED));
    }

    @Test   
    public void extraValueTestValidate() throws VoidValidatorException   {
        Reporter.println("extraValueTestValidate");
        RdfHolder holder = new RdfHolder(minReader);
        Validator validator = new Validator(holder, specifications);
        String result = validator.validate();
        assertThat(result, containsString(ResourceMetaData.NO_ERRORS));
        assertThat(result,  endsWith(Validator.SUCCESS));
        List<Statement> oldStatements = minReader.getStatementList(null, OpsTestConstants.HAS_PHONE_NUMBER, null);
        Statement oldStatement = oldStatements.get(0);
        Value newObject = new LiteralImpl("new");
        Statement newStatement = new StatementImpl(oldStatement.getSubject(), oldStatement.getPredicate(), newObject);
        holder.addStatement(newStatement);
        validator = new Validator(holder, specifications);
        String result2 = validator.validate();
        assertEquals(result, result2);
   }
    
    @Test
    public void extraValueWithCarinalityOneValidate() throws VoidValidatorException   {
        Reporter.println("extraValueWithCarinalityOneValidate");
        RdfHolder holder = new RdfHolder(minReader);
        List<Statement> oldStatements = minReader.getStatementList(null, OpsTestConstants.HAS_WEBSITE, null);
        Statement oldStatement = oldStatements.get(0);
        URI newObject = new URIImpl(oldStatement.getObject().stringValue() + "new");
        Statement newStatement = new StatementImpl(oldStatement.getSubject(), oldStatement.getPredicate(), newObject);
        holder.addStatement(newStatement);
        Validator validator = new Validator(holder, specifications);
        String result = validator.validate();
        assertThat(result, containsString(CardinalityMetaData.REMOVE));
        assertThat(result,  endsWith(Validator.FAILED));
   }

    @Test
    public void uriAsStringValidate() throws VoidValidatorException   {
        Reporter.println("uriAsStringValidate");
        RdfHolder holder = new RdfHolder(minReader);
        List<Statement> oldStatements = minReader.getStatementList(null, OpsTestConstants.HAS_WEBSITE, null);
        holder.removeStatements(oldStatements);
        Statement oldStatement = oldStatements.get(0);
        Value newObject = new LiteralImpl("this is a String");
        Statement newStatement = new StatementImpl(oldStatement.getSubject(), oldStatement.getPredicate(), newObject);
        holder.addStatement(newStatement);
        Validator validator = new Validator(holder, specifications);
        String result = validator.validate();
        assertThat(result, containsString(PropertyMetaData.EXPECTED_TYPE));
        assertThat(result,  endsWith(Validator.FAILED));
   }
    
    @Test
    public void dataTimeAsDateValidate() throws VoidValidatorException, DatatypeConfigurationException   {
        Reporter.println("dataTimeAsDateValidate");
        RdfHolder holder = new RdfHolder(minReader);
        List<Statement> oldStatements = minReader.getStatementList(null, OpsTestConstants.HAS_BIRTHDATE, null);
        Statement oldStatement = oldStatements.get(0);
        Value newObject = new LiteralImpl("2013-01-17", XsdType.DATE.asURI());
        Statement newStatement = new StatementImpl(oldStatement.getSubject(), oldStatement.getPredicate(), newObject);
        holder.addStatement(newStatement);
        Validator validator = new Validator(holder, specifications);
        String result = validator.validate();
        assertThat(result, containsString(PropertyMetaData.EXPECTED_TYPE));
        assertThat(result,  endsWith(Validator.FAILED));
   }
        
    @Test
    public void missingOneOfGroupValidate() throws VoidValidatorException   {
        Reporter.println("missingOneOfGroupValidate");
        RdfHolder holder = new RdfHolder(minReader);
        List<Statement> remove = minReader.getStatementList(null, OpsTestConstants.HAS_HOUSE_NUMBER, null);
        assertThat(remove.size(), greaterThan(0));
        holder.removeStatements(remove);
        Validator validator = new Validator(holder, specifications);
        String result = validator.validate();
        assertThat(result, containsString(MetaDataGroup.INCLUDE_ALL));       
        assertThat(result,  endsWith(Validator.FAILED));
    }

    @Test
    public void missingOneOfGroupButHaveAlternativeValidate() throws VoidValidatorException   {
        Reporter.println("missingOneOfGroupButHaveAlternativeValidate");
        RdfHolder holder = new RdfHolder(minReader);
        List<Statement> oldStatements = minReader.getStatementList(null, OpsTestConstants.HAS_HOUSE_NUMBER, null);
        holder.removeStatements(oldStatements);
        Statement oldStatement = oldStatements.get(0);
        Statement newStatement = new StatementImpl(oldStatement.getSubject(), OpsTestConstants.HAS_HOUSE_NUMBER, oldStatement.getObject());
        holder.addStatement(newStatement);
        
        Validator validator = new Validator(holder, specifications);
        String result = validator.validate();
        assertThat(result,  endsWith(Validator.SUCCESS));
    }

    @Test
    public void missingLinkedType() throws VoidValidatorException   {
        Reporter.println("missingOneOfGroupButHaveAlternativeValidate");
        RdfHolder holder = new RdfHolder(minReader);
        List<Statement> remove = minReader.getStatementList(null, RdfConstants.TYPE_URI , OpsTestConstants.PARENT);
        assertThat(remove.size(), greaterThan(0));
        holder.removeStatements(remove);
          
        Validator validator = new Validator(holder, specifications);
        String result = validator.validate();
        assertThat(result,  endsWith(Validator.SUCCESS));
    }
}
