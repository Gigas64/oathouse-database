/*
 * To change this template, choose Tools | Templates
 * turnOn open the template in the editor.
 */
package com.oathouse.oss.storage.valueholder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import org.junit.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
/**
 *
 * @author Administrator
 */
public class SDHolderTest {

    private int start;
    private int duration;
    private int sdKey;
    private SDHolder sdTest;

    @Before
    public void setUp() {
        start = 1456;
        duration = 7890;
        sdKey = 14567890;
        sdTest = new SDHolder(start, duration);
    }

    /**
     * Test of getPrimary method, of class ValueHolder.
     */
    @Test
    public void testGetStart() {
        assertEquals(start, sdTest.getStart());
        assertEquals(start, SDHolder.getStart(sdKey));
    }

    /**
     * Test of getSecondary method, of class ValueHolder.
     */
    @Test
    public void testGetDuration() {
        assertEquals(duration, sdTest.getDuration());
        assertEquals(duration, SDHolder.getDuration(sdKey));
    }

    /**
     * Test of getSD method, of class ValueHolder.
     */
    @Test
    public void testGetKey() {
        assertEquals(sdKey, sdTest.getSD());
        assertEquals(sdKey, SDHolder.getSD(start, duration));
    }

    @Test
    public void testAddSD() {
        int startKey = SDHolder.getSD(5, 10);
        assertEquals(SDHolder.getSD(5, -1), SDHolder.addSD(startKey, SDHolder.getSD(4, 0)));
        assertEquals(SDHolder.getSD(5, 0), SDHolder.addSD(startKey, SDHolder.getSD(4, 1)));
        assertEquals(SDHolder.getSD(5, 0), SDHolder.addSD(startKey, SDHolder.getSD(5, 0)));
        assertEquals(SDHolder.getSD(5, 1), SDHolder.addSD(startKey, SDHolder.getSD(5, 1)));
        assertEquals(SDHolder.getSD(5, 1), SDHolder.addSD(startKey, SDHolder.getSD(6, 0)));
        assertEquals(SDHolder.getSD(5, 10), SDHolder.addSD(startKey, SDHolder.getSD(6, 9)));
        assertEquals(SDHolder.getSD(5, 5), SDHolder.addSD(startKey, SDHolder.getSD(10, 0)));
        assertEquals(SDHolder.getSD(5, 11), SDHolder.addSD(startKey, SDHolder.getSD(16, 0)));
    }

    @Test
    public void testIntersectSd() throws Exception {
        int startKey = SDHolder.getSD(5, 10);
        assertEquals(-1, SDHolder.intersectSD(startKey, SDHolder.getSD(2, 1)));
        assertEquals(-1, SDHolder.intersectSD(startKey, SDHolder.getSD(4, 0)));
        assertEquals(SDHolder.getSD(5, 0), SDHolder.intersectSD(startKey, SDHolder.getSD(4, 1)));
        assertEquals(SDHolder.getSD(5, 10), SDHolder.intersectSD(startKey, SDHolder.getSD(4, 12)));
        assertEquals(SDHolder.getSD(5, 0), SDHolder.intersectSD(startKey, SDHolder.getSD(5, 0)));
        assertEquals(SDHolder.getSD(5, 10), SDHolder.intersectSD(startKey, SDHolder.getSD(5, 11)));
        assertEquals(SDHolder.getSD(6, 0), SDHolder.intersectSD(startKey, SDHolder.getSD(6, 0)));
        assertEquals(SDHolder.getSD(6, 8), SDHolder.intersectSD(startKey, SDHolder.getSD(6, 8)));
        assertEquals(SDHolder.getSD(15, 0), SDHolder.intersectSD(startKey, SDHolder.getSD(15, 0)));
        assertEquals(SDHolder.getSD(15, 0), SDHolder.intersectSD(startKey, SDHolder.getSD(15, 1)));
        assertEquals(-1, SDHolder.intersectSD(startKey, SDHolder.getSD(16, 1)));

    }

