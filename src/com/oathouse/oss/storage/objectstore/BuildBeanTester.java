/*
 * @(#)BuildBeanTester.java
 *
 * Copyright:	Copyright (c) 2010
 * Company:		Oathouse.com Ltd
 */
package com.oathouse.oss.storage.objectstore;

import com.oathouse.oss.storage.exceptions.ObjectBeanSopException;
import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Constructor;
import org.jdom2.Element;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.lang.reflect.Method;
import java.lang.reflect.Array;
import java.lang.reflect.Modifier;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

/**
 * The {@code BuildBeanTester} Class
 *
 * @author Darryl Oatridge
 * @version 1.00 27-Jan-2011
 */
public class BuildBeanTester<T> {

    /*
     * utility private method to test the order of beans for a given list
     */
    public static void testOrder(int[] order, List<ObjectBean> list) throws Exception {
        assertEquals("Testing manager size", order.length, list.size());
        for(int i = 0; i < order.length; i++) {
            assertEquals("Testing manager bean order [" + i + "]", order[i], list.get(i).getIdentifier());
        }
    }

    public static void testObjectBean(String fullClsName, boolean isPrintXml) throws ObjectBeanSopException {
        testObjectBean(fullClsName, isPrintXml, false, null);
    }

    public static void testObjectBean(String fullClsName, boolean isPrintXml, boolean isGroupKey) throws ObjectBeanSopException {
        testObjectBean(fullClsName, isPrintXml, isGroupKey, null);
    }

    public static void testObjectBean(String fullClsName, boolean isPrintXml, List<String> exemptMethods) throws ObjectBeanSopException {
        testObjectBean(fullClsName, isPrintXml, false, exemptMethods);
    }

    public static void testObjectBean(String fullClsName, boolean isPrintXml, boolean isGroupKey, List<String> exemptMethods) throws ObjectBeanSopException {
        if(exemptMethods == null) {
            exemptMethods = new LinkedList<>();
        }
        // get instance of object with a blank constructor
        ObjectBean obInstance = getObjectBeanInstance(getClass(fullClsName), new Object[0]);
        // test initalisation of all the attributes
        testAttributeInit(getClass(fullClsName), obInstance);
        // check the constructor
        checkConstructors(getClass(fullClsName), obInstance, isGroupKey);
        // set the fields with values
        setFields(getClass(fullClsName), obInstance);
        // check all the fields are set
        checkAllValuesSet(getClass(fullClsName), obInstance, " Set Check");
        //now the fieldsare set test the xml
        testXml(getClass(fullClsName), obInstance, isPrintXml);
        // reset the object instance
        obInstance = getObjectBeanInstance(getClass(fullClsName), new Object[0]);
        // check the get methods
        testGetMethods(getClass(fullClsName), obInstance, isGroupKey, exemptMethods);
        // check the set methods
        testSetMethods(getClass(fullClsName), obInstance, exemptMethods);
        //

    }

    private static Class<?> getClass(String fullClsName) throws ObjectBeanSopException {
        Class<?> cls = null;
        try {
            cls = Class.forName(fullClsName);
        } catch(ClassNotFoundException classNotFoundException) {
            throw new ObjectBeanSopException(fullClsName + " does not exist. Class not found!");
        }
        assertNotNull(fullClsName + ".forName() returned null", cls);
        return (cls);
    }

    private static ObjectBean getObjectBeanInstance(Class<?> cls, Object[] params) throws ObjectBeanSopException {
        String simpleClsName = cls.getSimpleName();
        Object obj = null;
        try {
            obj = ConstructorUtils.invokeConstructor(cls, params);
        } catch(Exception ex) {
            fail(cls.getName() + " failed to initialise with blank constructor, " + ex.getClass().getSimpleName());
        }
        assertNotNull(simpleClsName + " initalisation returned null", obj);
        assertTrue(cls.getName() + " is not an instance of ObjectBean", obj instanceof ObjectBean);
        return ((ObjectBean) obj);
    }

    private static void testAttributeInit(Class<?> cls, ObjectBean obInstance) throws ObjectBeanSopException {
        String simpleClsName = cls.getSimpleName();
        while(cls != null) {
            for(Field field : cls.getDeclaredFields()) {
                checkField(field, obInstance, simpleClsName, true);
            }
            cls = cls.getSuperclass();
            if(cls.equals(Object.class)) {
                cls = null;
            }
        }
    }

