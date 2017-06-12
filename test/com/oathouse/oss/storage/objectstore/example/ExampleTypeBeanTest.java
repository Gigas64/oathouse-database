/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.oathouse.oss.storage.objectstore.example;

import com.oathouse.oss.storage.objectstore.example.ExampleDataTypeBean;
import com.oathouse.oss.storage.objectstore.example.ExampleBean;
import com.oathouse.oss.storage.exceptions.ObjectStoreException;
import com.oathouse.oss.storage.objectstore.BeanBuilder;
import com.oathouse.oss.storage.objectstore.ObjectBean;
import java.util.HashMap;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
import org.junit.Test;

/**
 *
 * @author Darryl
 */
public class ExampleTypeBeanTest {

    @Test
    public void testGetDom() throws ObjectStoreException {
        // ExampleDataTypeBean
        int identifier = 1023;
        int age = 41;
        long sdValue = 1342678002;
        boolean checked = true;
        String name = "Miggins";
        String owner ="tester";
        ExampleBean exampleObject = new ExampleBean(0, 100, "Zero", "tester");
        int[] days = {1, 3, 6};
        boolean[] week = {true, true, false, true, true, false, false};
        String[] months = {"Jan", "Feb", "Mar", "Apr"};
        ExampleBean[] examples = {
            new ExampleBean(1, 101, "One", "tester"),
            new ExampleBean(2, 102, "Two", "tester"),
            new ExampleBean(3, 103, "Three", "tester")
        };

        ConcurrentSkipListSet<Boolean> checker = new ConcurrentSkipListSet<Boolean>();
        checker.add(true);
        checker.add(true);
        checker.add(false);
        checker.add(true);
        checker.add(false);

        ConcurrentSkipListSet<Integer> refId = new ConcurrentSkipListSet<Integer>();
        refId.add(134);
        refId.add(265);
        refId.add(318);

        ConcurrentSkipListSet<String> refName = new ConcurrentSkipListSet<String>();
        refName.add("This");
        refName.add("IS");
        refName.add("First");

        ConcurrentSkipListSet<ExampleBean> refObject = new ConcurrentSkipListSet<ExampleBean>();
        refObject.add(new ExampleBean(7, 301, "Seven", "tester"));
        refObject.add(new ExampleBean(8, 302, "Eight", "tester"));
        refObject.add(new ExampleBean(9, 303, "Nine", "tester"));

        ConcurrentSkipListMap<Integer, Integer> ywdRoom = new ConcurrentSkipListMap<Integer, Integer>();
        ywdRoom.put(10, 101);
        ywdRoom.put(20, 201);
        ywdRoom.put(30, 101);

        ConcurrentSkipListMap<Integer, String> ywdName = new ConcurrentSkipListMap<Integer, String>();
        ywdName.put(3, "Year");
        ywdName.put(5, "week");
        ywdName.put(7, "day");

        ConcurrentSkipListMap<Integer, ExampleBean> ywdObject = new ConcurrentSkipListMap<Integer, ExampleBean>();
        ywdObject.put(3, new ExampleBean(4, 201, "Four", "tester"));
        ywdObject.put(5, new ExampleBean(5, 202, "Five", "tester"));
        ywdObject.put(7, new ExampleBean(6, 203, "Six", "tester"));

        ConcurrentSkipListMap<Integer, Set<Integer>> archiveValue = new ConcurrentSkipListMap<Integer, Set<Integer>>();
        archiveValue.put(101, new ConcurrentSkipListSet<Integer>());
        archiveValue.put(102, new ConcurrentSkipListSet<Integer>());
        archiveValue.put(103, new ConcurrentSkipListSet<Integer>());
        archiveValue.get(101).add(499);
        archiveValue.get(101).add(288);
        archiveValue.get(102).add(333);

        ConcurrentSkipListMap<Integer, Set<String>> archiveName = new ConcurrentSkipListMap<Integer, Set<String>>();
        archiveName.put(101, new ConcurrentSkipListSet<String>());
        archiveName.put(102, new ConcurrentSkipListSet<String>());
        archiveName.put(103, new ConcurrentSkipListSet<String>());
        archiveName.get(101).add("One");
        archiveName.get(101).add("Two");
        archiveName.get(102).add("Three");

        ConcurrentSkipListMap<Integer, Set<ExampleBean>> archiveObject = new ConcurrentSkipListMap<Integer, Set<ExampleBean>>();
        archiveObject.put(101, new ConcurrentSkipListSet<ExampleBean>());
        archiveObject.put(102, new ConcurrentSkipListSet<ExampleBean>());
        archiveObject.put(103, new ConcurrentSkipListSet<ExampleBean>());
        archiveObject.get(101).add(new ExampleBean(10, 401, "Ten", "tester"));
        archiveObject.get(101).add(new ExampleBean(11, 402, "Eleven", "tester"));
        archiveObject.get(102).add(new ExampleBean(12, 403, "Twelve", "tester"));

        ExampleDataTypeBean test = new  ExampleDataTypeBean(identifier, owner, age, sdValue, checked, name, exampleObject, days, week, examples, months, checker, refId, refName, refObject, ywdRoom, ywdName, ywdObject, archiveValue, archiveName, archiveObject);
        String xml = test.toXML();
        System.out.println(xml.toString());
    }


}