    @Test
    public void testSpanSD() {
        int startKey = SDHolder.getSD(5, 10);
        assertEquals(SDHolder.getSD(2, 13), SDHolder.spanSD(startKey, SDHolder.getSD(2, 1)));
        assertEquals(SDHolder.getSD(4, 11), SDHolder.spanSD(startKey, SDHolder.getSD(4, 0)));
        assertEquals(SDHolder.getSD(4, 11), SDHolder.spanSD(startKey, SDHolder.getSD(4, 1)));
        assertEquals(SDHolder.getSD(5, 10), SDHolder.spanSD(startKey, SDHolder.getSD(5, 0)));
        assertEquals(SDHolder.getSD(5, 11), SDHolder.spanSD(startKey, SDHolder.getSD(5, 11)));
        assertEquals(SDHolder.getSD(5, 10), SDHolder.spanSD(startKey, SDHolder.getSD(15, 0)));
        assertEquals(SDHolder.getSD(5, 11), SDHolder.spanSD(startKey, SDHolder.getSD(15, 1)));
        assertEquals(SDHolder.getSD(5, 12), SDHolder.spanSD(startKey, SDHolder.getSD(16, 1)));
        assertEquals(SDHolder.getSD(5, 13), SDHolder.spanSD(startKey, SDHolder.getSD(17, 1)));
    }

    /**
     * Test the method overlaps, of class ValueHolder.
     */
    @Test
    public void testOverlaps() {
        // 4 -> 7
        int baseKey = SDHolder.getSD(4, 3);
        assertFalse(SDHolder.overlaps(SDHolder.getSD(3,0), baseKey));
        assertTrue(SDHolder.overlaps(SDHolder.getSD(3,1), baseKey));
        assertFalse(SDHolder.overlaps(SDHolder.getSD(3,2), baseKey));
        assertTrue(SDHolder.overlaps(SDHolder.getSD(4,0), baseKey));
        assertFalse(SDHolder.overlaps(SDHolder.getSD(4,1), baseKey));
        assertFalse(SDHolder.overlaps(SDHolder.getSD(5,0), baseKey));
        assertFalse(SDHolder.overlaps(baseKey, SDHolder.getSD(6,0)));
        assertFalse(SDHolder.overlaps(baseKey, SDHolder.getSD(6,1)));
        assertFalse(SDHolder.overlaps(baseKey, SDHolder.getSD(6,2)));
        assertTrue(SDHolder.overlaps(baseKey, SDHolder.getSD(7,0)));
        assertTrue(SDHolder.overlaps(baseKey, SDHolder.getSD(7,1)));
        assertFalse(SDHolder.overlaps(baseKey, SDHolder.getSD(8,0)));
    }

        /**
     * Test of inRange method, of class ValueHolder.
     */
    @Test
    public void testInRange() {
        // 4 -> 7
        int baseKey = SDHolder.getSD(4, 3);

        assertFalse(SDHolder.inRange(baseKey, SDHolder.getSD(-1, 0)));
        assertFalse(SDHolder.inRange(baseKey, SDHolder.getSD(-1, 4)));
        assertFalse(SDHolder.inRange(baseKey, SDHolder.getSD(2, 1)));
        assertFalse(SDHolder.inRange(baseKey, SDHolder.getSD(3, 0)));
        assertTrue(SDHolder.inRange(baseKey, SDHolder.getSD(3, 1)));
        assertTrue(SDHolder.inRange(baseKey, SDHolder.getSD(3, 2)));
        assertTrue(SDHolder.inRange(baseKey, SDHolder.getSD(3, 3)));
        assertTrue(SDHolder.inRange(baseKey, SDHolder.getSD(3, 4)));
        assertTrue(SDHolder.inRange(baseKey, SDHolder.getSD(3, 5)));
        assertTrue(SDHolder.inRange(baseKey, SDHolder.getSD(4, 0)));
        assertTrue(SDHolder.inRange(baseKey, SDHolder.getSD(4, 1)));
        assertTrue(SDHolder.inRange(baseKey, SDHolder.getSD(4, 2)));
        assertTrue(SDHolder.inRange(baseKey, SDHolder.getSD(4, 3)));
        assertTrue(SDHolder.inRange(baseKey, SDHolder.getSD(4, 4)));
        assertTrue(SDHolder.inRange(baseKey, SDHolder.getSD(5, 0)));
        assertTrue(SDHolder.inRange(baseKey, SDHolder.getSD(5, 1)));
        assertTrue(SDHolder.inRange(baseKey, SDHolder.getSD(5, 2)));
        assertTrue(SDHolder.inRange(baseKey, SDHolder.getSD(5, 3)));
        assertTrue(SDHolder.inRange(baseKey, SDHolder.getSD(7, 0)));
        assertTrue(SDHolder.inRange(baseKey, SDHolder.getSD(7, 1)));
        assertFalse(SDHolder.inRange(baseKey, SDHolder.getSD(8, 0)));
        assertFalse(SDHolder.inRange(baseKey, SDHolder.getSD(8, 1)));
    }

