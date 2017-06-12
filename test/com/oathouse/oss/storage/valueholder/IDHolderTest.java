/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oathouse.oss.storage.valueholder;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Darryl Oatridge
 */
public class IDHolderTest {

    public IDHolderTest() {
    }

    @Before
    public void setUp() {
    }

    /**
     * Unit test: the gets
     */
    @Test
    public void unit01_IDHolder() throws Exception {
        int ywd = 2011122;
        int id = 1234569;
        System.out.println("Key = " + IDHolder.getKey(ywd, id));
    }


}
