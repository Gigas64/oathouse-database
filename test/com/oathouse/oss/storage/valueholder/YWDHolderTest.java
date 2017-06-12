/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oathouse.oss.storage.valueholder;

import com.oathouse.oss.storage.valueholder.YWDHolder;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Administrator
 */
public class YWDHolderTest {

    @Before
    public void setUp() {
    }

    /**
     * Test of getYWD method, of class YWDHolder.
     */
    @Test
    public void testGetKey() {
        YWDHolder ywd = new YWDHolder(9999, 99, 9);
        YWDHolder ywdKey = new YWDHolder(9999999);
        assertEquals(9999999, ywd.getYWD());
        assertEquals(9999999, ywdKey.getYWD());

        ywd = new YWDHolder(4567, 23, 1);
        assertEquals(4567, ywd.getYear());
        assertEquals(23, ywd.getWeek());
        assertEquals(1, ywd.getDay());

        ywdKey = new YWDHolder(7654321);
        assertEquals(7654, ywdKey.getYear());
        assertEquals(32, ywdKey.getWeek());
        assertEquals(1, ywdKey.getDay());

        ywd = new YWDHolder(0, 0, 0);
        assertEquals(0, ywd.getYear());
        assertEquals(0, ywd.getWeek());
        assertEquals(0, ywd.getDay());

        ywdKey = new YWDHolder(0);
        assertEquals(0, ywdKey.getYear());
        assertEquals(0, ywdKey.getWeek());
        assertEquals(0, ywdKey.getDay());
        ywdKey = new YWDHolder(-1);
        assertEquals(0, ywdKey.getYear());
        assertEquals(0, ywdKey.getWeek());
        assertEquals(-1, ywdKey.getDay());
    }

    /**
     * Test of inRange method, of class YWDHolder.
     */
    @Test
    public void testInRange() {
        YWDHolder ywd = new YWDHolder(2010, 12, 3);
        // extream tests
        int start = 2010123;
        int end = 2010123;
        assertTrue(ywd.inRange(start, end));
        start = 2010122;
        assertTrue(ywd.inRange(start, end));
        start = 2010114;
        assertTrue(ywd.inRange(start, end));
        start = 2010112;
        assertTrue(ywd.inRange(start, end));
        end = 2010124;
        assertTrue(ywd.inRange(start, end));
        end = 2010130;
        assertTrue(ywd.inRange(start, end));
        // exceptional
        start = 2010124;
        end = 2010113;
        assertFalse(ywd.inRange(start, end));
    }

    @Test
    public void testStaticUtils() {
        // test get week
        assertEquals(52, YWDHolder.weeksInYear(2008010));
        assertEquals(53, YWDHolder.weeksInYear(2009010));
        assertEquals(52, YWDHolder.weeksInYear(2010010));
        assertEquals(52, YWDHolder.weeksInYear(2014010));
        assertEquals(53, YWDHolder.weeksInYear(2015010));
        assertEquals(52, YWDHolder.weeksInYear(2016010));

        // days
        assertEquals(2010500, YWDHolder.add(2010500, 0));
        assertEquals(2010501, YWDHolder.add(2010500, 1));
        assertEquals(2010502, YWDHolder.add(2010501, 1));
        assertEquals(2010506, YWDHolder.add(2010500, 6));
        assertEquals(2010510, YWDHolder.add(2010501, 6));
        assertEquals(2010515, YWDHolder.add(2010506, 6));

        //weeks
        assertEquals(2010510, YWDHolder.add(2010500, 10));
        assertEquals(2010512, YWDHolder.add(2010500, 12));
        assertEquals(2011040, YWDHolder.add(2011010, 30));
        assertEquals(2009530, YWDHolder.add(2009500, 30));
        assertEquals(2011020, YWDHolder.add(2010500, 40));

        // years
        assertEquals(2011011, YWDHolder.add(2010010, 1000));
        assertEquals(2013014, YWDHolder.add(2010010, 3000));

        // - days
        assertEquals(2010496, YWDHolder.add(2010500, -1));

    }

    /**
     * Unit test:
     */
    @Test
    public void unit01_add() throws Exception {

        // test the offsetDays
        int startYwd = YWDHolder.getYWD(2012, 50, 0);

        int controlYwd = YWDHolder.getYWD(2012, 50, 0); // offset 0 days
        assertEquals(controlYwd, YWDHolder.add(startYwd, 0));
        controlYwd  = YWDHolder.getYWD(2012, 50, 6); // offset 7 days
        assertEquals(controlYwd, YWDHolder.add(startYwd, 6));
        controlYwd  = YWDHolder.getYWD(2012, 51, 0); // offset 8 days
        assertEquals(controlYwd, YWDHolder.add(startYwd, 10));
        controlYwd  = YWDHolder.getYWD(2013, 1, 0); // offset 10 days
        assertEquals(controlYwd, YWDHolder.add(startYwd, 30));
        controlYwd  = YWDHolder.getYWD(2012, 49, 6); // offset -1 day
        assertEquals(controlYwd, YWDHolder.add(startYwd, -1));
        controlYwd  = YWDHolder.getYWD(2012, 49, 0); // offset -7 days
        assertEquals(controlYwd, YWDHolder.add(startYwd, -10));
        controlYwd  = YWDHolder.getYWD(2012, 48, 6); // offset -8 days
        assertEquals(controlYwd, YWDHolder.add(startYwd, -11));

        CalendarHelper calendar = new CalendarHelper("Europe/London","en_GB");
        int ywd = calendar.getRelativeYW(startYwd, 104); // add 2 years
        assertEquals(YWDHolder.getYWD(2014, 50, 0),ywd);


    }

}
