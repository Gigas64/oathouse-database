/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oathouse.oss.storage.objectstore;

import com.oathouse.oss.storage.objectstore.ObjectSetStore;
import com.oathouse.oss.server.OssProperties;
import com.oathouse.oss.storage.objectstore.example.ExampleBean;
import com.oathouse.oss.storage.exceptions.ObjectStoreException;
import java.util.LinkedList;
import com.oathouse.oss.storage.exceptions.NoSuchIdentifierException;
import com.oathouse.oss.storage.exceptions.PersistenceException;
import java.io.File;
import java.util.HashSet;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Administrator
 */
public class ObjectSetStoreTest {

    private ObjectSetStore<ExampleBean> manager;
    private String owner = "System";

    @Before
    public void setUp() throws Exception {
        String sep = File.separator;
        OssProperties props = OssProperties.getInstance();
        props.setHost("localhost");
        props.setPort(21021);
        props.setAuthority("TestAuth");
        manager = new ObjectSetStore<ExampleBean>("ObjectSetStoreTest");
        manager.init();
        manager.setObject(new ExampleBean(3, 101, "AA", owner));
        manager.setObject(new ExampleBean(4, 102, "AB", owner));
        manager.setObject(new ExampleBean(5, 103, "AC", owner));

    }

    @After
    public void tearDown() throws Exception {
        manager.clear();
    }

    /**
     * Test of getObject method, of class ObjectSetStore.
     */
    @Test
    public void testGetObject() throws Exception {
        ExampleBean test = new ExampleBean(3, 101, "AA", owner);
        assertEquals(test, manager.getObject(3));
        manager = null;
        manager = new ObjectSetStore<ExampleBean>("ObjectSetStoreTest");
        try {
            manager.getObject(3);
            fail("should throw an Initalisation exception");
        } catch(PersistenceException e) {
            // success
        }
        manager.init();
        assertEquals(test, manager.getObject(3));

        // get a number that doesn't exist
        try {
            manager.getObject(123);
            fail("Should throw an NoSuchItdentifier exception");
        } catch(NoSuchIdentifierException ex) {
            // success
        }
    }

    /**
     * Test of getAllObjects method, of class ObjectSetStore.
     */
    @Test
    public void testGetAllObjects() throws Exception {
        LinkedList<ExampleBean> test = new LinkedList<ExampleBean>();
        test.add(new ExampleBean(3, 101, "AA", owner));
        test.add(new ExampleBean(4, 102, "AB", owner));
        test.add(new ExampleBean(5, 103, "AC", owner));
        assertEquals(test, manager.getAllObjects());
        manager = null;
        manager = new ObjectSetStore<ExampleBean>("ObjectSetStoreTest");
        try {
            manager.getAllObjects();
            fail("should throw an initalisation exception");
        } catch(PersistenceException e) {
            // success
        }
        manager.init();
        assertEquals(test, manager.getAllObjects());
        // try empty
        manager.clear();
        test.clear();
        assertEquals(test, manager.getAllObjects());
    }

    /**
     * Test of getAllIdentifier method, of class ObjectSetStore.
     */
    @Test
    public void testGetAllIdentifier() throws Exception {
        HashSet<Integer> test = new HashSet<Integer>();
        test.add(3);
        test.add(4);
        test.add(5);
        assertEquals(test, manager.getAllIdentifier());
        manager = null;
        manager = new ObjectSetStore<ExampleBean>("ObjectSetStoreTest");
        try {
            manager.getAllObjects();
            fail("should throw an initalisation exception");
        } catch(PersistenceException e) {
            // success
        }
        manager.init();
        assertEquals(test, manager.getAllIdentifier());
    }

    /**
     * Test of isIdentifier method, of class ObjectSetStore.
     */
    @Test
    public void testIsIdentifier() throws Exception {
        assertTrue(manager.isIdentifier(3));
        assertFalse(manager.isIdentifier(-1));
        assertFalse(manager.isIdentifier(6));
    }

    /**
     * Test of setObject method, of class ObjectSetStore.
     */
    @Test
    public void testSetObject() throws Exception {
        long create = manager.getObject(3).getCreated();
        long modified = manager.getObject(3).getModified();
        String owner = manager.getObject(3).getOwner();
        ExampleBean test = new ExampleBean(3, 999, "ZZZ", "test");
        assertEquals(3, manager.getAllObjects().size());
        manager.setObject(test);
        assertEquals(3, manager.getAllObjects().size());
        assertEquals(test, manager.getObject(3));
        assertEquals(create, manager.getObject(3).getCreated());
        assertNotSame(modified, manager.getObject(3).getModified());
        assertEquals("test", manager.getObject(3).getOwner());
    }

    /**
     * Test of removeObject method, of class ObjectSetStore.
     */
    @Test
    public void testRemoveObject() throws Exception {
        LinkedList<ExampleBean> test = new LinkedList<ExampleBean>();
        test.add(new ExampleBean(3, 101, "AA", owner));
        test.add(new ExampleBean(4, 102, "AB", owner));
        manager.removeObject(5);
        assertEquals(test, manager.getAllObjects());
        try {
            manager.removeObject(2);
            // success
        } catch(PersistenceException e) {
            fail("should not throw a Persistence exception.");
        }

    }

    @Test
    public void testXMLDOM() throws Exception {
        String xml = manager.getObject(3).toXML();
        ExampleBean other = new ExampleBean();
        other.setXMLDOM(manager.getObject(3).getXMLDOM().detachRootElement());
        assertEquals(manager.getObject(3), other);
        assertEquals(xml, other.toXML());
    }

    @Test
    public void testNoPersistence() throws ObjectStoreException {
        manager.clear();
        manager = null;
        manager = new ObjectSetStore<ExampleBean>();
        manager.init();
        manager.setObject(new ExampleBean(1, 10, "name1", "tester"));
        manager.setObject(new ExampleBean(2, 11, "name2", "tester"));

        assertEquals(2,manager.getAllObjects().size());
        manager = null;
        manager = new ObjectSetStore<ExampleBean>("ObjectSetStoreTest");
        manager.init();
        assertEquals(0,manager.getAllObjects().size());

    }
}