    @Test
    public void testGetPeriodSdSplits() {
        int key = SDHolder.getSD(3, 9);
        Set<Integer> control = new ConcurrentSkipListSet<Integer>();
        control.add(SDHolder.getSD(3, 0));
        control.add(SDHolder.getSD(8, 0));
        control.add(SDHolder.getSD(12, 0));
        Set<Integer> test = SDHolder.getPeriodSdSplits(key, 5);
        assertEquals(control, test); // 3,8,12

        // try out getTimeSplit
        test = SDHolder.getTimeSplits(key, 5);
        assertEquals(3, test.size());
        assertTrue(test.containsAll(Arrays.asList(3,8,12)));

        key = SDHolder.getSD(3, 10);
        test = SDHolder.getPeriodSdSplits(key, 5);
        control.remove(SDHolder.getSD(12, 0));
        control.add(SDHolder.getSD(13, 0));
        assertEquals(control, test); // 3,8,13

        key = SDHolder.getSD(3, 11);
        test = SDHolder.getPeriodSdSplits(key, 5);
        control.add(SDHolder.getSD(14, 0));
        assertEquals(control, test); // 3,8,13,14

        key = SDHolder.getSD(3, 0);
        test = SDHolder.getPeriodSdSplits(key, 5);
        control.clear();
        control.add(SDHolder.getSD(3, 0));
        assertEquals(control, test); // 3

        key = SDHolder.getSD(3, 10);
        test = SDHolder.getPeriodSdSplits(key, 20);
        control.add(SDHolder.getSD(13, 0));
        assertEquals(control, test); // 3,13

        key = SDHolder.getSD(3, 11);
        test = SDHolder.getPeriodSdSplits(key, 0);
        control.clear();
        assertEquals(control, test); // empty

    }
    /**
     * Unit test:
     */
    @Test
    public void testGetTimeSet() throws Exception {
        Set<Integer> testSet = new ConcurrentSkipListSet<Integer>(Arrays.asList(
                SDHolder.getSD(5, 9),
                SDHolder.getSD(25, 9),
                SDHolder.getSD(35, 9)
                ));
        assertEquals(3, SDHolder.getTimeSet(testSet).size());
        assertTrue(SDHolder.getTimeSet(testSet).containsAll(Arrays.asList(5,25,35)));
    }

    @Test
    public void testGetSd0() {
        int key = SDHolder.getSD(3, 9);
        int control = SDHolder.getSD(3, 0);
        assertEquals(control, SDHolder.getSD0(key));
    }

