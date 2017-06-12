/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oathouse.oss.storage.objectstore;

import com.oathouse.oss.storage.exceptions.ObjectBeanSopException;
import com.oathouse.oss.storage.objectstore.example.ExampleBean;
import java.util.Collections;
import java.util.LinkedList;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Darryl
 */
public class ObjectBeanTest {

    @Test
    public void testParameters() {
        long time = System.currentTimeMillis();
        // null owner should trhow an exception
        try {
            ExampleBean bean = new ExampleBean(97, 101, "TestBean", null);
            fail("should trhow an exceptiuon for null owner");
        } catch(IllegalArgumentException e) {
            // SUCCESS
        }

        // empty owner should trhow an exception
        try {
            ExampleBean bean = new ExampleBean(97, 101, "TestBean", "");
            fail("should trhow an exceptiuon for empty owner");
        } catch(IllegalArgumentException e) {
            // SUCCESS
        }

        ExampleBean bean = new ExampleBean(97, 101, "TestBean", "NO_VALUE");
        assertEquals(97, bean.getIdentifier());
        assertEquals(time, bean.getCreated(), 50);
        assertEquals(time, bean.getModified(), 50);
        assertEquals("NO_VALUE", bean.getOwner());

        long modified = bean.getModified();
        long created = bean.getCreated();
        assertEquals(created, bean.getCreated(), 0);
        assertEquals(modified, bean.getModified(), 0);
        bean.setOwner("tester");
        assertEquals(97, bean.getIdentifier());
        assertEquals(created, bean.getCreated(), 0);
        assertNotSame(modified, bean.getModified());
        assertEquals("tester", bean.getOwner());
    }

    @Test
    public void testBean() throws ObjectBeanSopException {
        for(ObjectBeanTestEnum obe : ObjectBeanTestEnum.values()) {
            System.out.println("Testing: " + obe.name());
            BuildBeanTester.testObjectBean(obe.getCls(), obe.isPrintXml());
        }
    }
    /**
     * Unit test:
     */
    @Test
    public void testCompare() throws Exception {
        // create bean
        int seed = 0;
        ExampleBean bean04 = (ExampleBean) BeanBuilder.addBeanValues(new ExampleBean(), 4);
        Thread.sleep(2);
        ExampleBean bean01 = (ExampleBean) BeanBuilder.addBeanValues(new ExampleBean(), 1);
        Thread.sleep(2);
        ExampleBean bean03 = (ExampleBean) BeanBuilder.addBeanValues(new ExampleBean(), 3);
        Thread.sleep(2);
        ExampleBean bean02 = (ExampleBean) BeanBuilder.addBeanValues(new ExampleBean(), 2);
        LinkedList<ExampleBean> list = new LinkedList<ExampleBean>();
        list.add(bean01);
        list.add(bean02);
        list.add(bean03);
        list.add(bean04);
        testOrder(new int[]{1,2,3,4}, list);
        Collections.sort(list,ObjectBean.MODIFIED_ORDER);
        testOrder(new int[]{4,1,3,2}, list);
    }

    /*
     *
     */
    @Test
    public void testToXml() throws Exception {
        ExampleBean bean = (ExampleBean) BeanBuilder.addBeanValues(new ExampleBean(), 1);
        System.out.println("pretty");
        System.out.println(bean.toXML());
        System.out.println("compacted");
        System.out.println(bean.toXML(ObjectDataOptionsEnum.COMPACTED));
        System.out.println("trimmed");
        System.out.println(bean.toXML(ObjectDataOptionsEnum.TRIMMED));
        System.out.println("compacted trimmed");
        System.out.println(bean.toXML(ObjectDataOptionsEnum.TRIMMED, ObjectDataOptionsEnum.COMPACTED));
        System.out.println("printed");
        System.out.println(bean.toXML(ObjectDataOptionsEnum.PRINTED));
        System.out.println("compacted printed");
        System.out.println(bean.toXML(ObjectDataOptionsEnum.PRINTED, ObjectDataOptionsEnum.COMPACTED));

    }



    private void testOrder(int[] order, LinkedList<ExampleBean> check) {
        for(int i = 0; i < order.length; i++) {
            assertEquals("Testing order, layer [" + i + "]",order[i], check.get(i).getExampleId());
        }
    }


}
