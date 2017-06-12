/**
 * @(#)ObjectBean.java
 *
 * Copyright:	Copyright (c) 2009
 * Company:		Oathouse.com Ltd
 */
package com.oathouse.oss.storage.objectstore;

import com.oathouse.oss.storage.valueholder.IDHolder;
import com.oathouse.oss.storage.valueholder.YWDHolder;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

/**
 * The {@code ObjectBean} Class is a generalised class for Beans
 * using the ObjectManager. This class allows a identifier to be
 * stored and referenced within the ObjectManager. This class is
 * an Abstract class as you must implement setXMLDOM() and getXMLDOM()
 * to add the content of class to a constructed or extracted XML Document
 * the child class creates. as an example the following code would be included
 * if you had a bean with one attribute called myInt
 *
 * <blockquote>
 * <pre>
 *   public Document getXMLDOM() {
 *       <font color="green">// set up the root elements </font>
 *       Element root = new Element("MyBeanName");
 *       Document doc = new Document(root);
 *       <font color="green">// set the class name for persisence</font>
 *       root.setAttribute("class", this.getClass().getName());
 *       <font color="green">// add the root elements</font>
 *       root.addContent(super.getXMLDOM().detachRootElement());
 *       <font color="green">// add the bean elements</font>
 *       Element bean = new Element("bean");
 *       root.addContent(bean);
 *       bean.setAttribute("myInt", Integer.toString(myInt));
 *       <font color="green">// now return the Document</font>
 *       return (doc);
 *   }
 *
 *   public void setXMLDOM(Element root) {
 *       <font color="green">// extract the super meta data</font>
 *       super.setXMLDOM(root.getChild("meta"));
 *       <font color="green">// extract the bean data</font>
 *       Element bean = root.getChild("bean");
 *       myInt = Integer.parseInt(bean.getAttributeValue("myInt"));
 *   }
 * </pre>
 * </blockquote>

 *
 * @author 	Darryl Oatridge
 * @version 1.03 13-July-2010
 */
public abstract class ObjectBean implements Serializable, Comparable<ObjectBean> {

    public static final String ROOTNAME = "Oathouse";
    public static final String SYSTEM_OWNED = "SystemOwned";
    private static final long serialVersionUID = 20100713103L;
    private volatile int identifier;
    private volatile int archiveYwd;
    private volatile int groupKey;
    private volatile long created;
    private volatile long modified;
    private volatile String owner;

    /**
     * Used when needing to create a blank bean for loading from persistence
     */
    protected ObjectBean() {
        this.identifier = ObjectEnum.INITIALISATION.value();
        this.archiveYwd = ObjectEnum.INITIALISATION.value();
        this.groupKey = ObjectEnum.INITIALISATION.value();
        this.created = ObjectEnum.INITIALISATION.value();
        this.modified = ObjectEnum.INITIALISATION.value();
        this.owner = "";
    }

    /**
     * Constructor for a bean providing the bean identifier, groupKey, and owner.
     * The create and modify times are set automatically.
     *
     * @param identifier a numeric identifier for this bean
     * @param groupKey a numeric group key for this bean
     * @param owner the owner or creator of the bean
     */
    protected ObjectBean(int identifier, int groupKey, String owner) {
        this.identifier = identifier;
        this.archiveYwd = ObjectEnum.DEFAULT_VALUE.value();
        this.groupKey = groupKey;
        this.created = System.currentTimeMillis();
        this.modified = this.created;
        this.setOwner(owner);
    }

    /**
     * Constructor for a bean providing the bean identifier and owner.
     * The create and modify times are set automatically. The groupKey
     * is set to ObjectEnum.DEFAULT_KEY.value()
     *
     * @param identifier a numeric identifier for this bean
     * @param owner the owner or creator of the bean
     */
    public ObjectBean(int identifier, String owner) {
        this.identifier = identifier;
        this.archiveYwd = ObjectEnum.DEFAULT_VALUE.value();
        this.groupKey = ObjectEnum.DEFAULT_KEY.value();
        this.created = System.currentTimeMillis();
        this.modified = this.created;
        this.setOwner(owner);
    }

    /**
     * Internal constructor for constructing beans information from XML
     */
    private ObjectBean(int identifier, int archiveYwd, int groupKey, long created, long modified, String owner) {
        this.identifier = identifier;
        this.archiveYwd = archiveYwd;
        this.groupKey = groupKey;
        this.created = created;
        this.modified = modified;
        this.owner = owner;
    }