    /**
     * Unit test: Test the fitInSD
     * <blockquote>
     * <pre>
     *      Senario 1      **           Senario 2           **      Senario 3      **  Senario 4
     *                     **                               **                     **
     *          |-baseSd-| **           |-baseSd-|          ** |-baseSd-|          ** |---baseSd---|
     * |--linkSd--|        **  |---------linkSd-----------| **      |---linkSd---| **   |-linkSd-|
     * [-rtnSd-]      [-1] **  [-rtnSD-]          [-rtnSD-] ** [-1]      [-rtnSD-] ** [-1]      [-1]
     *
     * </pre>
     * </blockquote>
     */
    @Test
    public void unit01_fitInSD() throws Exception {
        int baseSd = SDHolder.getSD(10, 9); // 10 - 19
        int linkSd = 0;
        assertEquals(10, SDHolder.getStart(baseSd));
        assertEquals(19, SDHolder.getEnd(baseSd));
        // Senario 1
        linkSd = SDHolder.getSD(9, 1); // 9 - 10
        assertEquals(SDHolder.getSD(9, 0), SDHolder.fitInSD(baseSd, linkSd)[0]);
        assertEquals(-1, SDHolder.fitInSD(baseSd, linkSd)[1]);
        linkSd = SDHolder.getSD(9, 10); // 9 - 19
        assertEquals(SDHolder.getSD(9, 0), SDHolder.fitInSD(baseSd, linkSd)[0]);
        assertEquals(-1, SDHolder.fitInSD(baseSd, linkSd)[1]);
        linkSd = SDHolder.getSD(8, 10); // 8 - 18
        assertEquals(SDHolder.getSD(8, 1), SDHolder.fitInSD(baseSd, linkSd)[0]);
        assertEquals(-1, SDHolder.fitInSD(baseSd, linkSd)[1]);
        // Senario 2
        linkSd = SDHolder.getSD(9, 11); // 9 - 20
        assertEquals(SDHolder.getSD(9, 0), SDHolder.fitInSD(baseSd, linkSd)[0]);
        assertEquals(SDHolder.getSD(20, 0), SDHolder.fitInSD(baseSd, linkSd)[1]);
        linkSd = SDHolder.getSD(8, 13); // 8 - 21
        assertEquals(SDHolder.getSD(8, 1), SDHolder.fitInSD(baseSd, linkSd)[0]);
        assertEquals(SDHolder.getSD(20, 1), SDHolder.fitInSD(baseSd, linkSd)[1]);
        // Senario 3
        linkSd = SDHolder.getSD(19, 1); // 19 - 20
        assertEquals(-1, SDHolder.fitInSD(baseSd, linkSd)[0]);
        assertEquals(SDHolder.getSD(20, 0), SDHolder.fitInSD(baseSd, linkSd)[1]);
        linkSd = SDHolder.getSD(19, 2); // 19 - 21
        assertEquals(-1, SDHolder.fitInSD(baseSd, linkSd)[0]);
        assertEquals(SDHolder.getSD(20, 1), SDHolder.fitInSD(baseSd, linkSd)[1]);
        // Senario 4
        linkSd = SDHolder.getSD(10, 9); // 10 - 19
        assertEquals(-1, SDHolder.fitInSD(baseSd, linkSd)[0]);
        assertEquals(-1, SDHolder.fitInSD(baseSd, linkSd)[1]);
        linkSd = SDHolder.getSD(10, 0); // 10 - 10
        assertEquals(-1, SDHolder.fitInSD(baseSd, linkSd)[0]);
        assertEquals(-1, SDHolder.fitInSD(baseSd, linkSd)[1]);
        linkSd = SDHolder.getSD(19, 0); // 19 - 19
        assertEquals(-1, SDHolder.fitInSD(baseSd, linkSd)[0]);
        assertEquals(-1, SDHolder.fitInSD(baseSd, linkSd)[1]);
        // Senario 5 (left outside)
        linkSd = SDHolder.getSD(9, 0); // 9 - 9
        assertEquals(-1, SDHolder.fitInSD(baseSd, linkSd)[0]);
        assertEquals(-1, SDHolder.fitInSD(baseSd, linkSd)[1]);
        // Senario 5 (right outside)
        linkSd = SDHolder.getSD(20, 0); // 20 - 20
        assertEquals(-1, SDHolder.fitInSD(baseSd, linkSd)[0]);
        assertEquals(-1, SDHolder.fitInSD(baseSd, linkSd)[1]);
    }

