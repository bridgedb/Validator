/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.manchester.cs.metadata;

import java.io.File;
import java.util.GregorianCalendar;
import java.util.List;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.ac.manchester.cs.rdftools.RdfHolder;
import uk.ac.manchester.cs.rdftools.RdfInterface;
import uk.ac.manchester.cs.rdftools.RdfReader;
import uk.ac.manchester.cs.rdftools.Reporter;
import uk.ac.manchester.cs.rdftools.VoidValidatorException;
import uk.ac.manchester.cs.validator.Validator;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.CalendarLiteralImpl;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import uk.ac.manchester.cs.constants.PavConstants;
import uk.ac.manchester.cs.constants.VoidConstants;
import uk.ac.manchester.cs.metadata.type.XsdType;

/**
 *
 * @author Christian
 */
public class ValidationTest {
    
    static RdfReader minReader;
    static MetaDataSpecification specifications;
   
    public ValidationTest() {
        
    }
    
    @BeforeClass
    public static void setUpClass() throws VoidValidatorException {
        File file = new File ("test-data/testMin.ttl");
        minReader = new RdfReader(file);
        specifications = new MetaDataSpecification();
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
        assertThat(result,  endsWith(Validator.SUCCESS));
    }
    
   @Test
    public void noDataDumpValidate() throws VoidValidatorException   {
        Reporter.println("noDataDumpValidate");
        RdfHolder holder = new RdfHolder(minReader);
        holder.removeStatements(minReader.getStatementList(null, VoidConstants.DATA_DUMP, null));
        Validator validator = new Validator(holder, specifications);
        String result = validator.validate();
        assertThat(result, containsString(CardinalityMetaData.NOT_FOUND));
        assertThat(result,  endsWith(Validator.FAILED));
    }
   
    @Test
    public void noCreatedWithValidate() throws VoidValidatorException   {
        Reporter.println("noDataDumpValidate");
        RdfHolder holder = new RdfHolder(minReader);
        holder.removeStatements(minReader.getStatementList(null, PavConstants.CREATED_WITH, null));
        Validator validator = new Validator(holder, specifications);
        String result = validator.validate();
        System.out.println(result);
        assertThat(result, containsString(MetaDataAlternatives.INCLUDE_ALTERNATIVE));
        assertThat(result,  endsWith(Validator.FAILED));
    }

    @Test
    public void extraDataDumpValidate() throws VoidValidatorException   {
        Reporter.println("extraDataDumpValidate");
        RdfHolder holder = new RdfHolder(minReader);
        Validator validator = new Validator(holder, specifications);
        String result = validator.validate();
        assertThat(result, containsString(ResourceMetaData.NO_ERRORS));
        assertThat(result,  endsWith(Validator.SUCCESS));
        List<Statement> dumps = minReader.getStatementList(null, VoidConstants.DATA_DUMP, null);
        Statement dump = dumps.get(0);
        URI newObject = new URIImpl(dump.getObject().stringValue() + "new");
        Statement newDump = new StatementImpl(dump.getSubject(), dump.getPredicate(), newObject);
        holder.addStatement(newDump);
        validator = new Validator(holder, specifications);
        String result2 = validator.validate();
        assertEquals(result, result2);
   }
    
    @Test
    public void extraUriSpaceValidate() throws VoidValidatorException   {
        Reporter.println("extraUriSpaceValidate");
        RdfHolder holder = new RdfHolder(minReader);
        List<Statement> uriSpaces = minReader.getStatementList(null, VoidConstants.URI_SPACE_URI, null);
        Statement uriSpace = uriSpaces.get(0);
        URI newObject = new URIImpl(uriSpace.getObject().stringValue() + "new");
        Statement newDump = new StatementImpl(uriSpace.getSubject(), uriSpace.getPredicate(), newObject);
        holder.addStatement(newDump);
        Validator validator = new Validator(holder, specifications);
        String result = validator.validate();
        assertThat(result, containsString(CardinalityMetaData.REMOVE));
        assertThat(result,  endsWith(Validator.FAILED));
   }

    @Test
    public void dataDumpAsStringValidate() throws VoidValidatorException   {
        Reporter.println("extraDataDumpValidate");
        RdfHolder holder = new RdfHolder(minReader);
        List<Statement> dumps = minReader.getStatementList(null, VoidConstants.DATA_DUMP, null);
        holder.removeStatements(dumps);
        Statement dump = dumps.get(0);
        Value newObject = new LiteralImpl("this is a String");
        Statement newDump = new StatementImpl(dump.getSubject(), dump.getPredicate(), newObject);
        holder.addStatement(newDump);
        Validator validator = new Validator(holder, specifications);
        String result = validator.validate();
        assertThat(result, containsString(PropertyMetaData.EXPECTED_TYPE));
        assertThat(result,  endsWith(Validator.FAILED));
   }
    
    @Test
    public void createdOnAsDateValidate() throws VoidValidatorException, DatatypeConfigurationException   {
        Reporter.println("createdOnAsDateValidate");
        RdfHolder holder = new RdfHolder(minReader);
        List<Statement> createdOns = minReader.getStatementList(null, PavConstants.CREATED_ON, null);
        Statement createdOn = createdOns.get(0);
        Value newObject = new LiteralImpl("2013-01-17", XsdType.DATE.asURI());
        Statement newCreatedOn = new StatementImpl(createdOn.getSubject(), createdOn.getPredicate(), newObject);
        holder.addStatement(newCreatedOn);
        Validator validator = new Validator(holder, specifications);
        String result = validator.validate();
        assertThat(result, containsString(PropertyMetaData.EXPECTED_TYPE));
        assertThat(result,  endsWith(Validator.FAILED));
   }
        

}