    /**
     * The object identifier
     */
    public int getIdentifier() {
        return identifier;
    }

    /**
     * The object archiveYwd
     */
    public int getArchiveYwd() {
        return archiveYwd;
    }

    /**
     * The object long identifier
     */
    public long getLongId() {
        if(!YWDHolder.isValid(archiveYwd) || archiveYwd == ObjectEnum.DEFAULT_ID.value() || archiveYwd == ObjectEnum.INITIALISATION.value()) {
            return (identifier);
        }
        return IDHolder.getKey(archiveYwd, identifier);
    }

    /**
     * The object groupKey
     */
    public int getGroupKey() {
        return groupKey;
    }

    /**
     * The object creation time in milliseconds
     */
    public long getCreated() {
        return created;
    }

    /**
     * The object modification time in milliseconds when the object was last changed
     */
    public long getModified() {
        return modified;
    }

    /**
     * The object owner who last changed the object
     */
    public String getOwner() {
        return owner;
    }

    protected final void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    public final void setArchiveYwd(int archiveYwd) {
        if(YWDHolder.isValid(archiveYwd)) {
            this.archiveYwd = archiveYwd;
        } else {
            this.archiveYwd = ObjectEnum.DEFAULT_VALUE.value();
        }
    }

    protected final void setGroupKey(int groupKey) {
        this.groupKey = groupKey;
    }

    protected final void setModified() {
        this.modified = System.currentTimeMillis();
    }

    protected final void setCreated(long created) {
        this.created = created;
    }

    public final void setOwner(String owner) {
        if(owner == null || owner.isEmpty()) {
            throw new IllegalArgumentException("The 'owner' parameter of objects extending ObjectBean must have a value");
        }
        this.owner = owner;
    }

    /**
     * @return true if the identifier of this ObjectBean is the DEFAULT_ID
     * @see ObjectEnum
     */
    public boolean isDefaultId() {
        if(this.getIdentifier() == ObjectEnum.DEFAULT_ID.value()) {
            return true;
        }
        return false;
    }

    /**
     * Compares both the groupKey and the identifier. If the groupKey is different then this is
     * compared. If the groupKey's are equal then the identifier is compared.
     *
     * @param other the ObjectBean to compare
     * @return -1 if less, 0 if the same, 1 if greater
     */
    @Override
    public int compareTo(ObjectBean other) {
        if(this.groupKey != other.getGroupKey()) {
            return (this.groupKey < other.getGroupKey() ? -1 : 1);
        }
        return (this.identifier < other.getIdentifier() ? -1 : (this.identifier == other.getIdentifier() ? 0 : 1));
    }

    /**
     * A comparator which imposes a total ordering on some collection of ObjectBean objects by
     * the modify date of the ObjectBean.
     */
    public static final Comparator<ObjectBean> MODIFIED_ORDER = new Comparator<ObjectBean>() {

        @Override
        public int compare(ObjectBean c1, ObjectBean c2) {
            if(c1 == null && c2 == null) {
                return 0;
            }
            // just in case there are null object values show them last
            if(c1 != null && c2 == null) {
                return -1;
            }
            if(c1 == null && c2 != null) {
                return 1;
            }
            // compare
            int modComp = c1.getModified() < c2.getModified() ? -1 : (c1.getModified() == c2.getModified() ? 0 : 1);
            if(modComp != 0) {
                return modComp;
            }
            // Modify not unique so violates the equals comparability. Can cause disappearing objects in Sets
            return (c1.getIdentifier() < c2.getIdentifier() ? -1 : (c1.getIdentifier() == c2.getIdentifier() ? 0 : 1));
        }
    };

    /**
     * A comparator which imposes a total ordering on some collection of ObjectBean objects by
     * the modify date of the ObjectBean in reverse order.
     */
    public static final Comparator<ObjectBean> REVERSE_MODIFIED_ORDER = new Comparator<ObjectBean>() {

        @Override
        public int compare(ObjectBean c1, ObjectBean c2) {
            if(c1 == null && c2 == null) {
                return 0;
            }
            // just in case there are null object values show them last
            if(c1 != null && c2 == null) {
                return 1;
            }
            if(c1 == null && c2 != null) {
                return -1;
            }
            // compare
            int modComp = c2.getModified() < c1.getModified() ? -1 : (c2.getModified() == c1.getModified() ? 0 : 1);
            if(modComp != 0) {
                return modComp;
            }
            // Modify not unique so violates the equals comparability. Can cause disappearing objects in Sets
            return (c2.getIdentifier() < c1.getIdentifier() ? -1 : (c2.getIdentifier() == c1.getIdentifier() ? 0 : 1));
        }
    };