    /**
     * Unit test: Test the new compare method using SDBits
     */
    @Test
    public void unit02_compare() throws Exception {
        // 4 -> 7
        int baseKey = SDHolder.getSD(4, 3);
        assertEquals(SDBits.START_BEFORE + SDBits.END_BEFORE_LINK, SDHolder.compare(baseKey, SDHolder.getSD(1, 1)));
        assertEquals(SDBits.START_BEFORE + SDBits.END_BEFORE_LINK, SDHolder.compare(baseKey, SDHolder.getSD(2, 0)));
        assertEquals(SDBits.START_BEFORE + SDBits.END_LINKED, SDHolder.compare(baseKey, SDHolder.getSD(2, 1)));
        assertEquals(SDBits.START_BEFORE + SDBits.END_LINKED, SDHolder.compare(baseKey, SDHolder.getSD(3, 0)));
        assertEquals(SDBits.START_BEFORE + SDBits.END_AT_START, SDHolder.compare(baseKey, SDHolder.getSD(3, 1)));
        assertEquals(SDBits.START_BEFORE + SDBits.END_INSIDE, SDHolder.compare(baseKey, SDHolder.getSD(3, 2)));
        assertEquals(SDBits.START_BEFORE + SDBits.END_INSIDE, SDHolder.compare(baseKey, SDHolder.getSD(3, 3)));
        assertEquals(SDBits.START_BEFORE + SDBits.END_EQUALS, SDHolder.compare(baseKey, SDHolder.getSD(3, 4)));
        assertEquals(SDBits.START_BEFORE + SDBits.END_AFTER, SDHolder.compare(baseKey, SDHolder.getSD(3, 5)));
        assertEquals(SDBits.START_EQUALS + SDBits.END_AT_START, SDHolder.compare(baseKey, SDHolder.getSD(4, 0)));
        assertEquals(SDBits.START_EQUALS + SDBits.END_INSIDE, SDHolder.compare(baseKey, SDHolder.getSD(4, 1)));
        assertEquals(SDBits.START_EQUALS + SDBits.END_INSIDE, SDHolder.compare(baseKey, SDHolder.getSD(4, 2)));
        assertEquals(SDBits.START_EQUALS + SDBits.END_EQUALS, SDHolder.compare(baseKey, SDHolder.getSD(4, 3)));
        assertEquals(SDBits.START_EQUALS + SDBits.END_AFTER, SDHolder.compare(baseKey, SDHolder.getSD(4, 4)));
        assertEquals(SDBits.START_INSIDE + SDBits.END_INSIDE, SDHolder.compare(baseKey, SDHolder.getSD(5, 0)));
        assertEquals(SDBits.START_INSIDE + SDBits.END_INSIDE, SDHolder.compare(baseKey, SDHolder.getSD(5, 1)));
        assertEquals(SDBits.START_INSIDE + SDBits.END_EQUALS, SDHolder.compare(baseKey, SDHolder.getSD(5, 2)));
        assertEquals(SDBits.START_INSIDE + SDBits.END_AFTER, SDHolder.compare(baseKey, SDHolder.getSD(5, 3)));
        assertEquals(SDBits.START_INSIDE + SDBits.END_INSIDE, SDHolder.compare(baseKey, SDHolder.getSD(6, 0)));
        assertEquals(SDBits.START_INSIDE + SDBits.END_EQUALS, SDHolder.compare(baseKey, SDHolder.getSD(6, 1)));
        assertEquals(SDBits.START_INSIDE + SDBits.END_AFTER, SDHolder.compare(baseKey, SDHolder.getSD(6, 2)));
        assertEquals(SDBits.START_AT_END + SDBits.END_EQUALS, SDHolder.compare(baseKey, SDHolder.getSD(7, 0)));
        assertEquals(SDBits.START_AT_END + SDBits.END_AFTER, SDHolder.compare(baseKey, SDHolder.getSD(7, 1)));
        assertEquals(SDBits.START_LINKED + SDBits.END_AFTER, SDHolder.compare(baseKey, SDHolder.getSD(8, 0)));
        assertEquals(SDBits.START_LINKED + SDBits.END_AFTER, SDHolder.compare(baseKey, SDHolder.getSD(8, 1)));
        assertEquals(SDBits.START_AFTER_LINK + SDBits.END_AFTER, SDHolder.compare(baseKey, SDHolder.getSD(9, 0)));

        // Special BEFORE turnOn AFTER
        assertTrue(SDBits.match(SDBits.START_BEFORE + SDBits.END_BEFORE, SDHolder.compare(baseKey, SDHolder.getSD(3, 0))));
        assertTrue(SDBits.match(SDBits.START_BEFORE + SDBits.END_BEFORE, SDHolder.compare(baseKey, SDHolder.getSD(1, 1))));
        assertFalse(SDBits.match(SDBits.START_BEFORE + SDBits.END_BEFORE, SDHolder.compare(baseKey, SDHolder.getSD(3, 1))));
        assertTrue(SDBits.match(SDBits.START_AFTER + SDBits.END_AFTER, SDHolder.compare(baseKey, SDHolder.getSD(8, 1))));
        assertTrue(SDBits.match(SDBits.START_AFTER + SDBits.END_AFTER, SDHolder.compare(baseKey, SDHolder.getSD(9, 0))));
        assertFalse(SDBits.match(SDBits.START_AFTER + SDBits.END_AFTER, SDHolder.compare(baseKey, SDHolder.getSD(7, 3))));
        // Special IN_PERIOD
        assertFalse(SDBits.match(SDBits.IN_PERIOD, SDHolder.compare(baseKey, SDHolder.getSD(3, 0))));
        assertFalse(SDBits.match(SDBits.IN_PERIOD, SDHolder.compare(baseKey, SDHolder.getSD(3, 1))));
        assertTrue(SDBits.match(SDBits.IN_PERIOD, SDHolder.compare(baseKey, SDHolder.getSD(4, 0))));
        assertTrue(SDBits.match(SDBits.IN_PERIOD, SDHolder.compare(baseKey, SDHolder.getSD(4, 1))));
        assertTrue(SDBits.match(SDBits.IN_PERIOD, SDHolder.compare(baseKey, SDHolder.getSD(4, 3))));
        assertFalse(SDBits.match(SDBits.IN_PERIOD, SDHolder.compare(baseKey, SDHolder.getSD(4, 4))));
        assertTrue(SDBits.match(SDBits.IN_PERIOD, SDHolder.compare(baseKey, SDHolder.getSD(5, 0))));
        assertTrue(SDBits.match(SDBits.IN_PERIOD, SDHolder.compare(baseKey, SDHolder.getSD(5, 2))));
        assertFalse(SDBits.match(SDBits.IN_PERIOD, SDHolder.compare(baseKey, SDHolder.getSD(5, 3))));
        assertTrue(SDBits.match(SDBits.IN_PERIOD, SDHolder.compare(baseKey, SDHolder.getSD(7, 0))));
        assertFalse(SDBits.match(SDBits.IN_PERIOD, SDHolder.compare(baseKey, SDHolder.getSD(7, 1))));
        assertFalse(SDBits.match(SDBits.IN_PERIOD, SDHolder.compare(baseKey, SDHolder.getSD(8, 0))));
        // extras for Special IN_RANGE
        assertFalse(SDBits.match(SDBits.IN_RANGE, SDHolder.compare(baseKey, SDHolder.getSD(3, 0))));
        assertTrue(SDBits.match(SDBits.IN_RANGE, SDHolder.compare(baseKey, SDHolder.getSD(3, 1))));
        assertTrue(SDBits.match(SDBits.IN_RANGE, SDHolder.compare(baseKey, SDHolder.getSD(3, 5))));
        assertTrue(SDBits.match(SDBits.IN_RANGE, SDHolder.compare(baseKey, SDHolder.getSD(4, 4))));
        assertTrue(SDBits.match(SDBits.IN_RANGE, SDHolder.compare(baseKey, SDHolder.getSD(7, 1))));
        assertFalse(SDBits.match(SDBits.IN_RANGE, SDHolder.compare(baseKey, SDHolder.getSD(8, 0))));
        // special LINKED_TO & LINKED_FROM
        assertTrue(SDBits.match(SDBits.LINKED_TO, SDHolder.compare(baseKey, SDHolder.getSD(2, 1))));
        assertTrue(SDBits.match(SDBits.LINKED_TO, SDHolder.compare(baseKey, SDHolder.getSD(3, 0))));
        assertFalse(SDBits.match(SDBits.LINKED_TO, SDHolder.compare(baseKey, SDHolder.getSD(3, 1))));
        assertFalse(SDBits.match(SDBits.LINKED_TO, SDHolder.compare(baseKey, SDHolder.getSD(3, 4))));
        assertFalse(SDBits.match(SDBits.LINKED_TO, SDHolder.compare(baseKey, SDHolder.getSD(3, 5))));
        assertFalse(SDBits.match(SDBits.LINKED_FROM, SDHolder.compare(baseKey, SDHolder.getSD(3, 5))));
        assertFalse(SDBits.match(SDBits.LINKED_FROM, SDHolder.compare(baseKey, SDHolder.getSD(7, 1))));
        assertTrue(SDBits.match(SDBits.LINKED_FROM, SDHolder.compare(baseKey, SDHolder.getSD(8, 0))));
        assertTrue(SDBits.match(SDBits.LINKED_FROM, SDHolder.compare(baseKey, SDHolder.getSD(8, 1))));
        // special LINKED
        assertTrue(SDBits.match(SDBits.LINKED, SDHolder.compare(baseKey, SDHolder.getSD(2, 1))));
        assertTrue(SDBits.match(SDBits.LINKED, SDHolder.compare(baseKey, SDHolder.getSD(3, 0))));
        assertTrue(SDBits.match(SDBits.LINKED, SDHolder.compare(baseKey, SDHolder.getSD(8, 0))));
        assertTrue(SDBits.match(SDBits.LINKED, SDHolder.compare(baseKey, SDHolder.getSD(8, 1))));
    }
    /*
     *
     */

