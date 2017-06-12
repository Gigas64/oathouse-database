/*
 * @(#)BeanFactory.java
 *
 * Copyright:	Copyright (c) 2010
 * Company:		Oathouse.com Ltd
 */
package com.oathouse.oss.storage.objectstore.example;

import com.oathouse.oss.storage.objectstore.ObjectBean;
import com.oathouse.oss.storage.objectstore.BuildBeanTester;
import java.util.HashMap;
import java.util.Map;

/**
 * The {@code BeanFactory} Class
 *
 * @author Darryl Oatridge
 * @version 1.00 11-Feb-2011
 */
public class BeanFactory {

    /**
     * Fills an object bean with values. The values set are predictable, returning the same values
     * for each field depending upon the seed value you give. This means you can test against a value
     * form the generated number.
     *
     *
     * <p>
     * The fieldSet allows you to preset certain fields but is restricted to primatives
     * and string types, so sets and maps can't be preset. For example:
     * </p>
     *
     * <blockquote>
     * <pre>
     *      HashMap<String,String> fieldSet = new HashMap<String,String>();
     *      fieldSet.add("id","3");
     *      fieldSet.add("myInteger","15");
     *      fieldSet.add("myString","Some Name");
     *      fieldSet.add("myBoolean","false");

     *      // create an empty ObjecBean then use BeanFactory to fill it.
     *      MyObjectBean bean1 = new MyObjectBean();
     *      BeanFactory.addBeanValues(bean1, 1, fieldSet);
     *      // reset just the id
     *      fieldSet.put("id", "4");
     *      // different way to create and fill a new bean
     *      MyObjectBean bean2 = (MyObjectBean) BeanFactory.addBeanValues(new MyObjectBean(), 2, fieldSet);
     *
     *      // to set multiple values
     *      for(int i = 1; i < 5; i++) {
     *          MyObjectBean bean = new MyObjectBean();
     *          manager.setObject(10, (MyObjectBean) BeanFactory.addBeanValues(bean, i, fieldSet));
     *      }
     * </pre>
     * </blockquote>
     *
     * <p>
     * If you want to set the identifier use the key word 'id'. By default booleans are set to true, if you
     * want to set a boolean to false use the value 0 as shown in the example.
     * </p>
     *
     * @param ob an ObjectBean
     * @param seed a number so the bean has a different set of values
     * @param fieldSet a map of either primatives or String to set
     * @return the ObjectBean passed with values (this is a convenience
     * @throws Exception
     */
    public static ObjectBean addBeanValues(ObjectBean ob, int seed, Map<String,String> fieldSet) throws Exception {
        Class<?> cls = ob.getClass();
        BuildBeanTester.setFields(cls, ob, seed, fieldSet);
        return ob;
    }

    public static ObjectBean addBeanValues(ObjectBean ob, Map<String,String> fieldSet) throws Exception {
            return addBeanValues(ob, 1, fieldSet);
    }

    public static ObjectBean addBeanValues(ObjectBean ob, int seed) throws Exception {
        return addBeanValues(ob, seed, new HashMap<String,String>());
    }

    public static ObjectBean addBeanValues(ObjectBean ob) throws Exception {
        return addBeanValues(ob, 1, new HashMap<String,String>());
    }


    /**
     * Utility tool to create days of the week
     *
     * pattern examples:
     *   0 = false,false,false,false,false,false,false,
     *   1 = true,false,false,false,false,false,false,
     *   17 = true,false,false,false,true,false,false,
     *   21 = true,false,true,false,true,false,false,
     *   31 = true,true,true,true,true,false,false
     *   32 = false,false,false,false,false,true,false
     *   96 = false,false,false,false,false,true,true
     *   127 = true,true,true,true,true,true,true
     *
     */
    public static boolean[] getDays(int pattern) {
        boolean[] days = new boolean[7];
        for(int i = 0; i < 7; i++) {
            if(((int) Math.pow(2, i) & pattern) > 0) {
                days[i] = true;
            }
        }
        // return the bean
        return (days);
        /* examples
        0 = false,false,false,false,false,false,false,
        1 = true,false,false,false,false,false,false,
        17 = true,false,false,false,true,false,false,
        21 = true,false,true,false,true,false,false,
        31 = true,true,true,true,true,false,false
        32 = false,false,false,false,false,true,false
        96 = false,false,false,false,false,true,true
        127 = true,true,true,true,true,true,true
         */

    }

    private BeanFactory() {
    }

}
