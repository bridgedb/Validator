/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.manchester.cs.datadesc.validator.bean;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Christian
 */
@XmlRootElement(name="Test")
public class TestBean {
    
    @XmlElement(name="Name")
    private String name;
    
    @XmlElement(name="years")
    private int age;

    public TestBean(){
        
    }
    public TestBean(String newName, int count){
        name = newName;
        age = count;
    }
    
    public String toString(){
        return "name = " + name + " age = " + age;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the age
     */
    public int getAge() {
        return age;
    }
}
