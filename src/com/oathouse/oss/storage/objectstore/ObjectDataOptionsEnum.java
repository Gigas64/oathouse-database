
/*
 * @(#)ObjectDataOptionsEnum.java
 *
 * Copyright:	Copyright (c) 2012
 * Company:		Oathouse.com Ltd
 */
package com.oathouse.oss.storage.objectstore;

import java.util.ArrayList;
import static java.util.Arrays.asList;
import java.util.List;

/**
 * The {@code ObjectDataOptionsEnum} Enumeration is used to identify the storage type
 * identifying how the ObjectBean data should be stored.
 *
 * @author Darryl Oatridge
 * @version 1.00 07-Nov-2012
 */
public enum ObjectDataOptionsEnum {
    /** The ObjectBean is stored in memory */
    MEMORY,
    /** The ObjectBean is stored to Persistence */
    PERSIST,
    /** The ObjectBean is stored in an ordered manner */
    ORDERED,
    /** The ObjectBean is stored to archive */
    ARCHIVE,
    /** The ObjectBean is presented in a ciphered format */
    CIPHERED,
    /** The ObjectBean is presented in a Compacted format */
    COMPACTED,
    /** The ObjectBean is presented with the XML header trimmed off */
    TRIMMED,
    /** The ObjectBean is presented with the XML header trimmed off and the ObjectBean class in short format*/
    PRINTED;

    /**
     * compares a ObjectDataOptionsEnum against a list, returning true if the list contains the enumeration.
     *
     * @param array the array or arguments
     * @return true if the list contains the enumeration
     */
    public boolean isIn(ObjectDataOptionsEnum[] array) {
        return (asList(array).contains(this));
    }

    /**
     * compares a ObjectDataOptionsEnum against a list, returning true if the list is empty.
     *
     * @param array the array or arguments
     * @return true if the list is empty
     */
    public boolean isEmpty(ObjectDataOptionsEnum[] array) {
        return (asList(array).isEmpty());
    }

    /**
     * A static helper method to convert a List to a ObjectDataOptionsEnum array.
     *
     * @param list a list of ObjectDataOptionsEnum
     * @return the list converted to an array of ObjectDataOptionsEnum
     */
    public static ObjectDataOptionsEnum[] toArray(List<ObjectDataOptionsEnum> list) {
        return list.toArray(new ObjectDataOptionsEnum[list.size()]);
    }

    /**
     * A static helper to add ObjectDataOptionsEnum enumeration from an already existing ObjectDataOptionsEnum array
     *
     * @param array the original array of ObjectDataOptionsEnum enumeration
     * @param options the ObjectDataOptionsEnum enumerations to be added
     * @return the new array of ObjectDataOptionsEnum enumerations
     */
    public static ObjectDataOptionsEnum[] addToArray(ObjectDataOptionsEnum[] array, ObjectDataOptionsEnum... options) {
        ArrayList<ObjectDataOptionsEnum> arrayList = new ArrayList<>(asList(array));
        for(ObjectDataOptionsEnum option : options) {
            if(!arrayList.contains(option)) {
                arrayList.add(option);
            }
        }
        return arrayList.toArray(new ObjectDataOptionsEnum[arrayList.size()]);
    }

    /**
     * A static helper to remove ObjectDataOptionsEnum enumeration from an already existing ObjectDataOptionsEnum array
     *
     * @param array the original array of ObjectDataOptionsEnum enumeration
     * @param options the ObjectDataOptionsEnum enumerations to be removed
     * @return the new array of ObjectDataOptionsEnum enumerations
     */
    public static ObjectDataOptionsEnum[] removeFromArray(ObjectDataOptionsEnum[] array, ObjectDataOptionsEnum... options) {
        ArrayList<ObjectDataOptionsEnum> arrayList = new ArrayList<>(asList(array));
        for(ObjectDataOptionsEnum option : options) {
            if(arrayList.contains(option)) {
                arrayList.remove(option);
            }
        }
        return arrayList.toArray(new ObjectDataOptionsEnum[arrayList.size()]);
    }

}