    private static void checkConstructors(Class<?> cls, ObjectBean obInstance, boolean isGroupKey) throws ObjectBeanSopException {
        String simpleClsName = cls.getSimpleName();
        Constructor<?>[] constructors = cls.getDeclaredConstructors();
        boolean isZero = false;
        boolean notZero = false;
        int paramCount = 0;
        for(int i = 0; i < constructors.length; i++) {
            Constructor<?> c = constructors[i];
            if(c.getParameterTypes().length > 0) {
                notZero = true;
                if(paramCount < c.getParameterTypes().length) {
                    paramCount = c.getParameterTypes().length;
                }
            } else {
                isZero = true;
            }
//            Class<?>[] paramTypes = c.getParameterTypes();
//            String name = c.getName();
//            System.out.print(Modifier.toString(c.getModifiers()));
//            System.out.print(" " + name + "(");
//            for(int j = 0; j < paramTypes.length; j++) {
//                if(j > 0) {
//                    System.out.print(", ");
//                }
//                System.out.print(paramTypes[j].getName());
//            }
//            System.out.println(");");
        }
        assertTrue(simpleClsName + " does not have a zero length consructor", isZero);
        assertTrue(simpleClsName + " does not have a consructor", notZero);
        int fieldCount = 0;
        while(cls != null) {
            for(Field field : cls.getDeclaredFields()) {
                if(!Modifier.isStatic(field.getModifiers())) {
                    fieldCount++;
                }
            }
            cls = cls.getSuperclass();
            if(cls.equals(Object.class)) {
                cls = null;
            }
        }
        fieldCount -= isGroupKey ? 3 : 4;
        assertEquals(simpleClsName + " constructor does not have the correct ammount of parameters", fieldCount, paramCount);
    }