    @Test
    public void test_compare_AT_LEAST() throws Exception {
        // 4 -> 7
        int baseKey = SDHolder.getSD(4, 3);
        assertFalse(SDBits.match(SDBits.AT_LEAST, SDHolder.compare(baseKey, SDHolder.getSD(3, 0))));
        assertFalse(SDBits.match(SDBits.AT_LEAST, SDHolder.compare(baseKey, SDHolder.getSD(3, 1))));
        assertTrue(SDBits.match(SDBits.AT_LEAST, SDHolder.compare(baseKey, SDHolder.getSD(3, 4))));
        assertTrue(SDBits.match(SDBits.AT_LEAST, SDHolder.compare(baseKey, SDHolder.getSD(3, 5))));
        assertFalse(SDBits.match(SDBits.AT_LEAST, SDHolder.compare(baseKey, SDHolder.getSD(4, 2))));
        assertTrue(SDBits.match(SDBits.AT_LEAST, SDHolder.compare(baseKey, SDHolder.getSD(4, 3))));
        assertTrue(SDBits.match(SDBits.AT_LEAST, SDHolder.compare(baseKey, SDHolder.getSD(4, 4))));
        assertFalse(SDBits.match(SDBits.AT_LEAST, SDHolder.compare(baseKey, SDHolder.getSD(5, 2))));
        assertFalse(SDBits.match(SDBits.AT_LEAST, SDHolder.compare(baseKey, SDHolder.getSD(5, 3))));
        assertFalse(SDBits.match(SDBits.AT_LEAST, SDHolder.compare(baseKey, SDHolder.getSD(6, 0))));
        assertFalse(SDBits.match(SDBits.AT_LEAST, SDHolder.compare(baseKey, SDHolder.getSD(7, 0))));

    }

