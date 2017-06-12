/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oathouse.oss.storage.objectstore;

import com.oathouse.oss.server.OssProperties;
import com.oathouse.oss.storage.exceptions.NullObjectException;
import com.oathouse.oss.storage.exceptions.PersistenceException;
import java.util.LinkedList;
import com.oathouse.oss.storage.exceptions.ObjectStoreException;
import com.oathouse.oss.storage.objectstore.example.ExampleBean;
import java.util.Vector;
import com.oathouse.oss.storage.exceptions.NoSuchIdentifierException;
import java.io.File;
import java.util.concurrent.ConcurrentSkipListMap;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Administrator
 */
public class ObjectOrderMapStoreTest {

    private ObjectOrderMapStore<ExampleBean> manager;
    private String owner = "tester";
    private ConcurrentSkipListMap<Integer, ConcurrentSkipListMap<Integer, ExampleBean>> testStore;

    @Before
    public void setUp() throws Exception {
        String sep = File.separator;
        OssProperties props = OssProperties.getInstance();
        props.setHost("localhost");
        props.setPort(21021);
        props.setAuthority("TestAuth");
        props.setLogConfigFile("." + File.separator + "oss_log4j.properties");
        manager = new ObjectOrderMapStore<ExampleBean>("ObjectOrderMapStoreTest");
        manager.init();
        manager.clear();
    }

    @After
    public void tearDown() throws Exception {
        manager.shutdown();
    }

    /**
     * Test of init method, of class ObjectManager.
     */
    @Test
    public void testInit() throws Exception {
        Vector<Integer> order = new Vector<Integer>();
        order.add(3);
        order.add(4);
        order.add(5);
        ObjectOrderBean orderBean = new ObjectOrderBean(1, order, owner);
        manager.init();
        manager.setObject(0, new ExampleBean(0, 0, "", owner));
        manager.setObject(1, new ExampleBean(3, 101, "AA", owner));
        manager.setObject(1, new ExampleBean(4, 102, "AB", owner));
        manager.setObject(1, new ExampleBean(5, 103, "AC", owner));
        assertEquals(3, manager.getObjectAt(1, 0).getTestId());
        assertEquals(4, manager.getObjectAt(1, 1).getTestId());
        assertEquals(5, manager.getObjectAt(1, 2).getTestId());
        manager.setFirstObject(1, new ExampleBean(5, 103, "AC", owner));
        assertEquals(5, manager.getObjectAt(1, 0).getTestId());
        assertEquals(3, manager.getObjectAt(1, 1).getTestId());
        assertEquals(4, manager.getObjectAt(1, 2).getTestId());
        manager = null;
        manager = new ObjectOrderMapStore<ExampleBean>("ObjectOrderMapStoreTest");
        manager.init();
        assertEquals(5, manager.getObjectAt(1, 0).getTestId());
        assertEquals(3, manager.getObjectAt(1, 1).getTestId());
        assertEquals(4, manager.getObjectAt(1, 2).getTestId());
        manager = null;
        manager = new ObjectOrderMapStore<ExampleBean>("ObjectOrderMapStoreTest");
        manager.init();
        assertEquals(5, manager.getObjectAt(1, 0).getTestId());
        assertEquals(3, manager.getObjectAt(1, 1).getTestId());
        assertEquals(4, manager.getObjectAt(1, 2).getTestId());
    }

    /**
     * Test of getObject method, of class ObjectManager.
     */
    @Test
    public void testGetObject() throws Exception {
        manager.init();
        manager.setObject(0, new ExampleBean(0, 0, "", owner));
        manager.setObject(1, new ExampleBean(3, 101, "AA", owner));
        manager.setObject(1, new ExampleBean(4, 102, "AB", owner));
        manager.setObject(1, new ExampleBean(5, 103, "AC", owner));
        // create a test store
        testStore = null;
        testStore = new ConcurrentSkipListMap<Integer, ConcurrentSkipListMap<Integer, ExampleBean>>();
        testStore.put(0, new ConcurrentSkipListMap<Integer, ExampleBean>());
        testStore.put(1, new ConcurrentSkipListMap<Integer, ExampleBean>());
        testStore.get(0).put(0, new ExampleBean(0, 0, "", owner));
        testStore.get(1).put(3, new ExampleBean(3, 101, "AA", owner));
        testStore.get(1).put(4, new ExampleBean(4, 102, "AB", owner));
        testStore.get(1).put(5, new ExampleBean(5, 103, "AC", owner));
        assertEquals(2, manager.getAllKeys().size());
        int counter = 0;
        for(int key : testStore.keySet()) {
            for(int id : testStore.get(key).keySet()) {
                assertEquals(manager.getObject(key, id), testStore.get(key).get(id));
                counter++;
            }
        }
        assertEquals(4, counter);
        try {
            manager.getObject(2, 1);
            fail("Should throw an exception");
        } catch(NoSuchIdentifierException ex) {
            // success
        }
        try {
            manager.getObject(1, 2);
            fail("Should throw an exception");
        } catch(NoSuchIdentifierException ex) {
            // success
        }
    }

    @Test
    public void testSetObjectAt() throws ObjectStoreException {
        manager.init();
        ExampleBean b1 =new ExampleBean(3, 101, "AA", owner);
        ExampleBean b2 =new ExampleBean(4, 102, "AB", owner);
        manager.setObjectAt(1, b1, 0);
        manager.setObjectAt(1, b2, 0);
        assertEquals(b2, ((LinkedList<ExampleBean>) manager.getAllObjects(1)).getFirst());
        assertEquals(b1, ((LinkedList<ExampleBean>) manager.getAllObjects(1)).getLast());
    }

    /**
     * Test of method, of class ObjectOrderManager.
     */
    @Test
    public void testGetObjectAt() throws Exception {
        manager.init();
        manager.setLastObject(0, new ExampleBean(0, 0, "", owner));
        manager.setLastObject(1, new ExampleBean(3, 101, "AA", owner));
        manager.setFirstObject(1, new ExampleBean(4, 102, "AB", owner));
        manager.setObjectAt(1, new ExampleBean(5, 103, "AC", owner), 1);
        assertEquals(4, manager.getFirstObject(1).getTestId());
        assertEquals(5, manager.getObjectAt(1, 1).getTestId());
        assertEquals(3, manager.getLastObject(1).getTestId());
    }

    @Test
    public void testSetKey() throws Exception {
        manager.init();
        manager.setObject(0, new ExampleBean(0, 0, "", owner));
        manager.setObject(1, new ExampleBean(3, 101, "AA", owner));
        manager.setObject(1, new ExampleBean(4, 102, "AB", owner));
        manager.setObject(1, new ExampleBean(5, 103, "AC", owner));
        assertEquals(3, manager.getAllObjects(1).size());
        manager.setKey(1);
        assertEquals(3, manager.getAllObjects(1).size());
        manager.setKey(3);
        assertEquals(0, manager.getAllObjects(3).size());
        assertTrue(manager.getAllKeys().contains(3));
    }

    /**
     * Test of method, of class ObjectOrderManager.
     */
    @Test
    public void testGetAllObjects() throws PersistenceException, NullObjectException {
        manager.init();
        manager.setObject(0, new ExampleBean(0, 0, "", owner));
        manager.setObject(1, new ExampleBean(3, 101, "AA", owner));
        manager.setObject(1, new ExampleBean(4, 102, "AB", owner));
        manager.setObject(1, new ExampleBean(5, 103, "AC", owner));


    }
}
