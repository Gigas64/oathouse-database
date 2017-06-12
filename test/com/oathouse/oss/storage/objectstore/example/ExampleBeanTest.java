/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.oathouse.oss.storage.objectstore.example;

import com.oathouse.oss.storage.objectstore.BeanBuilder;
import com.oathouse.oss.storage.objectstore.ObjectBean;
import com.oathouse.oss.storage.objectstore.example.ExampleBean;
import java.util.HashMap;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Darryl
 */
public class ExampleBeanTest {

    @Test
    public void testXMLDOM() {
        ExampleBean bean = new ExampleBean(1, 101, "TestBean", "tester");
        String xml = bean.toXML();
        ExampleBean other = new ExampleBean();
        other.setXMLDOM(bean.getXMLDOM().detachRootElement());
        assertEquals(bean, other);
        assertEquals(xml, other.toXML());
    }

    @Test
    public void showXml() {
        ExampleBean bean = new ExampleBean(1, 101, "TestBean", "tester");
        System.out.println(bean.toXML());
    }
}