    /**
     * Unit test: Test splitSD method
     */
    @Test
    public void unit03_splitSD() throws Exception {
        // 4 -> 7
        int baseKey = SDHolder.getSD(4, 3);

        int[] result = SDHolder.splitSD(baseKey, 3);
        assertEquals(result[0], -1);
        assertEquals(result[1], -1);

        result = SDHolder.splitSD(baseKey, 4);
        assertEquals(result[0], -1);
        assertEquals(result[1], -1);

        result = SDHolder.splitSD(baseKey, 5);
        assertEquals(result[0], SDHolder.getSD(4, 0));
        assertEquals(result[1], SDHolder.getSD(5, 2));

        result = SDHolder.splitSD(baseKey, 6);
        assertEquals(result[0], SDHolder.getSD(4, 1));
        assertEquals(result[1], SDHolder.getSD(6, 1));

        result = SDHolder.splitSD(baseKey, 7);
        assertEquals(result[0], SDHolder.getSD(4, 2));
        assertEquals(result[1], SDHolder.getSD(7, 0));

        result = SDHolder.splitSD(baseKey, 8);
        assertEquals(result[0], -1);
        assertEquals(result[1], -1);
    }

    /**
     * Unit test: Test the linkInSd
     */
    @Test
    public void unit04_linkInSd() throws Exception {
        int control = SDHolder.getSD(10, 9);
        assertEquals(SDHolder.getSD(20, 9), SDHolder.linkInSD(control, SDHolder.getSD(30, 9)));
        assertEquals(SDHolder.getSD(20, 0), SDHolder.linkInSD(control, SDHolder.getSD(21, 0)));
        assertEquals(-1, SDHolder.linkInSD(control, SDHolder.getSD(20, 0)));
        assertEquals(-1, SDHolder.linkInSD(control, SDHolder.getSD(19, 5)));
    }


