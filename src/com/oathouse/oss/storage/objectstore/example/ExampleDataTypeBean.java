/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/*
 * @(#)ExampleDataTypeBean.java
 *
 * Copyright:	Copyright (c) 2010
 * Company:		Oathouse.com Ltd
 */
package com.oathouse.oss.storage.objectstore.example;

import com.oathouse.oss.storage.objectstore.ObjectBean;
import java.util.Map;
import java.util.Set;

/**
 * The {@code ExampleDataTypeBean} Class
 *
 * @author Darryl Oatridge
 * @version 1.00 08-Jan-2011
 */
public class ExampleDataTypeBean extends ObjectBean {

    private static final long serialVersionUID = 100L;
    private volatile int age;
    private volatile long sdValue;
    private volatile boolean checked;
    private volatile String name;
    private volatile ExampleBean exampleObject;
    private final int[] day;
    private final boolean[] week;
    private final ExampleBean[] example;
    private final String[] months;
    private final Set<Boolean> refCheck;
    private final Set<Integer> refId;
    private final Set<String> refName;
    private final Set<ExampleBean> refObject;
    private final Map<Integer,Integer> ywdRoom;
    private final Map<Integer,String> ywdName;
    private final Map<Integer,ExampleBean> ywdObject;
    private final Map<Integer,Set<Integer>> archiveValue;
    private final Map<Integer,Set<String>> archiveName;
    private final Map<Integer,Set<ExampleBean>> archiveObject;

    public ExampleDataTypeBean(int identifier, String owner, int age, long sdValue, boolean checked, String name,
            ExampleBean exampleObject, int[] day, boolean[] week, ExampleBean[] example, String[] months,
            Set<Boolean> refCheck,
            Set<Integer> refId,
            Set<String> refName,
            Set<ExampleBean> refObject,
            Map<Integer, Integer> ywdRoom,
            Map<Integer, String> ywdName,
            Map<Integer, ExampleBean> ywdObject,
            Map<Integer, Set<Integer>> archiveValue,
            Map<Integer, Set<String>> archiveName,
            Map<Integer, Set<ExampleBean>> archiveObject) {
        super(identifier, owner);
        this.age = age;
        this.sdValue = sdValue;
        this.checked = checked;
        this.name = name;
        this.exampleObject = exampleObject;
        this.day = day;
        this.week = week;
        this.example = example;
        this.months = months;
        this.refCheck = refCheck;
        this.refId = refId;
        this.refName = refName;
        this.refObject = refObject;
        this.ywdRoom = ywdRoom;
        this.ywdName = ywdName;
        this.ywdObject = ywdObject;
        this.archiveValue = archiveValue;
        this.archiveName = archiveName;
        this.archiveObject = archiveObject;
    }

    public ExampleDataTypeBean() {
        super();
        this.age = 0;
        this.sdValue = 0;
        this.checked = true;
        this.name = "";
        this.exampleObject = null;
        this.day = null;
        this.week = null;
        this.example = null;
        this.months = null;
        this.refCheck = null;
        this.refId = null;
        this.refName = null;
        this.refObject = null;
        this.ywdRoom = null;
        this.ywdName = null;
        this.ywdObject = null;
        this.archiveValue = null;
        this.archiveName = null;
        this.archiveObject = null;
    }


 }
