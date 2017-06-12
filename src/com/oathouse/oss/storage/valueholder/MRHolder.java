/*
 * @(#)MRHolder.java
 *
 * Copyright:	Copyright (c) 2010
 * Company:		Oathouse.com Ltd
 */
package com.oathouse.oss.storage.valueholder;

/**
 * The {@code MRHolder} Class allows for the storage and referencing of a multi reference
 * set of values as a single integer value. Though an instance of the class can be created,
 * its main use are the static methods
 *
 * @author Darryl Oatridge
 * @version 1.00 06-Feb-2011
 */
public class MRHolder {

    private final static int MR_FACTOR = 10;
    private int type; // 0-9
    private int reference;

    /**
     * Constructor taking the identifier and reference
     * The identifier can only be from 0-9
     *
     * @param type a value between 0 and 9
     * @param ref an integer reference from 1 to Integer.MAX_VALUE / 10
     */
    public MRHolder(int type, int ref) {
        this.type = type;
        this.reference = ref;
    }

    final public int getType() {
        return type;
    }

    final public int getRef() {
        return reference;
    }

    /* **********************************
     * S T A T I C   M E T H O D S
     * **********************************/
    public static int getRef(int mrValue) {
        return mrValue / MR_FACTOR;
    }

    public static int getType(int mrValue) {
        return mrValue % MR_FACTOR;
    }

    public static int getMR(int type, int ref) {
        int rFactor = ref * MR_FACTOR;
        int iFactor = type;
        return(rFactor + iFactor);
    }

    public static boolean isType(int mrValue, int type) {
        return (getType(mrValue) == type ? true: false);
    }
}
