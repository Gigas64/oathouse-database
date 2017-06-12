/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oathouse.oss.storage.objectstore;

// common imports
import com.oathouse.oss.server.OssProperties;
import com.oathouse.oss.storage.exceptions.NullObjectException;
import com.oathouse.oss.storage.exceptions.ObjectStoreException;
import com.oathouse.oss.storage.exceptions.PersistenceException;
import static com.oathouse.oss.storage.objectstore.ObjectDataOptionsEnum.*;
import com.oathouse.oss.storage.objectstore.example.ExampleBean;
import com.oathouse.oss.storage.valueholder.CalendarStatic;
import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import static org.hamcrest.CoreMatchers.*;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Darryl Oatridge
 */
public class ObjectDBMSTest {

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
        manager = new ObjectMapStore<>("ObjectDbmsManager", ARCHIVE);
        manager.init();
        manager.clear();
    }

    @After
    public void tearDown() throws Exception {
        manager.shutdown();
    }

    /*
     *
     */
    @Test
    public void testArchiveDateOnRemove() throws Exception {
        setExampleData();
        // test the archive date is not set (use key 1)
        int key = 1;
        // test the persistence key
        assertThat(manager.getAllObjects(key, PERSIST), is(manager.getAllObjects(key, ARCHIVE)));
        for(int id : manager.getAllIdentifier(key)) {
            assertThat(manager.getObject(key, id, PERSIST).getArchiveYwd(), is(ObjectEnum.DEFAULT_VALUE.value()));
            assertThat(manager.getObject(key, id, ARCHIVE).getArchiveYwd(), is(ObjectEnum.DEFAULT_VALUE.value()));
        }
        //archive off the whole key
        manager.removeKey(key);
        for(int id : manager.getAllIdentifier(key, ARCHIVE)) {
            assertThat(manager.getObject(key, id, ARCHIVE).getArchiveYwd(), is(CalendarStatic.getRelativeYWD(0)));
        }

        // archive of just one
        key = 2;
        int id = 3;
        assertThat(manager.getObject(key, id, PERSIST).getArchiveYwd(), is(ObjectEnum.DEFAULT_VALUE.value()));
        assertThat(manager.getObject(key, id, ARCHIVE).getArchiveYwd(), is(ObjectEnum.DEFAULT_VALUE.value()));
        manager.removeObject(key, id);
        assertThat(manager.getObject(key, id, ARCHIVE).getArchiveYwd(), is(CalendarStatic.getRelativeYWD(0)));
    }

    /*
     *
     */
    @Test
    public void testReinstate() throws Exception {
        int key = 1;
        setExampleData();
        assertEquals(true, manager.getAllKeys().contains(key));
        List<ExampleBean> control = manager.getAllObjects(key);
        assertEquals(true, manager.getAllKeys(ARCHIVE).contains(key));
        manager.removeKey(key);
        assertEquals(false, manager.getAllKeys().contains(key));
        assertEquals(true, manager.getAllKeys(ARCHIVE).contains(key));
        manager.reinstateArchiveKey(key);
        assertEquals(true, manager.getAllKeys().contains(key));
        assertEquals(true, manager.getAllKeys(ARCHIVE).contains(key));
        assertEquals(control, manager.getAllObjects(key));
        // try ordered
        manager = new ObjectMapStore<>("ObjectDbmsManager", ARCHIVE, ORDERED);
        manager.init();
        setExampleData();
        assertEquals(true, manager.getAllKeys().contains(key));
        control = manager.getAllObjects(key);
        assertEquals(true, manager.getAllKeys(ARCHIVE).contains(key));
        manager.removeKey(key);
        // test the archive date is set
        for(int id : manager.getAllIdentifier(key)) {
            assertThat(manager.getObject(key, id, ARCHIVE).getArchiveYwd(), is(CalendarStatic.getRelativeYWD(0)));
        }
        assertEquals(false, manager.getAllKeys().contains(key));
        assertEquals(true, manager.getAllKeys(ARCHIVE).contains(key));
        manager.reinstateArchiveKey(key);
        assertEquals(true, manager.getAllKeys().contains(key));
        assertEquals(true, manager.getAllKeys(ARCHIVE).contains(key));
        assertEquals(control, manager.getAllObjects(key));

        // test the archive date is reset
        for(int id : manager.getAllIdentifier(key)) {
            assertThat(manager.getObject(key, id).getArchiveYwd(), is(ObjectEnum.DEFAULT_VALUE.value()));
        }

    }

    /*
     *
     */
    @Test
    public void testReinstateFails() throws Exception {
        setExampleData();
        try {
            manager.reinstateArchiveKey(99);
            fail("Should throw exception as not valid key");
        } catch(PersistenceException persistenceException) {
            // success
        }
        try {
            manager.reinstateArchiveObject(99, 1);
            fail("Should throw exception as not valid key");
        } catch(PersistenceException persistenceException) {
            // success
        }
        try {
            manager.reinstateArchiveObject(1, 99);
            fail("Should throw exception as not valid id");
        } catch(PersistenceException persistenceException) {
            // success
        }
    }


    /*
     *
     */
    @Test
    public void testCypher() throws Exception {
        manager = new ObjectMapStore<>("ObjectDbmsManager", CIPHERED);
        manager.init();
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

    /*
     * try to change to cypher should not allow it.
     */
    @Test(expected=PersistenceException.class)
    public void testNonCipherPersistence() throws Exception {
        setExampleData();
        manager = new ObjectMapStore<>("ObjectDbmsManager", CIPHERED);
        manager.init();
    }



    @Test
    public void testGenerateId() throws ObjectStoreException {
        // get key when empty
        assertEquals(1, manager.generateIdentifier());
        //checkthe time interval works
        assertEquals(2, manager.generateIdentifier());
        assertEquals(3, manager.regenerateIdentifier());
        // now reset and try again
        manager.resetTimeReserveExclusions();
        assertEquals(1, manager.generateIdentifier());
        manager.resetTimeReserveExclusions();
        // set the example data
        setExampleData();

        assertEquals(1, manager.regenerateIdentifier());
        assertEquals(6, manager.generateIdentifier());
        manager.resetTimeReserveExclusions();
        manager.setObject(1, new ExampleBean(1, 333, "TEST", owner));
        assertEquals(6, manager.generateIdentifier());
        manager.resetTimeReserveExclusions();
        assertEquals(6, manager.regenerateIdentifier());
        manager.resetTimeReserveExclusions();
        assertEquals(-10, manager.regenerateIdentifier(-10, null, 0));
        assertEquals(6, manager.generateIdentifier(-10, 0));
        assertEquals(6, manager.generateIdentifier(ObjectEnum.MIN_RESERVED.value(), 0));
        assertEquals(6, manager.generateIdentifier(4, 0));
        assertEquals(6, manager.generateIdentifier(6, 0));
        assertEquals(7, manager.generateIdentifier(7, 0));
        Set<Integer> excludes = new ConcurrentSkipListSet<Integer>();
        for(int i = 4; i < 15; i++) {
            excludes.add(i);
        }
        assertEquals(15, manager.regenerateIdentifier(1, excludes, 0));
        excludes.remove(11);
        assertEquals(11, manager.regenerateIdentifier(1, excludes, 0));

    }

    /*
     *
     */
    @Test
    public void testGenerateIdTimeReservedId() throws Exception {
        //the default is 300 milliseconds reserve timeout
        assertEquals(1, manager.regenerateIdentifier());
        Thread.sleep(200);
        assertEquals(2, manager.regenerateIdentifier());
        assertEquals(3, manager.generateIdentifier());
        assertEquals(4, manager.regenerateIdentifier());
        Thread.sleep(200);
        assertEquals(1, manager.regenerateIdentifier());
        assertEquals(5, manager.generateIdentifier());
        Thread.sleep(400);
        assertEquals(1, manager.generateIdentifier());

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
