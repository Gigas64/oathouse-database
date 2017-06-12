/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oathouse.oss.storage.objectstore;

import java.util.HashMap;
import com.oathouse.oss.server.OssProperties;
import com.oathouse.oss.storage.objectstore.example.ExampleBean;
import java.util.Set;
import com.oathouse.oss.storage.exceptions.ObjectStoreException;
import java.util.LinkedList;
import java.util.HashSet;
import com.oathouse.oss.storage.exceptions.NoSuchIdentifierException;
import com.oathouse.oss.storage.exceptions.NullObjectException;
import com.oathouse.oss.storage.exceptions.PersistenceException;
import java.io.File;
import java.util.concurrent.ConcurrentSkipListSet;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Darryl
 */
public class ObjectMapStoreTest {

    private ObjectMapStore<ExampleBean> manager;
    private String owner = ObjectBean.SYSTEM_OWNED;

    @Before
    public void setUp() throws Exception {
        OssProperties props = OssProperties.getInstance();
        props.setHost("localhost");
        props.setPort(21021);
        props.setAuthority(owner);
        props.setLogConfigFile("." + File.separator + "oss_log4j.properties");
        ObjectDBMS.clearAuthority(owner);
        manager = new ObjectMapStore<>("ObjectMapStoreManager");
        manager.init();
        manager.clear();
    }

    @After
    public void tearDown() throws Exception {
        manager.shutdown();
    }

    /**
     * System test:
     */
    @Test
    public void system01_getManagers() throws Exception {
        // set the example data
        setExampleData();

        // create bean
        int id = 0;
        HashMap<String, String> fieldSet = new HashMap<String, String>();
        fieldSet.put("id", Integer.toString(++id));
        fieldSet.put("owner", owner);
        ExampleBean bean01 = (ExampleBean) BeanBuilder.addBeanValues(new ExampleBean(), id, fieldSet);

        Set<String> control = new ConcurrentSkipListSet<String>();
        control.add("ObjectMapStoreManager");
        ObjectMapStore<ExampleBean> manager1 = new ObjectMapStore<ExampleBean>("OneManager");
        manager1.init();
        manager1.setObject(0, bean01);
        control.add("OneManager");
        ObjectMapStore<ExampleBean> manager2 = new ObjectMapStore<ExampleBean>("TwoManager");
        manager2.init();
        manager2.setObject(0, bean01);
        control.add("TwoManager");
        ObjectMapStore<ExampleBean> manager3 = new ObjectMapStore<ExampleBean>("ThreeManager");
        manager3.init();
        manager3.setObject(0, bean01);
        control.add("ThreeManager");
        ObjectMapStore<ExampleBean> manager4 = new ObjectMapStore<ExampleBean>("FourManager");
        manager4.init();
        manager4.setObject(0, bean01);
        control.add("FourManager");

        Set<String> result = ObjectDBMS.getAllManagers(OssProperties.getInstance().getAuthority());
        assertEquals(control.size(), result.size());
        assertEquals(control,result);
    }

    /**
     * Test of getObject method, of class ObjectMapStore.
     */
    @Test
    public void testGetObject() throws Exception {
        // set the example data
        setExampleData();

        ExampleBean test = new ExampleBean(3, 101, "AA", owner);
        assertEquals(test, manager.getObject(1, 3));
        manager = null;
        manager = new ObjectMapStore<ExampleBean>("ObjectMapStoreManager");
        try {
            manager.getObject(1, 3);
            fail("should throw an Initalisation exception");
        } catch(PersistenceException e) {
            // success
        }
        manager.init();
        assertEquals(test, manager.getObject(1, 3));

        // get a number that doesn't exist
        try {
            manager.getObject(1, 123);
            fail("Should throw an NoSuchItdentifier exception");
        } catch(NoSuchIdentifierException ex) {
            // success
        }
        try {
            manager.getObject(0, 3);
            fail("Should throw an NoSuchItdentifier exception");
        } catch(NoSuchIdentifierException ex) {
            // success
        }
        try {
            manager.getObject(10, 3);
            fail("Should throw an NoSuchItdentifier exception");
        } catch(NoSuchIdentifierException ex) {
            // success
        }

        // check the getObject where the key is unknown
        test = new ExampleBean(4, 102, "AB", owner);
        assertEquals(test, manager.getObject(4));
        test = new ExampleBean(3, 101, "AA", owner);
        assertEquals(test, manager.getObject(3));

    }

