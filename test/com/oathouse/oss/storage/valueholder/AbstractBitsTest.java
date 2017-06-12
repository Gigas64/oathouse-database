/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oathouse.oss.storage.valueholder;

import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Darryl Oatridge
 */
public class AbstractBitsTest {

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Unit test: Test the getStringBinaryForBits
     */
    @Test
    public void unit01_getStringBinaryForBits() {
        assertEquals("00000000 00000000 00000000 00000010 ", SDBits.getStringBinaryForBits(SDBits.START_BEFORE));
        assertEquals("00000000 00000000 00000000 00000100 ", SDBits.getStringBinaryForBits(SDBits.START_EQUALS));
        assertEquals("00000000 00000000 00000000 00001000 ", SDBits.getStringBinaryForBits(SDBits.START_INSIDE));
        assertEquals("00000000 00000000 00000000 00000110 ", SDBits.getStringBinaryForBits(SDBits.START_EQUALS | SDBits.START_BEFORE | SDBits.START_BEFORE));
        assertEquals("00000000 00000000 00001110 00011100 ", SDBits.getStringBinaryForBits(SDBits.IN_PERIOD));
        assertEquals("00000000 00000000 00001110 00011100 ", SDBits.getStringBinaryForBits(SDBits.IN_PERIOD | SDBits.IN_PERIOD));
    }

    /**
     * Unit test: tests the abstract isBits() method
     */
    @Test
    public void unit02_isBits() throws Exception {
        int bits = 0;
        int mask = 0;
        assertFalse(SDBits.isBits(bits, mask));
        bits = SDBits.START_BEFORE | SDBits.END_AFTER;
        mask = SDBits.START_BEFORE;
        assertTrue(SDBits.isBits(bits, mask));
        assertTrue(SDBits.isBits(bits, mask | SDBits.FILTER_MATCH));
        assertFalse(SDBits.isBits(bits, mask | SDBits.FILTER_EQUALS));
        mask |= SDBits.END_AFTER;
        assertTrue(SDBits.isBits(bits, mask));
        assertTrue(SDBits.isBits(bits, mask | SDBits.FILTER_MATCH));
        assertTrue(SDBits.isBits(bits, mask | SDBits.FILTER_EQUALS));

        bits = SDBits.START_BEFORE | SDBits.END_AFTER;
        mask = SDBits.START_INSIDE;
        assertFalse(SDBits.isBits(bits, mask));
        assertFalse(SDBits.isBits(bits, mask | SDBits.FILTER_MATCH));
        assertFalse(SDBits.isBits(bits, mask | SDBits.FILTER_EQUALS));

        mask = SDBits.START_INSIDE | SDBits.START_BEFORE | SDBits.END_AFTER;
        assertTrue(SDBits.isBits(bits, mask));
        assertFalse(SDBits.isBits(bits, mask | SDBits.FILTER_MATCH));
        assertFalse(SDBits.isBits(bits, mask | SDBits.FILTER_EQUALS));
    }

    /**
     * Unit test: turnOn
     */
    @Test
    public void unit03_turnOn() throws Exception {
        int bits = 3;
        int turnOn = 6;
        int result = 7;
        assertEquals(result, SDBits.turnOn(bits, turnOn));

    }

    /**
     * Unit test: turnOn
     */
    @Test
    public void unit03_turnOff() throws Exception {
        int bits = 1;
        int turnOff = 2;
        int result = 1;
        assertEquals(result, SDBits.turnOff(bits, turnOff));
        bits = 3;
        turnOff = 6;
        result = 1;
        assertEquals(result, SDBits.turnOff(bits, turnOff));

    }

}
