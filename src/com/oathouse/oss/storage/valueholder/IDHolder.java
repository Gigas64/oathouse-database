/*
 * @(#)IDHolder.java
 *
 * Copyright:	Copyright (c) 2010
 * Company:		Oathouse.com Ltd
 */
package com.oathouse.oss.storage.valueholder;

/**
 * The {@code IDHolder} Class allows for the storage and referencing of a long ID
 * that allows an identifier to be qualified by a ywd.
 *
 * @author Darryl Oatridge
 * @version 1.00 14-Oct-2011
 */
public class IDHolder {
    private static final long ID_FACTOR = 10000000000L;

    private int ywd;
    private int id;

    /**
     * Constructor to create the read only IDHolder
     * @param ywd
     * @param id
     */
    public IDHolder(int ywd, int id) {
        this.ywd = ywd;
        this.id = id;
    }

    /**
     * This constructor allows a key value to be passed as a parameter
     * @param key
     */
    public IDHolder(long key) {
        this.ywd = getYwd(key);
        this.id = getId(key);
    }

    final public int getYwd() {
        return ywd;
    }

    final public int getId() {
        return id;
    }

    final public long getKey() {
        return getKey(ywd, id);
    }

    /* **********************************
     * S T A T I C   M E T H O D S
     * **********************************/
    public static int getYwd(long key) {
        return ((int) (key / ID_FACTOR));
    }

    public static int getId(long key) {
        return ((int) (key % ID_FACTOR));
    }

    public static long getKey(int ywd, int id) {
        long dFactor = ywd * ID_FACTOR;
        int pFactor = id;
        return (dFactor + pFactor);
    }

    /* **********************************
     * O V E R R I D E   M E T H O D S
     * **********************************/
    @Override
    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        }
        if(getClass() != obj.getClass()) {
            return false;
        }
        final IDHolder other = (IDHolder) obj;
        if(this.ywd != other.ywd) {
            return false;
        }
        if(this.id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + this.ywd;
        hash = 59 * hash + this.id;
        return hash;
    }

}
