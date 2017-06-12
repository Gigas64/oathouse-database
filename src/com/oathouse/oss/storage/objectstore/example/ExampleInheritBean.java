/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/*
 * @(#)ExampleInheritBean.java
 *
 * Copyright:	Copyright (c) 2010
 * Company:		Oathouse.com Ltd
 */
package com.oathouse.oss.storage.objectstore.example;

import com.oathouse.oss.storage.objectstore.ObjectEnum;
import com.oathouse.oss.storage.objectstore.ObjectSetBean;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import org.jdom2.Element;

/**
 * The {@code ExampleInheritBean} Class
 *
 * @author Darryl Oatridge
 * @version 1.00 15-Jan-2011
 */
public class ExampleInheritBean extends ObjectSetBean {

    private static final long serialVersionUID = 20110115100L;
    private static final int DAYS_IN_WEEK = 7;
    private volatile int ref;
    private volatile String refName;
    private final boolean[] days;
    private final Map<Integer, String> refNameSet;

    public ExampleInheritBean() {
        super();
        this.ref = ObjectEnum.INITIALISATION.value();
        this.refName = "";
        this.days = new boolean[7];
        this.refNameSet = new ConcurrentSkipListMap<>();
    }

    public ExampleInheritBean(int identifier, String name, String label,
            Set<Integer> valueSet, int ref, String refName, boolean[] days,
            Map<Integer, String> refNameMap, String owner) {
        super(identifier, name, label, valueSet, owner);
        this.ref = ref;
        this.refName = refName != null ? refName : "";
        this.days = new boolean[7];
        int length = days.length < 7 ? days.length : 7;
        System.arraycopy(days, 0, this.days, 0, length);
        this.refNameSet = new ConcurrentSkipListMap<>();
        if(refNameMap != null && !refNameMap.isEmpty()){
            this.refNameSet.putAll(refNameMap);
        }
    }

    /**
     * Returns the identifier for this bean
     */
    public int getExampleInheritId() {
        return super.getIdentifier();
    }

    public int getRef() {
        return ref;
    }

    public String getRefName() {
        return refName;
    }

    public boolean[] getDays() {
        return days;
    }

    public Map<Integer, String> getRefNameSet() {
        return new ConcurrentSkipListMap<>(refNameSet);
    }

    protected void setRefName(String refName, String owner) {
        this.refName = refName;
    }

    protected void setRef(int ref, String owner) {
        this.ref = ref;
        super.setOwner(owner);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        }
        if(getClass() != obj.getClass()) {
            return false;
        }
        final ExampleInheritBean other = (ExampleInheritBean) obj;
        if(this.ref != other.ref) {
            return false;
        }
        if(!Objects.equals(this.refName, other.refName)) {
            return false;
        }
        if(!Arrays.equals(this.days, other.days)) {
            return false;
        }
        if(!Objects.equals(this.refNameSet, other.refNameSet)) {
            return false;
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + this.ref;
        hash = 89 * hash + Objects.hashCode(this.refName);
        hash = 89 * hash + Arrays.hashCode(this.days);
        hash = 89 * hash + Objects.hashCode(this.refNameSet);
        return hash + super.hashCode();
    }

    /**
     * crates all the elements that represent this bean at this level.
     * @return List of elements in order
     */
    @Override
    public List<Element> getXMLElement() {
        List<Element> rtnList = new LinkedList<Element>();
        // create and add the content Element
        for(Element e : super.getXMLElement()) {
             rtnList.add(e);
        }
        Element bean = new Element("ExampleInheritBean");
        rtnList.add(bean);
        // set the data
        bean.setAttribute("ref", Integer.toString(ref));
        bean.setAttribute("refName", refName);
        Element allElements = new Element("days_array");
        bean.addContent(allElements);
        for(int i = 0; i < days.length; i++) {
            Element eachElement = new Element("days");
            eachElement.setAttribute("index", Integer.toString(i));
            eachElement.setAttribute("value", Boolean.toString(days[i]));
            allElements.addContent(eachElement);
        }
        allElements = new Element("refNameSet_map");
        bean.addContent(allElements);
        if(refNameSet != null) {
            for(int key : refNameSet.keySet()) {
                Element eachElement = new Element("refNameSet");
                eachElement.setAttribute("key", Integer.toString(key));
                eachElement.setAttribute("value", refNameSet.get(key));
                allElements.addContent(eachElement);
            }
        }
        bean.setAttribute("serialVersionUID", Long.toString(serialVersionUID));
        return(rtnList);
    }

    /**
     * sets all the values in the bean from the XML. Remember to
     * put default values in getAttribute() and check the content
     * of getText() if you are parsing to a value.
     *
     * @param root element of the DOM
     */
    @Override
    public void setXMLDOM(Element root) {
        // extract the super meta data
        super.setXMLDOM(root);
        // extract the bean data
        Element bean = root.getChild("ExampleInheritBean");
        // set up the data
        ref = Integer.parseInt(bean.getAttributeValue("ref", "-1"));
        refName = bean.getAttributeValue("refName", "");
        List<Element> allElements = bean.getChild("days_array").getChildren("days");
        for(Element eachDay : allElements) {
            int dow = Integer.valueOf(eachDay.getAttributeValue("index","0"));
            if(dow < days.length) {
                days[dow] = Boolean.valueOf(eachDay.getAttributeValue("value","false"));
            }
        }
        allElements = bean.getChild("refNameSet_map").getChildren("refNameSet");
        refNameSet.clear();
        for(Object o : allElements) {
            Element eachElement = (Element) o;
            refNameSet.put(Integer.valueOf(eachElement.getAttributeValue("key", "-1")),
                                eachElement.getAttributeValue("value", ""));
        }

    }
}
