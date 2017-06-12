/*
 * @(#)ObjectOrderBean.java
 *
 * Copyright:	Copyright (c) 2010
 * Company:		Oathouse.com Ltd
 */
package com.oathouse.oss.storage.objectstore;

import java.util.LinkedList;
import java.util.List;
import org.jdom2.Element;

/**
 * The {@code ObjectOrderBean} Classis a helper class for the {@code ObjectOrderMapStore}
 * to maintain the order of stored items where order is an important part of the delivery of
 * objects stored. Order is maintained as a LinkedList of object ref identifiers.
 *
 * @author Darryl Oatridge
 * @version 1.01 19-Jan-2011
 */
public class ObjectOrderBean extends ObjectBean {

    private static final long serialVersionUID = 20100726101L;
    private final LinkedList<Integer> objectOrder;

    public ObjectOrderBean(int identifier, List<Integer> order, String owner) {
        super(identifier, owner);
        this.objectOrder = new LinkedList<>();
        if(order != null) {
            synchronized (order) {
                this.objectOrder.addAll(order);
            }
        }
    }

    public ObjectOrderBean() {
        super();
        objectOrder = new LinkedList<>();
    }

    protected int getObjectOrderId() {
        return super.getIdentifier();
    }

    public int getIdCount() {
        return (objectOrder.size());
    }

    public int getIdAt(int index) {
        return (objectOrder.get(index));
    }

    public int getFirstId() {
        return (objectOrder.getFirst());
    }

    public int getLastId() {
        return (objectOrder.getLast());
    }

    public int getIndexOf(int id) {
        return (objectOrder.indexOf((Integer) id));
    }

    public boolean containsId(int id) {
        if(objectOrder.contains((Integer) id)) {
            return (true);
        }
        return (false);
    }

    protected void addIdAt(int index, int id) {
        synchronized (objectOrder) {
            if(index < 0 || index >= objectOrder.size()) {
                objectOrder.add((Integer) id);
            } else {
                objectOrder.add(index, (Integer) id);
            }
        }
    }

    protected void addFirstId(int id) {
        synchronized (objectOrder) {
            objectOrder.add(0, (Integer) id);
        }
    }

    protected void addLastId(int id) {
        synchronized (objectOrder) {
            objectOrder.add((Integer) id);
        }
    }

    protected void removeId(int id) {
        synchronized (objectOrder) {
            objectOrder.remove((Integer) id);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        }
        if(getClass() != obj.getClass()) {
            return false;
        }
        final ObjectOrderBean other = (ObjectOrderBean) obj;
        if(this.objectOrder != other.objectOrder && (this.objectOrder == null || !this.objectOrder.equals(other.objectOrder))) {
            return false;
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (this.objectOrder != null ? this.objectOrder.hashCode() : 0);
        return hash + hashCode();
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
        Element bean = new Element("ObjectOrderBean");
        rtnList.add(bean);
        // set the data
        Element allElements = new Element("objectOrder");
        bean.addContent(allElements);
        if(objectOrder != null) {
            for(int i : objectOrder) {
                Element eachElement = new Element("object");
                eachElement.setAttribute("order", Integer.toString(i));
                allElements.addContent(eachElement);
            }
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
        Element bean = root.getChild("ObjectOrderBean");
        // set up the data
        objectOrder.clear();
        List allElements = bean.getChild("objectOrder").getChildren("object");
        int counter = 0;
        for(Object o : allElements) {
            Element eachElement = (Element) o;
            String def = Integer.toString(counter++);
            synchronized (objectOrder) {
                objectOrder.add(Integer.parseInt(eachElement.getAttributeValue("order", def)));
            }
        }
    }
}
