/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.manchester.cs.datadesc.validator.bean;

import java.io.StringWriter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import org.codehaus.jettison.json.JSONObject;
import org.codehaus.jettison.mapped.Configuration;
import org.codehaus.jettison.mapped.MappedNamespaceConvention;
import org.codehaus.jettison.mapped.MappedXMLStreamReader;
import org.codehaus.jettison.mapped.MappedXMLStreamWriter;
/**
 *
 * @author Christian
 */
public class MyMarshaller {
    public static void main(String[] args) throws Exception {
        TestBean test = new TestBean("John", 45);
        JAXBContext jc = JAXBContext.newInstance(TestBean.class);
        Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);        
 //       marshaller.marshal(test, System.out);
        StringWriter theWriter = new StringWriter();
        Configuration theConfig = new Configuration();
        MappedNamespaceConvention theConvention = new MappedNamespaceConvention(theConfig);
        XMLStreamWriter theXMLWriter = new MappedXMLStreamWriter(theConvention, theWriter);
        marshaller.marshal(test, theXMLWriter);
        System.out.println(theWriter.toString());
        
        XMLStreamReader theXMLReader = new MappedXMLStreamReader(new JSONObject(theWriter.toString()), theConvention);
        Unmarshaller theUnmarshaller = jc.createUnmarshaller();
        TestBean theResult = (TestBean) theUnmarshaller.unmarshal(theXMLReader);
        System.out.println(theResult);
    }
}
