/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oathouse.oss.storage.valueholder;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Administrator
 */
public class DPHolderTest {

    private int day;
    private int period;
    private int dpKey;
    private DPHolder dpTest;

    @Before
    public void setUp() {
        day = 1;
        period = 3456789;
        dpKey = 103456789;
        dpTest = new DPHolder(day, period);
    }

    /**
     * Test of getDay method, of class DPHolder.
     */
    @Test
    public void testConstructor() {
    }

    /**
     * Test of getPrimary method, of class ValueHolder.
     */
    @Test
    public void testGetStart() {
        assertEquals(day, dpTest.getDay());
        assertEquals(day, DPHolder.getDay(dpKey));
    }

    /**
     * Test of getSecondary method, of class ValueHolder.
     */
    @Test
    public void testGetDuration() {
        assertEquals(period, dpTest.getPeriod());
        assertEquals(period, DPHolder.getPeriod(dpKey));
    }

    /**
     * Test of getDP method, of class ValueHolder.
     */
    @Test
    public void testGetKey() {
        assertEquals(dpKey, dpTest.getDP());
        assertEquals(dpKey, DPHolder.getDP(day, period));
    }

}
