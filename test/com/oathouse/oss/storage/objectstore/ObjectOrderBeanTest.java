/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.oathouse.oss.storage.objectstore;

import com.oathouse.oss.storage.objectstore.ObjectOrderBean;
import java.util.Vector;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Administrator
 */
public class ObjectOrderBeanTest {
    ObjectOrderBean oob;

    @Before
    public void setUp() {
        Vector<Integer> order =new Vector<Integer>();
        order.add(23);
        order.add(11);
        order.add(18);
        oob = new ObjectOrderBean(1, order, "tester");
    }

    /**
     * Test of getXMLDOM method, of class ObjectBean.
     */
    @Test
    public void testXMLDOM() {
        ObjectOrderBean other = new ObjectOrderBean();
        other.setXMLDOM(oob.getXMLDOM().detachRootElement());
        assertEquals(oob, other);
    }
}