/*
 * @(#)ObjectSetBean.java
 *
 * Copyright:	Copyright (c) 2010
 * Company:		Oathouse.com Ltd
 */
package com.oathouse.oss.storage.objectstore;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import org.jdom2.Element;

/**
 * The {@code ObjectSetBean} Class is a utility class extending ObjectBean due
 * to the number of Timetable Bean classes that require the storage of a Set of
 * Integers. Mostly these are used for Holder values.
 *
 * @author Darryl Oatridge
 * @version 1.02 28-11-2010
 */
public abstract class ObjectSetBean extends ObjectBean {

    private static final long serialVersionUID = 20101128102L;
    private volatile String name;
    private volatile String label;
    private final ConcurrentSkipListSet<Integer> valueStore;

    public ObjectSetBean(int identifier, String name, String label, Set<Integer> valueSet, String owner) {
        super(identifier, owner);
        this.name = name != null ? name : "";
        this.label = label != null ? label : "";
        this.valueStore = new ConcurrentSkipListSet<Integer>();
        if(valueSet != null) {
            synchronized (valueSet) {
                this.valueStore.addAll(valueSet);
            }
        }
    }

    public ObjectSetBean() {
        super();
        this.name = "";
        this.label = "";
        this.valueStore = new ConcurrentSkipListSet<Integer>();

    }

    protected String getName() {
        return name;
    }

    protected String getLabel() {
        return label;
    }

    protected ConcurrentSkipListSet<Integer> getValueStore() {
        return new ConcurrentSkipListSet<>(valueStore);
    }

    protected void setName(String name, String owner) {
        this.name = name;
        super.setOwner(owner);
    }

    protected void setLabel(String label, String owner) {
        this.label = label;
        super.setOwner(owner);
    }

    protected void setValueStore(Set<Integer> valueSet, String owner) {
        this.valueStore.clear();
        if(valueSet != null) {
            synchronized (valueSet) {
                this.valueStore.addAll(valueSet);
            }
        }
        super.setOwner(owner);
    }

    public static final Comparator<ObjectSetBean> CASE_INSENSITIVE_NAME_ORDER = new Comparator<ObjectSetBean>() {
        @Override
        public int compare(ObjectSetBean o1, ObjectSetBean o2) {
            if (o1 == null && o2 == null) {
                return 0;
            }
            // just in case there are null object values show them last
            if (o1 != null && o2 == null) {
                return -1;
            }
            if (o1 == null && o2 != null) {
                return 1;
            }
            // compare
            int result = o1.getName().toLowerCase().compareTo(o1.getName().toLowerCase());
            if(result != 0) {
                return result;
            }
            // dob not unique so violates the equals comparability. Can cause disappearing objects in Sets
            return (((Integer)o1.getIdentifier()).compareTo((Integer)o2.getIdentifier()));
        }
    };

    @Override
    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        }
        if(getClass() != obj.getClass()) {
            return false;
        }
        final ObjectSetBean other = (ObjectSetBean) obj;
        if((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if((this.label == null) ? (other.label != null) : !this.label.equals(other.label)) {
            return false;
        }
        if(this.valueStore != other.valueStore && (this.valueStore == null || !this.valueStore.equals(other.valueStore))) {
            return false;
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 37 * hash + (this.label != null ? this.label.hashCode() : 0);
        hash = 37 * hash + (this.valueStore != null ? this.valueStore.hashCode() : 0);
        return hash + super.hashCode();
    }

    /**
     * crates all the elements that represent this bean at this level.
     * @return List of elements in order
     */
    @Override
    public List<Element> getXMLElement() {
        List<Element> rtnList = new LinkedList<>();
        // create and add the content Element
        for(Element e : super.getXMLElement()) {
            rtnList.add(e);
        }
        Element bean = new Element("ObjectSetBean");
        rtnList.add(bean);
        // set the data
        bean.setAttribute("name", name);
        bean.setAttribute("label", label);
        Element allElements = new Element("valueStore_set");
        bean.addContent(allElements);
        for(int holder : valueStore) {
            Element eachElement = new Element("valueStore");
            eachElement.setAttribute("value", Integer.toString(holder));
            allElements.addContent(eachElement);
        }
        bean.setAttribute("serialVersionUID", Long.toString(serialVersionUID));
        return (rtnList);
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
        Element bean = root.getChild("ObjectSetBean");
        // set up the data
        name = bean.getAttributeValue("name");
        label = bean.getAttributeValue("label");
        valueStore.clear();
        for(Object o : bean.getChild("valueStore_set").getChildren("valueStore")) {
            Element eachElement = (Element) o;
            valueStore.add(Integer.valueOf(eachElement.getAttributeValue("value", "0")));
        }
    }
}