    /**
     * Unit test: test the SDHolder methods to add and remove one from the periodSD
     */
    @Test
    public void unit06_InOutMethods() throws Exception {
        assertEquals(200009, SDHolder.buildSDIn(20, 30));
        assertEquals(200009, SDHolder.buildSDIn(20, 30));
        assertEquals(200009, SDHolder.getSDIn(20, 10));
        assertEquals(190000, SDHolder.buildSDIn(20, 20));
        assertEquals(190000, SDHolder.getSDIn(20, 0));
        assertEquals(30, SDHolder.getEndOut(SDHolder.getSD(20, 9)));
        assertEquals(21, SDHolder.getStartOut(SDHolder.getSD(20, 9)));
        assertEquals(10, SDHolder.getDurationOut(SDHolder.getSD(20, 9)));

        SDHolder test = new SDHolder(20, 9);
        assertEquals(29,test.getEnd());
        assertEquals(30,test.getEndOut());
    }

    /**
     * Unit test: test the logic of hasOverlap
     */
    @Test
    public void unit07_hasOverlap() throws Exception {
        Set<Integer> testSet = new ConcurrentSkipListSet<Integer>();
        assertFalse(SDHolder.hasOverlap(testSet));
        testSet.add(SDHolder.getSD(10, 9));
        assertFalse(SDHolder.hasOverlap(testSet));
        testSet.add(SDHolder.getSD(20, 9));
        assertFalse(SDHolder.hasOverlap(testSet));
        testSet.add(SDHolder.getSD(10, 0));
        assertTrue(SDHolder.hasOverlap(testSet));
        testSet.remove(SDHolder.getSD(10, 0));
        assertFalse(SDHolder.hasOverlap(testSet));
        testSet.add(SDHolder.getSD(29, 0));
        assertTrue(SDHolder.hasOverlap(testSet));
    }

    /*
     *
     */
    @Test
    public void test01_reduceSdDuration() throws Exception {
        // 4 -> 7
        int baseKey = SDHolder.getSD(4, 3);

        assertThat(SDHolder.adjustDuration(baseKey, -4), is(SDHolder.getSD(4, 0)));
        assertThat(SDHolder.adjustDuration(baseKey, -3), is(SDHolder.getSD(4, 0)));
        assertThat(SDHolder.adjustDuration(baseKey, -1), is(SDHolder.getSD(4, 2)));
        assertThat(SDHolder.adjustDuration(baseKey, 0), is(SDHolder.getSD(4, 3)));
        assertThat(SDHolder.adjustDuration(baseKey, 1), is(SDHolder.getSD(4, 4)));
    }

    /*
     *
     */
    @Test
    public void testBuildSd() throws Exception {
        assertThat(SDHolder.buildSD("7:30", "8:30"), is(SDHolder.getSD(450, 60)));   
    }

}
