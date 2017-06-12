/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oathouse.oss.storage.valueholder;

// common imports
import com.oathouse.oss.storage.objectstore.ObjectDBMS;
import com.oathouse.oss.storage.objectstore.BeanBuilder;
import com.oathouse.oss.storage.objectstore.ObjectBean;
import com.oathouse.oss.server.OssProperties;
import java.io.File;
import java.util.*;
import static java.util.Arrays.*;
// Test Imports
import org.junit.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
/**
 *
 * @author Darryl Oatridge
 */
public class CalendarStaticTest {

    @Test
    public void calendarStatic_getRelativeMonthStartEnd() {
        // find january as an offset
        int offsetJan = -11;
        boolean found = false;
        for(; offsetJan <= 0; offsetJan++) {
            if(YWDHolder.getWeek(CalendarStatic.getRelativeMonthStartEnd(offsetJan)[0]) == 1) {
                found = true;
                break;
            }
        }
        assertThat(found, is(true));
        int[] jan = CalendarStatic.getRelativeMonthStartEnd(offsetJan++);
        System.out.println("Jan : " + jan[0] + " - " + jan[1]);
        int[] feb = CalendarStatic.getRelativeMonthStartEnd(offsetJan++);
        System.out.println("feb : " + feb[0] + " - " + feb[1]);
        int[] mar = CalendarStatic.getRelativeMonthStartEnd(offsetJan);
        System.out.println("mar : " + mar[0] + " - " + mar[1]);
    }
}