    /**
     * A comparator which imposes a total ordering on some collection of ObjectBean objects by
     * the archive ywd of the ObjectBean.
     */
    public static final Comparator<ObjectBean> ARCHIVE_YWD_ORDER = new Comparator<ObjectBean>() {

        @Override
        public int compare(ObjectBean a1, ObjectBean a2) {
            if(a1 == null && a2 == null) {
                return 0;
            }
            // just in case there are null object values show them last
            if(a1 != null && a2 == null) {
                return 1;
            }
            if(a1 == null && a2 != null) {
                return -1;
            }
            // compare the ywd
            int ywdComp = a2.getArchiveYwd() < a1.getArchiveYwd() ? -1 : (a2.getArchiveYwd() == a1.getArchiveYwd() ? 0 : 1);
            if(ywdComp != 0) {
                return ywdComp;
            }
            // if the same ywd try sorting by the create
            int creComp = a1.getCreated() < a2.getCreated() ? -1 : (a1.getCreated() == a2.getCreated() ? 0 : 1);
            if(creComp != 0) {
                return creComp;
            }
            // archiveYwd and create not unique so violates the equals comparability. Can cause disappearing objects in Sets
            return (a2.getIdentifier() < a1.getIdentifier() ? -1 : (a2.getIdentifier() == a1.getIdentifier() ? 0 : 1));
        }
    };

