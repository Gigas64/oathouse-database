/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.oathouse.oss.storage.objectstore;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 *
 * @author Administrator
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({com.oathouse.oss.storage.objectstore.ObjectBeanTest.class,
                     com.oathouse.oss.storage.objectstore.ObjectEnumTest.class,
                     com.oathouse.oss.storage.objectstore.ObjectOrderBeanTest.class,
                     com.oathouse.oss.storage.objectstore.ObjectSetBeanTest.class,
                     com.oathouse.oss.storage.objectstore.ObjectSingleStoreTest.class,
                     com.oathouse.oss.storage.objectstore.ObjectSetStoreTest.class,
                     com.oathouse.oss.storage.objectstore.ObjectMapStoreTest.class,
                     com.oathouse.oss.storage.objectstore.ObjectOrderMapStoreTest.class,
                     com.oathouse.oss.storage.objectstore.ObjectOrderSetStoreTest.class})
public class ObjectStoreSuitTest {
}