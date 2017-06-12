/*
 * @(#)BeanBuilder.java
 *
 * Copyright:	Copyright (c) 2010
 * Company:		Oathouse.com Ltd
 */
package com.oathouse.oss.storage.objectstore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The {@code BeanBuilder} Class
 *
 * @author Darryl Oatridge
 * @version 1.00 11-Feb-2011
 */
public class BeanBuilder {

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
     *      HashMap&lt;String,String&gt; fieldSet = new HashMap&lt;String,String&gt;();
     *      fieldSet.add("id","3");
     *      fieldSet.add("myInteger","15");
     *      fieldSet.add("myString","Some Name");
     *      fieldSet.add("myBoolean","false");

     *      // create an empty ObjecBean then use BeanBuilder to fill it.
     *      MyObjectBean bean1 = new MyObjectBean();
     *      BeanBuilder.addBeanValues(bean1, 1, fieldSet);
     *      // reset just the id
     *      fieldSet.put("id", "4");
     *      // different way to create and fill a new bean
     *      MyObjectBean bean2 = (MyObjectBean) BeanBuilder.addBeanValues(new MyObjectBean(), 2, fieldSet);
     *
     *      // to set multiple values
     *      for(int i = 1; i &lt; 5; i++) {
     *          MyObjectBean bean = new MyObjectBean();
     *          manager.setObject(10, (MyObjectBean) BeanBuilder.addBeanValues(bean, i, fieldSet));
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

    /**
     * Fills an object bean with values. The values set are predictable, returning the same values
     * for each field depending upon the seed value. This means you can test against a value
     * form the generated number. This overridden method does not allow setting of the seed which is set
     * to 1
     *
     *
     * <p>
     * The fieldSet allows you to preset certain fields but is restricted to primatives
     * and string types, so sets and maps can't be preset. For example:
     * </p>
     *
     * <blockquote>
     * <pre>
     *      HashMap&lt;String,String&gt; fieldSet = new HashMap&lt;String,String&gt;();
     *      fieldSet.add("id","3");
     *      fieldSet.add("myInteger","15");
     *      fieldSet.add("myString","Some Name");
     *      fieldSet.add("myBoolean","false");

     *      // create an empty ObjecBean then use BeanBuilder to fill it.
     *      MyObjectBean bean1 = new MyObjectBean();
     *      BeanBuilder.addBeanValues(bean1, fieldSet);
     *      // reset just the id
     *      fieldSet.put("id", "4");
     *      // different way to create and fill a new bean
     *      MyObjectBean bean2 = (MyObjectBean) BeanBuilder.addBeanValues(new MyObjectBean(), fieldSet);
     *
     *      // to set multiple values
     *      for(int i = 1; i &lt; 5; i++) {
     *          MyObjectBean bean = new MyObjectBean();
     *          manager.setObject(10, (MyObjectBean) BeanBuilder.addBeanValues(bean, i, fieldSet));
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
     * @param fieldSet a map of either primatives or String to set
     * @return the ObjectBean passed with values (this is a convenience
     * @throws Exception
     */
    public static ObjectBean addBeanValues(ObjectBean ob, Map<String,String> fieldSet) throws Exception {
            return addBeanValues(ob, 1, fieldSet);
    }

    /**
     * Fills an object bean with values. The values set are predictable, returning the same values
     * for each field depending upon the seed value you give. This means you can test against a value
     * form the generated number. By default booleans are set to true.
     *
     * @param ob an ObjectBean
     * @param seed a number so the bean has a different set of values
     * @return the ObjectBean passed with values (this is a convenience
     * @throws Exception
     */
    public static ObjectBean addBeanValues(ObjectBean ob, int seed) throws Exception {
        return addBeanValues(ob, seed, new HashMap<String,String>());
    }

    /**
     * Fills an object bean with values. The values set are predictable, returning the same values
     * for each field. This means you can test against a value. By default booleans are set to true.
     *
     * @param ob an ObjectBean
     * @return the ObjectBean passed with values (this is a convenience
     * @throws Exception
     */
    public static ObjectBean addBeanValues(ObjectBean ob) throws Exception {
        return addBeanValues(ob, 1, new HashMap<String,String>());
    }

    // day builder helper statics
    public static final int MON_TO_SUN = 127;
    public static final int MON_TO_FRI = 31;
    public static final int MON_WED_FRI = 21;
    public static final int MON_AND_FRI = 17;
    public static final int MON_ONLY = 1;
    public static final int SAT_ONLY = 96;

    /**
     * Utility tool to create days of the week.
     *
     * <p>
     * pattern examples:<br>
     *   0 = false,false,false,false,false,false,false<br>
     *   1 = true,false,false,false,false,false,false<br>
     *   17 = true,false,false,false,true,false,false<br>
     *   21 = true,false,true,false,true,false,false<br>
     *   31 = true,true,true,true,true,false,false<br>
     *   32 = false,false,false,false,false,true,false<br>
     *   96 = false,false,false,false,false,true,true<br>
     *   127 = true,true,true,true,true,true,true<br>
     * </p>
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
    }

    private BeanBuilder() {
    }

}
