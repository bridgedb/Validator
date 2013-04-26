package uk.ac.manchester.cs.metadata;

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
import uk.ac.manchester.cs.constants.OpsTestConstants;
import uk.ac.manchester.cs.constants.RdfConstants;
import uk.ac.manchester.cs.metadata.type.XsdType;
import uk.ac.manchester.cs.rdftools.RdfFactory;
import uk.ac.manchester.cs.rdftools.RdfHolder;
import uk.ac.manchester.cs.rdftools.RdfReader;
import uk.ac.manchester.cs.rdftools.Reporter;
import uk.ac.manchester.cs.rdftools.VoidValidatorException;
import uk.ac.manchester.cs.validator.Validator;

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
    
    public SimpleTest() {
        
    }
    
    @BeforeClass
    public static void setUpClass() throws VoidValidatorException {
        File file = new File ("test-data/testSimple.ttl");
        minReader = RdfFactory.getMemory();
        minContext = minReader.loadFile(file);
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
        String result = Validator.validate(minReader, minContext, specifications);
        assertThat(result,  endsWith(Validator.SUCCESS));
    }
    
    @Test
    public void missingValueValidate() throws VoidValidatorException   {
        Reporter.println("missingValueValidate");
        RdfHolder holder = new RdfHolder(minReader, minContext);
        List<Statement> remove = 
                minReader.getStatementList(ALL_SUBJECTS, OpsTestConstants.HAS_WEBSITE, ALL_OBJECTS, minContext);
        assertThat( remove.size(), greaterThan(0));
        holder.removeStatements(remove);
        String result = Validator.validate(holder, minContext, specifications);
        assertThat(result, containsString(CardinalityMetaData.NOT_FOUND));
        assertThat(result,  endsWith(Validator.FAILED));
    }
   
    @Test
    public void missingAllAlternativesValidate() throws VoidValidatorException   {
        Reporter.println("missingAllAlternativesValidate");
        RdfHolder holder = new RdfHolder(minReader, minContext);
        List<Statement> remove = 
                minReader.getStatementList(ALL_SUBJECTS, OpsTestConstants.HAS_PHONE_NUMBER, ALL_OBJECTS, minContext);
        assertThat(remove.size(), greaterThan(0));
        holder.removeStatements(remove);
        String result = Validator.validate(holder, minContext, specifications);
        assertThat(result, containsString(MetaDataAlternatives.INCLUDE_ALTERNATIVE));
        assertThat(result,  endsWith(Validator.FAILED));
    }

    @Test   
    public void extraValueTestValidate() throws VoidValidatorException   {
        Reporter.println("extraValueTestValidate");
        RdfHolder holder = new RdfHolder(minReader, minContext);
        String result = Validator.validate(holder, minContext, specifications);
        assertThat(result, containsString(ResourceMetaData.NO_ERRORS));
        assertThat(result,  endsWith(Validator.SUCCESS));
        List<Statement> oldStatements = 
                minReader.getStatementList(ALL_SUBJECTS, OpsTestConstants.HAS_PHONE_NUMBER, ALL_OBJECTS, minContext);
        Statement oldStatement = oldStatements.get(0);
        Value newObject = new LiteralImpl("new");
        Statement newStatement = 
                new ContextStatementImpl(oldStatement.getSubject(), oldStatement.getPredicate(), newObject, minContext);
        holder.addStatement(newStatement);
        String result2 = Validator.validate(holder, minContext, specifications);
        assertEquals(result, result2);
   }
    
    @Test
    public void extraValueWithCarinalityOneValidate() throws VoidValidatorException   {
        Reporter.println("extraValueWithCarinalityOneValidate");
        RdfHolder holder = new RdfHolder(minReader, minContext);
        List<Statement> oldStatements = 
                minReader.getStatementList(ALL_SUBJECTS, OpsTestConstants.HAS_WEBSITE, ALL_OBJECTS, minContext);
        Statement oldStatement = oldStatements.get(0);
        URI newObject = new URIImpl(oldStatement.getObject().stringValue() + "new");
        Statement newStatement = 
                new ContextStatementImpl(oldStatement.getSubject(), oldStatement.getPredicate(), newObject, minContext);
        holder.addStatement(newStatement);
        String result = Validator.validate(holder, minContext, specifications);
        assertThat(result, containsString(CardinalityMetaData.REMOVE));
        assertThat(result,  endsWith(Validator.FAILED));
   }

    @Test
    public void uriAsStringValidate() throws VoidValidatorException   {
        Reporter.println("uriAsStringValidate");
        RdfHolder holder = new RdfHolder(minReader, minContext);
        List<Statement> oldStatements = 
                minReader.getStatementList(ALL_SUBJECTS, OpsTestConstants.HAS_WEBSITE, ALL_OBJECTS, minContext);
        holder.removeStatements(oldStatements);
        Statement oldStatement = oldStatements.get(0);
        Value newObject = new LiteralImpl("this is a String");
        Statement newStatement = 
                new ContextStatementImpl(oldStatement.getSubject(), oldStatement.getPredicate(), newObject, minContext);
        holder.addStatement(newStatement);
        String result = Validator.validate(holder, minContext, specifications);
        assertThat(result, containsString(PropertyMetaData.EXPECTED_TYPE));
        assertThat(result,  endsWith(Validator.FAILED));
   }
    
    @Test
    public void dataTimeAsDateValidate() throws VoidValidatorException, DatatypeConfigurationException   {
        Reporter.println("dataTimeAsDateValidate");
        RdfHolder holder = new RdfHolder(minReader, minContext);
        List<Statement> oldStatements = 
                minReader.getStatementList(ALL_SUBJECTS, OpsTestConstants.HAS_BIRTHDATE, ALL_OBJECTS, minContext);
        Statement oldStatement = oldStatements.get(0);
        Value newObject = new LiteralImpl("2013-01-17", XsdType.DATE.asURI());
        Statement newStatement = 
                new ContextStatementImpl(oldStatement.getSubject(), oldStatement.getPredicate(), newObject, minContext);
        holder.addStatement(newStatement);
        String result = Validator.validate(holder, minContext, specifications);
        assertThat(result, containsString(PropertyMetaData.EXPECTED_TYPE));
        assertThat(result,  endsWith(Validator.FAILED));
   }
        
    @Test
    public void missingOneOfGroupValidate() throws VoidValidatorException   {
        Reporter.println("missingOneOfGroupValidate");
        RdfHolder holder = new RdfHolder(minReader, minContext);
        List<Statement> remove = 
                minReader.getStatementList(ALL_SUBJECTS, OpsTestConstants.HAS_HOUSE_NUMBER, ALL_OBJECTS, minContext);
        assertThat(remove.size(), greaterThan(0));
        holder.removeStatements(remove);
        String result = Validator.validate(holder, minContext, specifications);
        assertThat(result, containsString(MetaDataGroup.INCLUDE_ALL));       
        assertThat(result,  endsWith(Validator.FAILED));
    }

    @Test
    public void missingOneOfGroupButHaveAlternativeValidate() throws VoidValidatorException   {
        Reporter.println("missingOneOfGroupButHaveAlternativeValidate");
        RdfHolder holder = new RdfHolder(minReader, minContext);
        List<Statement> oldStatements = 
                minReader.getStatementList(ALL_SUBJECTS, OpsTestConstants.HAS_HOUSE_NUMBER, ALL_OBJECTS, minContext);
        holder.removeStatements(oldStatements);
        Statement oldStatement = oldStatements.get(0);
        Statement newStatement = new ContextStatementImpl(
                oldStatement.getSubject(), OpsTestConstants.HAS_HOUSE_NUMBER, oldStatement.getObject(), minContext);
        holder.addStatement(newStatement);       
        String result = Validator.validate(holder, minContext, specifications);
        assertThat(result,  endsWith(Validator.SUCCESS));
    }

    @Test
    public void missingLinkedType() throws VoidValidatorException   {
        Reporter.println("missingLinkedType");
        RdfHolder holder = new RdfHolder(minReader, minContext);
        List<Statement> remove = minReader.getStatementList(ALL_SUBJECTS, RdfConstants.TYPE_URI , OpsTestConstants.PERSON, minContext);
        assertThat(remove.size(), greaterThan(0));
        holder.removeStatements(remove);
          
        String result = Validator.validate(holder, minContext, specifications);
        assertThat(result,  endsWith(Validator.SUCCESS));
    }
    
    @Test
    public void missingLinkedTypeBad() throws VoidValidatorException   {
        Reporter.println("missingLinkedTypeBad");
        RdfHolder holder = new RdfHolder(minReader, minContext);
        List<Statement> remove = minReader.getStatementList(ALL_SUBJECTS, RdfConstants.TYPE_URI , OpsTestConstants.PERSON, minContext);
        assertThat(remove.size(), greaterThan(0));
        holder.removeStatements(remove);
        remove = 
                minReader.getStatementList(ALL_SUBJECTS, OpsTestConstants.HAS_HOUSE_NUMBER, ALL_OBJECTS, minContext);
        assertThat(remove.size(), greaterThan(0));
        holder.removeStatements(remove);
         
        String result = Validator.validate(holder, minContext, specifications);
        assertThat(result, containsString(LinkedResource.NO_KNOWN_TYPE));       
        assertThat(result,  endsWith(Validator.FAILED));
    }
 
}