    /**
     * Test of getObjectKey method, of class ObjectMapStore.
     */
    @Test
    public void testGetAllKeysFromIntetifier() throws Exception {
        // set the example data
        setExampleData();

        assertEquals(1, manager.getAllKeysForIdentifier(2).size());
        assertTrue(manager.getAllKeysForIdentifier(2).contains(0));

        assertEquals(2, manager.getAllKeysForIdentifier(3).size());
        assertTrue(manager.getAllKeysForIdentifier(3).contains(1));
        assertTrue(manager.getAllKeysForIdentifier(3).contains(2));

        assertEquals(1, manager.getAllKeysForIdentifier(4).size());
                assertTrue(manager.getAllKeysForIdentifier(4).contains(1));

        assertEquals(2, manager.getAllKeysForIdentifier(5).size());
        assertTrue(manager.getAllKeysForIdentifier(5).contains(1));
        assertTrue(manager.getAllKeysForIdentifier(5).contains(2));
    }

    /**
     * Test of getAllObjects method, of class ObjectMapStore.
     */
    @Test
    public void testGetAllObjects() throws Exception {
        // set the example data
        setExampleData();

        LinkedList<ExampleBean> keyTest = new LinkedList<ExampleBean>();
        LinkedList<ExampleBean> allTest = new LinkedList<ExampleBean>();

        keyTest.add(new ExampleBean(3, 101, "AA", owner));
        keyTest.add(new ExampleBean(4, 102, "AB", owner));
        keyTest.add(new ExampleBean(5, 103, "AC", owner));

        allTest.addAll(keyTest);
        allTest.add(new ExampleBean(2, 11, "Z", owner));
        allTest.add(new ExampleBean(3, 102, "AC", owner));
        allTest.add(new ExampleBean(5, 103, "AC", owner));

        assertEquals(1, manager.getAllObjects(0).size());
        assertEquals(3, manager.getAllObjects(1).size());
        assertEquals(2, manager.getAllObjects(2).size());
        assertEquals(0, manager.getAllObjects(3).size());
        assertEquals(keyTest, manager.getAllObjects(1));
        assertEquals(6, manager.getAllObjects().size());
        for(ExampleBean bean : manager.getAllObjects()) {
            assertTrue(allTest.contains(bean));
        }
        manager = null;
        manager = new ObjectMapStore<ExampleBean>("ObjectMapStoreManager");
        try {
            manager.getAllObjects(1);
            fail("should throw an initalisation exception");
        } catch(PersistenceException e) {
            // success
        }
        manager.init();
        assertEquals(keyTest, manager.getAllObjects(1));
        for(ExampleBean bean : manager.getAllObjects()) {
            assertTrue(allTest.contains(bean));
        }
        // try empty
        manager.clear();
        keyTest.clear();
        assertEquals(0, manager.getAllObjects(1).size());
        assertEquals(0, manager.getAllObjects(3).size());


    }

    /**
     * Test of getAllKeys method, of class ObjectMapStore.
     */
    @Test
    public void testGetAllKeys() throws Exception {
        // set the example data
        setExampleData();

        HashSet<Integer> test = new HashSet<Integer>();
        test.add(0);
        test.add(1);
        test.add(2);
        assertEquals(test, manager.getAllKeys());
        test.clear();
        manager.clear();
        assertEquals(test, manager.getAllKeys());
    }

    /**
     * Test of getAllIdentifier method, of class ObjectMapStore.
     */
    @Test
    public void testGetAllIdentifier() throws Exception {
        // set the example data
        setExampleData();

        HashSet<Integer> test = new HashSet<Integer>();
        test.add(2);
        test.add(3);
        test.add(4);
        test.add(5);
        assertEquals(test, manager.getAllIdentifier());
        test.remove(2);
        assertEquals(test, manager.getAllIdentifier(1));
        test.clear();
        test.add(2);
        assertEquals(test, manager.getAllIdentifier(0));
    }

    /**
     * Test of isIdentifier method, of class ObjectMapStore.
     */
    @Test
    public void testIsIdentifier() throws Exception {
        // set the example data
        setExampleData();

        assertTrue(manager.isIdentifier(2));
        assertTrue(manager.isIdentifier(3));
        assertFalse(manager.isIdentifier(1));
        assertTrue(manager.isIdentifier(0, 2));
        assertTrue(manager.isIdentifier(1, 3));
        assertFalse(manager.isIdentifier(0, 3));
        assertFalse(manager.isIdentifier(1, 2));

    }

