/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.oathouse.oss.storage.objectstore.example;

import com.oathouse.oss.storage.objectstore.example.ExampleInheritBean;
import com.oathouse.oss.storage.exceptions.ObjectStoreException;
import com.oathouse.oss.storage.objectstore.BeanBuilder;
import com.oathouse.oss.storage.objectstore.ObjectBean;
import java.util.HashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Darryl
 */
public class ExampleInheritBeanTest {
    @Test
    public void testXMLInheritance() throws ObjectStoreException {
        boolean[] days = BeanBuilder.getDays(BeanBuilder.MON_ONLY);
        ConcurrentSkipListSet<Integer> refSet = new ConcurrentSkipListSet<Integer>();
        refSet.add(134);
        refSet.add(265);
        refSet.add(318);
        ConcurrentSkipListMap<Integer, String> refName = new ConcurrentSkipListMap<Integer, String>();
        refName.put(1,"Fred");
        refName.put(2,"Jim");
        refName.put(3,"Bob");
        refName.put(4,"Jane");

        ExampleInheritBean bean = new ExampleInheritBean(1, "name", "label", refSet, 101, "refName", days, refName, "tester");
        System.out.println(bean.toXML());
    }

    /*
     *
     */
    @Test
    public void testBeanBuilderBooleanArray() throws Exception {
        int id = 1;
        HashMap<String, String> fieldSet = new HashMap<>();
        fieldSet.put("id", Integer.toString(id++));
        fieldSet.put("owner", ObjectBean.SYSTEM_OWNED);
        fieldSet.put("days", "true,true,true,false,false");
        ExampleInheritBean bean = (ExampleInheritBean) BeanBuilder.addBeanValues(new ExampleInheritBean(), id, fieldSet);
        System.out.println(bean.toXML());

    }

}