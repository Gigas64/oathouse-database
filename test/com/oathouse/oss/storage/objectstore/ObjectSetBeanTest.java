/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.oathouse.oss.storage.objectstore;

import java.util.concurrent.ConcurrentSkipListMap;
import java.util.Map;
import com.oathouse.oss.storage.objectstore.example.ExampleInheritBean;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Administrator
 */
public class ObjectSetBeanTest {

    private ExampleInheritBean testBean;
    private Set<Integer> testStore;
    private Map<Integer, String> refName;
    private boolean[] days = BeanBuilder.getDays(BeanBuilder.MON_ONLY);

    @Before
    public void setUp() {
        testStore = new ConcurrentSkipListSet<Integer>();
        // set up ttOne
        testStore.add(3);
        testStore.add(5);
        testStore.add(7);
        testStore.add(11);
        testStore.add(0);
        testStore.add(-1);
        refName = new ConcurrentSkipListMap<Integer, String>();
        refName.put(1, "Fred");
        refName.put(2, "Jim");
        refName.put(3, "Bob");
        refName.put(4, "Jane");

        testBean = new ExampleInheritBean(1, "name", "label", testStore, 101, "refName", days, refName, "Tester");
    }

    /**
     * Test of getPeriod method, of class EducationTimetableBean.
     */
    @Test
    public void testGets() {
        assertEquals("name", testBean.getName());
        assertEquals("label", testBean.getLabel());
        assertEquals(testStore, testBean.getValueStore());
    }

    @Test
    public void testExtream() {
        testBean = new ExampleInheritBean(2, null, null, null,102,null, days, null,"Tester");
        assertEquals("", testBean.getName());
        assertEquals("", testBean.getLabel());
        assertEquals(true, testBean.getValueStore().isEmpty());
    }

    @Test
    public void testXMLDOM() {
        String xml = testBean.toXML();
        ExampleInheritBean other = new ExampleInheritBean(1, "A", "BB", testStore, 101, "myReference", days, refName, "tester");
        other.setXMLDOM(testBean.getXMLDOM().detachRootElement());
        assertEquals(testBean, other);
        assertEquals(xml, other.toXML());
    }
}