    @Override
    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        }
        if(this.getClass() != obj.getClass()) {
            return false;
        }
        final ObjectBean other = (ObjectBean) obj;
        if(this.identifier != other.getIdentifier()) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + this.identifier;
        return hash;
    }

    /**
     * Method constructing a JDOM Document.
     * @return JDOM Document.
     */
    public Document getXMLDOM() {
        // set up the root elements
        Element root = new Element(ROOTNAME);
        Document doc = new Document(root);
        // set the class name for persisence
        root.setAttribute("class", this.getClass().getName());
        // add the root elements
        for(Element e : this.getXMLElement()) {
            root.addContent(e);
        }
        return (doc);
    }

    /**
     * crates all the elements that represent this bean at this level.
     * @return List of elements in order
     */
    public List<Element> getXMLElement() {
        List<Element> rtnList = new LinkedList<>();
        // create and add the content Element
        Element bean = new Element("ObjectBean");
        rtnList.add(bean);
        // set the data
        bean.setAttribute("identifier", Integer.toString(identifier));
        bean.setAttribute("archiveYwd", Integer.toString(archiveYwd));
        bean.setAttribute("groupKey", Integer.toString(groupKey));
        bean.setAttribute("created", Long.toString(created));
        bean.setAttribute("modified", Long.toString(modified));
        bean.setAttribute("owner", owner);
        bean.setAttribute("serialVersionUID", Long.toString(serialVersionUID));
        return (rtnList);
    }

    /**
     * Method attempts to fill in the bean from a JDOM Element and sub elements
     * @param root element of the DOM
     */
    public void setXMLDOM(Element root) {
        // extract the bean data
        Element bean = root.getChild("ObjectBean");
        // set up the data
        identifier = Integer.parseInt(bean.getAttributeValue("identifier", Integer.toString(ObjectEnum.INITIALISATION.value())));
        archiveYwd = Integer.parseInt(bean.getAttributeValue("archiveYwd", Integer.toString(ObjectEnum.INITIALISATION.value())));
        groupKey = Integer.parseInt(bean.getAttributeValue("groupKey", Integer.toString(ObjectEnum.INITIALISATION.value())));
        created = Long.parseLong(bean.getAttributeValue("created", Integer.toString(ObjectEnum.INITIALISATION.value())));
        modified = Long.parseLong(bean.getAttributeValue("modified", Integer.toString(ObjectEnum.INITIALISATION.value())));
        owner = bean.getAttributeValue("owner", "NO_VALUE");
    }

    /**
     * returns a String representation of bean as XML. The returned String
     * has the XML header removed and is in pretty format
     *
     * @return XML as string. No xml Document header
     */

    public String toXML() {
        return (toXML(ObjectDataOptionsEnum.TRIMMED));
    }

    /**
     * returns a String representation of bean as XML
     *
     * @param objectStorageArgs the ObjectDataOptionsEnum array of arguments
     * @return XML as string
     */
    public synchronized String toXML(ObjectDataOptionsEnum... objectStorageArgs) {
        XMLOutputter serializer;

        if(ObjectDataOptionsEnum.COMPACTED.isIn(objectStorageArgs)) {
            serializer = new XMLOutputter(Format.getCompactFormat().setEncoding("utf-8"));
        } else {
            serializer = new XMLOutputter(Format.getPrettyFormat().setEncoding("utf-8").setIndent("  "));
        }
        StringBuilder xmlString = new StringBuilder(serializer.outputString(getXMLDOM()));
        if(ObjectDataOptionsEnum.TRIMMED.isIn(objectStorageArgs)) {
            int index = xmlString.indexOf("?>") + 4;
            xmlString.delete(0, index);
        }
        if(ObjectDataOptionsEnum.PRINTED.isIn(objectStorageArgs)) {
            int index = xmlString.indexOf("\">") + 2;
            xmlString.delete(0, index);
            String name = this.getClass().getSimpleName();
            xmlString.insert(0, "<Oathouse class=\"" + name + "\">");
        }
        return (xmlString.toString());
    }

    /**
     * override of the toString method
     *
     * @return string representation of the bean
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Bean->\n");
        Class<?> cls = this.getClass();
        while(cls != null) {
            sb.append(cls.getSimpleName());
            sb.append("\n");
            for(Field field : cls.getDeclaredFields()) {
                if(Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                if(!Modifier.isPublic(field.getModifiers())) {
                    field.setAccessible(true);
                }
                sb.append("  ");
                sb.append(field.getName());
                try {
                    Object fieldValue = field.get(this);
                    if(field.getType().isPrimitive() || field.getType().isEnum() || fieldValue instanceof String) {
                        sb.append(" = ");
                        sb.append(fieldValue.toString());
                    } else if(fieldValue instanceof Object[]) {
                        if(fieldValue instanceof String[]) {
                            String[] s = (String[]) fieldValue;
                            int length = Array.getLength(fieldValue);
                            sb.append("[");
                            sb.append(Integer.toString(length));
                            sb.append("] = { ");
                            for(int i = 0; i < length; i++) {
                                sb.append(s[i]);
                                if(i < length - 1) {
                                    sb.append(", ");
                                }
                            }
                            sb.append(" }");
                        } else {
                            Object[] o = (Object[]) fieldValue;
                            int length = Array.getLength(fieldValue);
                            sb.append("[");
                            sb.append(Integer.toString(length));
                            sb.append("] = { \n");
                            for(int i = 0; i < length; i++) {
                                sb.append("    index [");
                                sb.append(i);
                                sb.append("] ");

                                sb.append(o[i].toString());
                            }
                            sb.append("  }");
                        }
                    } else if(field.getType().isArray()) {
                        int length = Array.getLength(fieldValue);
                        sb.append("[");
                        sb.append(Integer.toString(length));
                        sb.append("] = { ");
                        for(int i = 0; i < length; i++) {
                            Object arrayObject = Array.get(fieldValue, i);
                            sb.append(arrayObject.toString());
                            if(i < length - 1) {
                                sb.append(", ");
                            }
                        }
                        sb.append(" }");
                    } else if(fieldValue instanceof Set<?>) {
                        sb.append(" = ");
                        Set<?> set = (Set<?>) fieldValue;
                        sb.append(" : ");
                        for(Object o : set) {
                            sb.append(o.toString());
                            sb.append(" : ");
                        }
                    } else if(fieldValue instanceof Map<?, ?>) {
                        sb.append(" = ");
                        Map<?, ?> map = (Map<?, ?>) fieldValue;
                        sb.append(" : ");
                        for(Object key : map.keySet()) {
                            sb.append("[");
                            sb.append(key.toString());
                            sb.append("]->");
                            sb.append(map.get(key).toString());
                            sb.append(" : ");
                        }
                    } else if(fieldValue instanceof Object) {
                        sb.append(" [");
                        sb.append(field.getType().getSimpleName());
                        sb.append("] = ");
                        sb.append(fieldValue.toString());
                    } else {
                        sb.append(" Unknown Type =>  ");
                        sb.append(field.getType());
                    }

                } catch(IllegalArgumentException ex) {
                    sb.append(" IllegalArgumentException ");
                } catch(IllegalAccessException ex) {
                    sb.append(" IllegalAccessException ");
                }

                field.setAccessible(false);
                sb.append("\n");
            }
            cls = cls.getSuperclass();
            if(cls.equals(Object.class)) {
                cls = null;
            }
        }
        sb.append("\n");

        return (sb.toString());
    }
}
