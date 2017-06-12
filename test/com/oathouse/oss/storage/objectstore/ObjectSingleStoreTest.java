/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.oathouse.oss.storage.objectstore;

import com.oathouse.oss.storage.objectstore.ObjectEnum;
import com.oathouse.oss.storage.objectstore.ObjectSingleStore;
import com.oathouse.oss.server.OssProperties;
import org.junit.After;
import com.oathouse.oss.storage.objectstore.example.ExampleBean;
import java.io.File;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Administrator
 */
public class ObjectSingleStoreTest {
    private ObjectSingleStore<ExampleBean> manager;

    @Before
    public void setUp() throws Exception {
        String sep = File.separator;
        OssProperties props = OssProperties.getInstance();
        props.setHost("localhost");
        props.setPort(21021);
        props.setAuthority("TestAuth");
        props.setConnection(OssProperties.Connection.FILE);
        manager = new ObjectSingleStore<ExampleBean>("ObjectSingleStoreTest");
        manager.init();
    }

    @After
    public void tearDown() throws Exception {
        manager.clear();
    }

    /**
     * Test of getObject method, of class ObjectSingleStore.
     */
    @Test
    public void testGetAndSetObject() throws Exception {
        ExampleBean bean = new ExampleBean(1, 20, "AA", "tester");
        manager.setObject(bean);
        assertEquals(bean, manager.getObject());
        assertEquals(ObjectEnum.DEFAULT_ID.value(),manager.getObject().getTestId());
        manager = null;
        manager = new ObjectSingleStore<ExampleBean>("ObjectSingleStoreTest");
        manager.init();
        assertEquals(bean, manager.getObject());
    }

}