    private static void testGetMethods(Class<?> cls, ObjectBean obInstance, boolean isGroupKey, List<String> exempt) throws ObjectBeanSopException {
        String simpleClsName = cls.getSimpleName();
        String methodName = null;
        Class<?>[] params = {Object.class};
        try {
            if(isGroupKey) {
                methodName = ("getId");
                cls.getDeclaredMethod(methodName, new Class<?>[0]);
                methodName = ("getKey");
                cls.getDeclaredMethod(methodName, new Class<?>[0]);
            } else {
                try {
                    // old type of declaration
                    methodName = ("get" + simpleClsName.substring(0, simpleClsName.length() - 4) + "Id");
                    cls.getDeclaredMethod(methodName, new Class<?>[0]);
                } catch(NoSuchMethodException noSuchMethodException) {
                    //if the old type is not there then try the new.
                    methodName = ("getId");
                    cls.getDeclaredMethod(methodName, new Class<?>[0]);
                }
            }
            methodName = ("equals");
            cls.getDeclaredMethod(methodName, params);
            methodName = ("hashCode");
            cls.getDeclaredMethod(methodName, new Class<?>[0]);
        } catch(NoSuchMethodException e) {
            fail(simpleClsName + " does not have a " + methodName + " method");
        }
        // check all attributes have a get
        while(cls != null) {
            for(Field field : cls.getDeclaredFields()) {
                if(!Modifier.isPublic(field.getModifiers())) {
                    field.setAccessible(true);
                }
                if(!Modifier.isStatic(field.getModifiers())) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(simpleClsName);
                    sb.append(" [");
                    sb.append(field.getName());
                    sb.append("]");

                    Object fieldValue = null;
                    try {
                        fieldValue = field.get(obInstance);
                    } catch(IllegalArgumentException illegalArgumentException) {
                        fail(sb.toString() + " threw an IllegalArgumentException when getting field value");
                    } catch(IllegalAccessException illegalAccessException) {
                        fail(sb.toString() + " threw an IllegalAccessException when getting field value");
                    }
                    if(field.getType().isPrimitive() && fieldValue instanceof Boolean) {
                        methodName = "is" + Character.toUpperCase(field.getName().charAt(0)) + field.getName().substring(1);
                    } else {
                        methodName = "get" + Character.toUpperCase(field.getName().charAt(0)) + field.getName().substring(1);
                    }
                    if(!exempt.contains(methodName)) {
                    try {
                        cls.getDeclaredMethod(methodName, new Class<?>[0]);
                    } catch(NoSuchMethodException e) {
                        fail(simpleClsName + " does not have a " + methodName.toString() + "() method for attribute " + field.getName());
                    }
                }
                }
                field.setAccessible(false);
            }
            cls = cls.getSuperclass();
            if(cls.equals(Object.class)) {
                cls = null;
            }
        }
    }

    private static void testSetMethods(Class<?> cls, ObjectBean obInstance,  List<String> exempt) throws ObjectBeanSopException {
        String simpleClsName = cls.getSimpleName();
        for(Method method : cls.getDeclaredMethods()) {
            StringBuilder sb = new StringBuilder();
            sb.append(simpleClsName);
            sb.append(" [");
            sb.append(method.getName());
            sb.append("]");
            if(method.getName().startsWith("set")) {
                if(method.getName().equals("setXMLDOM")) {
                    continue;
                }
//                System.out.println(sb.toString());
                assertTrue(sb.toString() + " protected modifer expected", Modifier.isProtected(method.getModifiers()));
                if(!exempt.contains(method.getName())) {
                assertTrue(sb.toString() + " may not have the Owner parameter included", method.getParameterTypes().length == 2);
                assertEquals(sb.toString() + " does not have 'String owner' as its last parameter", String.class, method.getParameterTypes()[1]);
                }
//                String fieldName = Character.toLowerCase(method.getName().charAt(3)) + method.getName().substring(4);
////                System.out.println("FieldName = " + fieldName);
//                Field field = null;
//                try {
//                    field = cls.getDeclaredField(fieldName);
//                } catch(NoSuchFieldException noSuchFieldException) {
//                    fail(simpleClsName + " threw a noSuchFieldException when getting field for set method");
//                } catch(SecurityException securityException) {
//                    fail(simpleClsName + " threw a securityException when getting field for set method");
//                }
//                Object fieldValue = null;
//                try {
//                    if(!Modifier.isPublic(field.getModifiers())) {
//                        field.setAccessible(true);
//                    }
//                    fieldValue = field.get(obInstance);
//                } catch(IllegalArgumentException illegalArgumentException) {
//                    fail(sb.toString() + " threw an IllegalArgumentException when getting field value for set method");
//                } catch(IllegalAccessException illegalAccessException) {
//                    fail(sb.toString() + " threw an IllegalAccessException when getting field value for set method");
//                }
//
//                Object[] args = new Object[2];
//                args[1] = new String("TestOwner");
//                if(fieldValue instanceof String) {
//                    args[0] = new String(fieldName);
//                } else if(fieldValue instanceof Integer) {
//                    args[0] = new Integer(99);
//                }
//                try {
//                    method.setAccessible(true);
//                    Object o = method.invoke(obInstance, args);
//                } catch(IllegalAccessException illegalAccessException) {
//                    fail(sb.toString() + " threw an illegalAccessException when invoking method");
//                } catch(InvocationTargetException invocationTargetException) {
//                    fail(sb.toString() + " threw an invocationTargetException when invoking method");
//                }
//                try {
//                    checkField(cls.getDeclaredField("owner"), obInstance, simpleClsName, false);
//                    checkField(cls.getDeclaredField(fieldName), obInstance, simpleClsName, false);
//                } catch(NoSuchFieldException noSuchFieldException) {
//                    fail(sb.toString() + " threw a noSuchFieldException checking field value owner");
//                } catch(SecurityException securityException) {
//                    fail(sb.toString() + " threw a noSuchFieldException checking field value owner");
//                }
            }
        }

    }

    public static void testXml(Class<?> cls, ObjectBean obInstance, boolean isPrintXml) throws ObjectBeanSopException {
        String simpleClsName = cls.getSimpleName();
        LinkedList<String> classNames = new LinkedList<String>();
        Class<?> checkCls = cls;
        while(checkCls != null) {
            classNames.add(checkCls.getSimpleName());
            checkCls = checkCls.getSuperclass();
            if(checkCls.equals(Object.class)) {
                checkCls = null;
            }
        }
        for(Element e : obInstance.getXMLElement()) {
            assertTrue("The XML for " + simpleClsName
                    + " has an incorrect class name element " + e.getName(), classNames.contains(e.getName()));
        }
        String xml = obInstance.toXML();
        if(isPrintXml) {
            System.out.println(xml);
        }
        ObjectBean other = null;
        try {
            other = (ObjectBean) ConstructorUtils.invokeConstructor(cls, new Object[0]);
        } catch(NoSuchMethodException noSuchMethodException) {
            fail(simpleClsName + " threw a NoSuchMethodException when constructedfor XML other object");
        } catch(IllegalAccessException illegalAccessException) {
            fail(simpleClsName + " threw a IllegalAccessException when constructedfor XML other object");
        } catch(InvocationTargetException invocationTargetException) {
            fail(simpleClsName + " threw a InvocationTargetException when constructedfor XML other object");
        } catch(InstantiationException instantiationException) {
            fail(simpleClsName + " threw a InstantiationException when constructedfor XML other object");
        }
        assertNotNull(simpleClsName + " returned null when being constructed for XML other object", other);
        other.setXMLDOM(obInstance.getXMLDOM().detachRootElement());
        checkAllValuesSet(cls, other, " XML Check");
        // test equals() and hashCode() include ObjectBean
        assertTrue("The equals() method in " + simpleClsName + " has failed. Compare XML Attribute name spelling", obInstance.equals(other));
        assertEquals("The hashCode() method in " + simpleClsName + " has failed", obInstance.hashCode(), other.hashCode());
        obInstance.setIdentifier(other.getIdentifier() + 1);
        assertFalse("The equals() method in " + simpleClsName + " has not been properly overridden", obInstance.equals(other));
        assertNotSame("The hashCode() method in " + simpleClsName + " has not been properly overridden", obInstance.hashCode(), other.hashCode());
        // check there is an xml element for every field
        while(cls != null) {
            for(Field field : cls.getDeclaredFields()) {
                if(!Modifier.isPublic(field.getModifiers())) {
                    field.setAccessible(true);
                }
                if(!Modifier.isStatic(field.getModifiers()) && !Modifier.isTransient(field.getModifiers())) {
                    assertTrue("The attribute '" + field.getName() + "' cannot be found in the xml", xml.contains(field.getName()));
                }
            }
            cls = cls.getSuperclass();
            if(cls.equals(ObjectBean.class)) {
                cls = null;
            }
        }

    }

    private static void checkAllValuesSet(Class<?> cls, ObjectBean obInstance, String request) throws ObjectBeanSopException {
        String simpleClsName = cls.getSimpleName() + request;
        while(cls != null) {
            for(Field field : cls.getDeclaredFields()) {
                checkField(field, obInstance, simpleClsName, false);
            }
            cls = cls.getSuperclass();
            if(cls.equals(Object.class)) {
                cls = null;
            }
        }
    }

    /* ***************************************
     * SET the values in the bean
     ******************************************/
    public static void setFields(Class<?> cls, ObjectBean obInstance) throws ObjectBeanSopException {
        setFields(cls, obInstance, 1, new HashMap<String, String>());
    }

    public static void setFields(Class<?> cls, ObjectBean obInstance, int seed) throws ObjectBeanSopException {
        setFields(cls, obInstance, seed, new HashMap<String, String>());
    }

    public static void setFields(Class<?> cls, ObjectBean obInstance, int seed, Map<String, String> fieldSets) throws ObjectBeanSopException {
        int counter = seed * 15;
        String simpleClsName = cls.getSimpleName();
        if(fieldSets.containsKey("id")) {
            obInstance.setIdentifier(Integer.valueOf(fieldSets.get("id")));
        } else {
            obInstance.setIdentifier(seed);
        }
        if(fieldSets.containsKey("archiveYwd")) {
            obInstance.setArchiveYwd(Integer.valueOf(fieldSets.get("archiveYwd")));
        } else {
            obInstance.setArchiveYwd(0);
        }
        if(fieldSets.containsKey("groupKey")) {
            obInstance.setGroupKey(Integer.valueOf(fieldSets.get("groupKey")));
        } else {
            obInstance.setGroupKey(ObjectEnum.DEFAULT_KEY.value());
        }
        if(fieldSets.containsKey("owner")) {
            obInstance.setOwner(fieldSets.get("owner"));
        } else {
            obInstance.setOwner(ObjectBean.SYSTEM_OWNED);;
        }
        obInstance.setCreated(System.currentTimeMillis() - 60000);
        obInstance.setModified();
        while(cls != null) {
            for(Field field : cls.getDeclaredFields()) {
                setField(field, obInstance, simpleClsName, counter++, fieldSets);
            }
            cls = cls.getSuperclass();
            if(cls.equals(ObjectBean.class)) {
                cls = null;
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static Field setField(Field field, ObjectBean obInstance, String simpleClsName, int valueCount,
            Map<String, String> fieldSets) throws ObjectBeanSopException {

        boolean isPreset = fieldSets.containsKey(field.getName()) ? true : false;
        if(!Modifier.isPublic(field.getModifiers())) {
            field.setAccessible(true);
        }
        if(!Modifier.isStatic(field.getModifiers()) && !Modifier.isTransient(field.getModifiers())) {
            StringBuilder sb = new StringBuilder();
            sb.append(simpleClsName);
            sb.append(" [");
            sb.append(field.getName());
            sb.append("]");
            try {
                Object fieldValue = field.get(obInstance);
                if(field.getType().isPrimitive()) {
                    if(fieldValue instanceof Integer) {
                        Integer setValue = isPreset ? Integer.valueOf(fieldSets.get(field.getName())) : Integer.valueOf(valueCount);
                        FieldUtils.writeField(field, obInstance, setValue, true);
                    } else if(fieldValue instanceof Long) {
                        Long setValue = isPreset ? Long.valueOf(fieldSets.get(field.getName())) : Long.valueOf(1000 + valueCount);
                        FieldUtils.writeField(field, obInstance, setValue, true);
                    } else if(fieldValue instanceof Double) {
                        Double setValue = isPreset ? Double.valueOf(fieldSets.get(field.getName())) : Double.valueOf(0.45 + valueCount);
                        FieldUtils.writeField(field, obInstance, setValue, true);
                    } else if(fieldValue instanceof Boolean) {
                        boolean bValue = isPreset ? Boolean.getBoolean(fieldSets.get(field.getName())) : true;
                        FieldUtils.writeField(field, obInstance, bValue, true);
                    } else {
                        fail(sb.toString() + " Unchecked primative " + fieldValue.getClass().getSimpleName() + " type");
                    }
                } else if(field.getType().isArray()) {
                    if(fieldValue instanceof boolean[]) {
                        boolean[] bArray = (boolean[]) fieldValue;
                        String[] sArray = new String[bArray.length];
                        if(isPreset) {
                            String[] vArray = fieldSets.get(field.getName()).split(",");
                            int length = vArray.length < 7 ? vArray.length : 7;
                            System.arraycopy(vArray, 0, sArray, 0, length);
                        }
                        for(int i = 0; i < bArray.length; i++) {
                            bArray[i] = isPreset ? Boolean.parseBoolean(sArray[i]) : (i % 2 == 0 ? true : false);
                        }
                    } else {
                        fail(sb.toString() + " Unchecked array " + fieldValue.getClass().getSimpleName() + " type");
                    }
                } else if(field.getType().isEnum()) {
                    Enum<?> e = (Enum<?>) fieldValue;
                    String setValue = isPreset ? fieldSets.get(field.getName()) : "NO_VALUE";
                    FieldUtils.writeField(field, obInstance, Enum.valueOf(e.getClass(), setValue), true);
                } else if(fieldValue instanceof String) {
                    String setValue = isPreset ? fieldSets.get(field.getName()) : (field.getName() + Integer.toString(valueCount));
                    FieldUtils.writeField(field, obInstance, setValue, true);
                } else if(fieldValue instanceof LinkedList<?>) {
                    assertTrue(sb.toString() + " not populated", setInit((LinkedList<Object>) fieldValue, obInstance, valueCount));
                } else if(fieldValue instanceof ConcurrentSkipListSet<?>) {
                    assertTrue(sb.toString() + " not populated", setInit((ConcurrentSkipListSet<Object>) fieldValue, obInstance, valueCount));
                } else if(fieldValue instanceof ConcurrentSkipListMap<?, ?>) {
                    assertTrue(sb.toString() + " not populated", mapInit((ConcurrentSkipListMap<Object, Object>) fieldValue, obInstance, valueCount));
                } else {
                    fail(sb.toString() + " Unchecked " + fieldValue.getClass().getSimpleName() + " type");
                }

            } catch(IllegalArgumentException | IllegalAccessException | ObjectBeanSopException ex) {
                fail(sb.toString() + " threw an exception : " + ex.getMessage());
            }
        }
        field.setAccessible(false);
        return field;
    }

    private static void checkField(Field field, ObjectBean obInstance, String simpleClsName, boolean isInit) throws ObjectBeanSopException {
        if(!Modifier.isPublic(field.getModifiers())) {
            field.setAccessible(true);
        }
        if(!Modifier.isStatic(field.getModifiers()) && !Modifier.isTransient(field.getModifiers())) {
            StringBuilder sb = new StringBuilder();
            sb.append(simpleClsName);
            sb.append(" [");
            sb.append(field.getName());
            sb.append("]");
            try {
                Object fieldValue = field.get(obInstance);
                if(fieldValue == null) {
                    if(isInit) {
                        fail(sb.toString() + " Object is null");
                    } else {
                        fail(sb.toString() + " Object has been set to null");
                    }
                } else if(field.getType().isPrimitive()) {
                    assertTrue(sb.toString() + " Volatile modifer expected", Modifier.isVolatile(field.getModifiers()));
                    if(fieldValue instanceof Integer) {
                        if(isInit) {
                            assertEquals(sb.toString(), ObjectEnum.INITIALISATION.value(), fieldValue);
                        } else {
                            assertNotSame(sb.toString(), ObjectEnum.INITIALISATION.value(), fieldValue);
                        }
                    } else if(fieldValue instanceof Long) {
                        if(isInit) {
                            assertEquals(sb.toString(), (long) ObjectEnum.INITIALISATION.value(), fieldValue);
                        } else {
                            assertNotSame(sb.toString(), (long) ObjectEnum.INITIALISATION.value(), fieldValue);
                        }
                    } else if(fieldValue instanceof Double) {
                        if(isInit) {
                            assertEquals(sb.toString(), (double) ObjectEnum.INITIALISATION.value(), fieldValue);
                        } else {
                            assertNotSame(sb.toString(), (double) ObjectEnum.INITIALISATION.value(), fieldValue);
                        }
                    } else if(fieldValue instanceof Boolean) {
                        if(isInit) {
                            assertEquals(sb.toString(), false, fieldValue);
                        } else {
                            assertNotSame(sb.toString(), false, fieldValue);
                        }
                    } else {
                        fail(sb.toString() + " Unchecked primative type '" + field.getType().getSimpleName() + "'");
                    }
                } else if(field.getType().isArray()) {
                    assertTrue(sb.toString() + " Final modifer expected", Modifier.isFinal(field.getModifiers()));
                    int length = Array.getLength(fieldValue);
                    boolean check = false;
                    for(int i = 0; i < length; i++) {
                        Object arrayObject = Array.get(fieldValue, i);
                        if(fieldValue instanceof boolean[]) {
                            if((Boolean) arrayObject) {
                                check = true;
                            }
                        } else {
                            fail(sb.toString() + " Unchecked Array type '" + field.getType().getSimpleName() + "'");
                        }
                        if(isInit) {
                            assertFalse(sb.toString() + " all values not set to false", check);
                        } else {
                            assertTrue(sb.toString() + " all values still set to false", check);
                        }
                    }
                } else if(field.getType().isEnum()) {
                    assertTrue(sb.toString() + " Volatile modifer expected", Modifier.isVolatile(field.getModifiers()));
                    if(isInit) {
                        assertEquals(sb.toString() + " Not Initalised", "UNDEFINED", fieldValue.toString());
                    } else {
                        assertEquals(sb.toString() + " Not Set", "NO_VALUE", fieldValue.toString());
                    }
                } else if(fieldValue instanceof String) {
                    assertTrue(sb.toString() + " Volatile modifer expected", Modifier.isVolatile(field.getModifiers()));
                    if(isInit) {
                        assertTrue(sb.toString() + " Not Initalised", ((String) fieldValue).isEmpty());
                    } else {
                        assertFalse(sb.toString() + " Not Set", ((String) fieldValue).isEmpty());
                    }
                } else if(fieldValue instanceof LinkedList<?>) {
                    assertTrue(sb.toString() + " Final modifer expected", Modifier.isFinal(field.getModifiers()));
                    if(isInit) {
                        assertTrue(sb.toString() + " Not Initalised", ((LinkedList<?>) fieldValue).isEmpty());
                    } else {
                        assertFalse(sb.toString() + " Not Set", ((LinkedList<?>) fieldValue).isEmpty());
                    }
                } else if(fieldValue instanceof ConcurrentSkipListSet<?>) {
                    assertTrue(sb.toString() + " Final modifer expected", Modifier.isFinal(field.getModifiers()));
                    if(isInit) {
                        assertTrue(sb.toString() + " Not Initalised", ((ConcurrentSkipListSet<?>) fieldValue).isEmpty());
                    } else {
                        assertFalse(sb.toString() + " Not Set", ((ConcurrentSkipListSet<?>) fieldValue).isEmpty());
                    }
                } else if(fieldValue instanceof ConcurrentSkipListMap<?, ?>) {
                    assertTrue(sb.toString() + " Final modifer expected", Modifier.isFinal(field.getModifiers()));
                    if(isInit) {
                        assertTrue(sb.toString() + " Not Initalised", ((ConcurrentSkipListMap<?, ?>) fieldValue).isEmpty());
                    } else {
                        assertFalse(sb.toString() + " Not Set", ((ConcurrentSkipListMap<?, ?>) fieldValue).isEmpty());
                    }
                } else {
                    fail(sb.toString() + " Unchecked Object type '" + field.getType().getSimpleName() + "'");
                }
            } catch(Exception ex) {
                fail(sb.toString() + ": " + ex.getMessage());
            }
        }
        field.setAccessible(false);
    }

    private static void listChildren(Element current, Set<String> eNames) {
        eNames.add(current.getName());
        List children = current.getChildren();
        Iterator iterator = children.iterator();
        while(iterator.hasNext()) {
            Element child = (Element) iterator.next();
            listChildren(child, eNames);
        }
    }

    private static boolean setInit(Collection<Object> set, ObjectBean source, int value) {
        set.add(101 + value);
        set.add(102 + value);
        try {
            source.toXML();
            return (true);
        } catch(Exception e) {
            set.clear();
        }
        set.add("One" + Integer.toString(value));
        set.add("Two" + Integer.toString(value));
        try {
            source.toXML();
            return (true);
        } catch(Exception e) {
            set.clear();
        }
        return (false);
    }

    private static boolean mapInit(ConcurrentSkipListMap<Object, Object> map, ObjectBean source, int value) {
        map.put(501 + value, 220 + value);
        map.put(502 + value, 231 + value);
        try {
            source.toXML();
            return (true);
        } catch(Exception e) {
            map.clear();
        }
        map.put(501 + value, 3200540L + value);
        map.put(502 + value, 5400120L + value);
        try {
            source.toXML();
            return (true);
        } catch(Exception e) {
            map.clear();
        }
        map.put(501 + value, "Ten" + Integer.toString(value));
        map.put(502 + value, "Twenty" + Integer.toString(value));
        try {
            source.toXML();
            return (true);
        } catch(Exception e) {
            map.clear();
        }
        ConcurrentSkipListSet<Integer> set = new ConcurrentSkipListSet<Integer>();
        set.add(101 + value);
        set.add(102 + value);
        map.put(501 + value, set);
        map.put(502 + value, new ConcurrentSkipListSet<Integer>());
        try {
            source.toXML();
            return (true);
        } catch(Exception e) {
            map.clear();
        }
        return (false);
    }

    /* **************************
     * methods to mirror Junit
     * **************************/
    static private void fail(String message) throws ObjectBeanSopException {
        throw new ObjectBeanSopException(message == null ? "" : message);
    }

    static private void assertEquals(String message, Object expected, Object actual) throws ObjectBeanSopException {
        if(expected == null && actual == null) {
            return;
        }
        if(expected != null && expected.equals(actual)) {
            return;
        }
        fail(message + " - Equals Failed: Expected " + expected.toString() + " but was " + actual.toString());
    }

    static private void assertTrue(String message, boolean condition) throws ObjectBeanSopException {
        if(!condition) {
            fail(message);
        }
    }

    static private void assertFalse(String message, boolean condition) throws ObjectBeanSopException {
        assertTrue(message, !condition);
    }

    static private void assertNotNull(String message, Object object) throws ObjectBeanSopException {
        assertTrue(message, object != null);
    }

    static private void assertNotSame(String message, Object unexpected, Object actual) throws ObjectBeanSopException {
        if(unexpected.equals(actual)) {
            fail(message + " - Expected " + unexpected + " to NOT equal " + actual);
        }
    }

    private BuildBeanTester() {
    }
}