    /**
     * Test of setObject method, of class ObjectMapStore.
     */
    @Test
    public void testSetObject() throws Exception {
        // set the example data
        setExampleData();

        long create = manager.getObject(1, 3).getCreated();
        long modified = manager.getObject(1, 3).getModified();
        String owner = manager.getObject(1, 3).getOwner();
        ExampleBean test = new ExampleBean(3, 999, "ZZZ", owner);
        assertEquals(3, manager.getAllObjects(1).size());
        manager.setObject(1, test);
        assertEquals(3, manager.getAllObjects(1).size());
        assertEquals(test, manager.getObject(1, 3));
        assertEquals(create, manager.getObject(1, 3).getCreated());
        assertNotSame(modified, manager.getObject(1, 3).getModified());
        assertEquals(owner, manager.getObject(1, 3).getOwner());

    }

    /**
     * Test of removeObject method, of class ObjectMapStore.
     */
    @Test
    public void testRemoveObject() throws Exception {
        // set the example data
        setExampleData();

        LinkedList<ExampleBean> test = new LinkedList<ExampleBean>();
        test.add(new ExampleBean(3, 101, "AA", owner));
        test.add(new ExampleBean(4, 102, "AB", owner));
        manager.removeObject(1, 5);
        assertEquals(test, manager.getAllObjects(1));
        assertFalse(manager.getAllIdentifier(1).contains(5));
        try {
            manager.removeObject(1, 2);
            // success
        } catch(PersistenceException e) {
            fail("should not throw a Persistence exception.");
        }
        try {
            manager.removeObject(3, 3);
        } catch(PersistenceException e) {
            fail("should not throw a Persistence exception.");
        }
    }

    /**
     * Test of removeKey method, of class ObjectMapStore.
     */
    @Test
    public void testRemoveKey() throws Exception {
        // set the example data
        setExampleData();

        manager.removeKey(1);
        assertTrue(manager.getAllObjects(1).isEmpty());

    }

    @Test
    public void testXMLDOM() throws Exception {
        // set the example data
        setExampleData();

        String xml = manager.getObject(1, 3).toXML();
        ExampleBean other = new ExampleBean();
        other.setXMLDOM(manager.getObject(1, 3).getXMLDOM().detachRootElement());
        assertEquals(manager.getObject(1, 3), other);
        assertEquals(xml, other.toXML());
    }

    @Test
    public void testNoPersistence() throws ObjectStoreException {
        // set the example data
        setExampleData();

        manager.clear();
        manager = null;
        manager = new ObjectMapStore<ExampleBean>();
        manager.init();

        manager.setObject(1, new ExampleBean(1, 10, "name1", "tester"));
        manager.setObject(1, new ExampleBean(2, 11, "name2", "tester"));

        assertEquals(2, manager.getAllObjects(1).size());
        manager = null;
        OssProperties props = OssProperties.getInstance();
        props.setHost("localhost");
        props.setPort(21021);
        props.setAuthority("TestAuth");
        manager = new ObjectMapStore<ExampleBean>("ObjectMapStoreManager");
        manager.init();
        assertEquals(0, manager.getAllObjects(1).size());
    }

    /**
     * Unit test:
     */
    @Test
    public void unit01_getAllKeysInMap() throws Exception {
        // set the example data
        setExampleData();
        assertEquals(3, manager.getAllKeys().size());
        manager = new ObjectMapStore<ExampleBean>("ObjectMapStoreManager", ObjectDataOptionsEnum.PERSIST);
        manager.init();
        assertEquals(3, manager.getAllKeys().size());

    }

    /**
     * Unit test:
     */
    @Test
    public void unit02_CloneObjectBean() throws Exception {
        ExampleBean bean = new ExampleBean(2, 11, "Z", owner);
        ExampleBean clone = manager.cloneObjectBean(bean.getIdentifier(), bean);
        assertEquals(clone, bean);
        assertFalse(bean == clone);
        //change ID
        clone = manager.cloneObjectBean(13, bean);
        assertEquals(13, clone.getExampleId());
        assertEquals(11, clone.getValue());
        assertEquals("Z", clone.getName());

    }

    private void setExampleData() throws PersistenceException, NullObjectException {
        manager.setObject(0, new ExampleBean(2, 11, "Z", owner));
        manager.setObject(1, new ExampleBean(3, 101, "AA", owner));
        manager.setObject(1, new ExampleBean(4, 102, "AB", owner));
        manager.setObject(1, new ExampleBean(5, 103, "AC", owner));
        manager.setObject(2, new ExampleBean(3, 102, "AC", owner));
        manager.setObject(2, new ExampleBean(5, 103, "AC", owner));

    }
}
