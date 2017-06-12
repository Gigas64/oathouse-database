/**
 * @(#)ObjectBeanTemplate.java
 *
 * Copyright:	Copyright (c) 2009
 * Company:     Oathouse.com Ltd
 */
package com.oathouse.oss.storage.objectstore.example;

import com.oathouse.oss.storage.objectstore.ObjectBean;
import com.oathouse.oss.storage.objectstore.ObjectEnum;
import java.util.LinkedList;
import java.util.List;
import org.jdom2.Element;

/**
 *
 * @author 	Darryl Oatridge
 * @version 	1.01 20-July-2010
 */
public class ExampleGroupBean extends ObjectBean {
    private static final long serialVersionUID = 20100720101L;

    private volatile int value;
    private volatile String name;

    public ExampleGroupBean(int identifier, int key, int value, String name, String owner) {
        super(identifier, key, owner);
        this.value = value;
        this.name = name;
    }

    public ExampleGroupBean() {
        super();
        this.value = ObjectEnum.INITIALISATION.value();
        this.name = "";
    }

    /**
     * Returns the identifier for this bean
     */
    public int getId() {
        return super.getIdentifier();
    }

    public int getKey() {
        return super.getGroupKey();
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        }
        if(getClass() != obj.getClass()) {
            return false;
        }
        final ExampleGroupBean other = (ExampleGroupBean) obj;
        if(this.value != other.value) {
            return false;
        }
        if((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + this.value;
        hash = 89 * hash + (this.name != null ? this.name.hashCode() : 0);
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
        super.getXMLElement().stream().forEach((e) -> {
            rtnList.add(e);
        });
        Element bean = new Element("ExampleGroupBean");
        rtnList.add(bean);
        // set the data
        bean.setAttribute("value", Integer.toString(value));
        bean.setAttribute("name", name);

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
        Element bean = root.getChild("ExampleGroupBean");
        // set up the data
        value = Integer.parseInt(bean.getAttributeValue("value","-1"));
        name = bean.getAttributeValue("name","");
    }

}
