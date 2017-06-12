/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oathouse.oss.storage.objectstore;

import com.oathouse.oss.storage.objectstore.ObjectOrderSetStore;
import com.oathouse.oss.server.OssProperties;
import com.oathouse.oss.storage.objectstore.example.ExampleBean;
import com.oathouse.oss.storage.exceptions.NoSuchIdentifierException;
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
public class ObjectOrderSetStoreTest {

    private ObjectOrderSetStore<ExampleBean> manager;
    private String owner = "tester";
    private ConcurrentSkipListSet<ExampleBean> testStore;

    @Before
    public void setUp() throws Exception {
        String sep = File.separator;
        OssProperties props = OssProperties.getInstance();
        props.setHost("localhost");
        props.setPort(21021);
        props.setAuthority("TestAuth");
        manager = new ObjectOrderSetStore<>("ObjectOrderSetStoreTest");
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
        manager.init();
        manager.setObject(new ExampleBean(1, 101, "AA", owner));
        manager.setObject(new ExampleBean(7, 102, "BB", owner));
        manager.setObject(new ExampleBean(11, 103, "CC", owner));
        assertEquals(1, manager.getObjectAt(0).getTestId());
        assertEquals(7, manager.getObjectAt(1).getTestId());
        assertEquals(11, manager.getObjectAt(2).getTestId());
        manager.setFirstObject(new ExampleBean(11, 103, "CC", owner));
        assertEquals(11, manager.getObjectAt(0).getTestId());
        assertEquals(1, manager.getObjectAt(1).getTestId());
        assertEquals(7, manager.getObjectAt(2).getTestId());
        manager = null;
        manager = new ObjectOrderSetStore<ExampleBean>("ObjectOrderSetStoreTest");
        manager.init();
        assertEquals(11, manager.getObjectAt(0).getTestId());
        assertEquals(1, manager.getObjectAt(1).getTestId());
        assertEquals(7, manager.getObjectAt(2).getTestId());
        manager = null;
        manager = new ObjectOrderSetStore<ExampleBean>("ObjectOrderSetStoreTest");
        manager.init();
        assertEquals(11, manager.getObjectAt(0).getTestId());
        assertEquals(1, manager.getObjectAt(1).getTestId());
        assertEquals(7, manager.getObjectAt(2).getTestId());


    }

    @Test
    public void testGetAllObjects() throws Exception {
        manager.init();
        manager.setObject(new ExampleBean(1, 101, "AA", owner));
        manager.setObject(new ExampleBean(7, 102, "BB", owner));
        manager.setObject(new ExampleBean(11, 103, "CC", owner));
        assertEquals(false, manager.getAllObjects().isEmpty());
        assertEquals(3, manager.getAllObjects().size());
    }

    /**
     * Test of getObject method, of class ObjectManager.
     */
    @Test
    public void testGetObject() throws Exception {
        manager.init();
        manager.setObject(new ExampleBean(3, 101, "AA", owner));
        manager.setObject(new ExampleBean(4, 102, "AB", owner));
        manager.setObject(new ExampleBean(5, 103, "AC", owner));
        // create a test store
        testStore = null;
        testStore = new ConcurrentSkipListSet<ExampleBean>();
        testStore.add(new ExampleBean(3, 101, "AA", owner));
        testStore.add(new ExampleBean(4, 102, "AB", owner));
        testStore.add(new ExampleBean(5, 103, "AC", owner));
        int counter = 0;
        for(ExampleBean e : testStore) {
            assertEquals(manager.getObject(e.getTestId()), e);
            counter++;
        }
        assertEquals(3, counter);
        try {
            manager.getObject(1);
            fail("Should throw an exception");
        } catch(NoSuchIdentifierException ex) {
            // success
        }
    }

    /**
     * Test of method, of class Objectmanager.
     */
    @Test
    public void testGetObjectAt() throws Exception {
        manager.init();
        manager.setLastObject(new ExampleBean(3, 101, "AA", owner));
        manager.setFirstObject(new ExampleBean(4, 102, "AB", owner));
        manager.setObjectAt(new ExampleBean(5, 103, "AC", owner), 1);
        assertEquals(4, manager.getFirstObject().getTestId());
        assertEquals(5, manager.getObjectAt(1).getTestId());
        assertEquals(3, manager.getLastObject().getTestId());
    }
}
