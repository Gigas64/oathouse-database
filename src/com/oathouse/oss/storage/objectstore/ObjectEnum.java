/*
 * @(#)ObjectEnum.java
 *
 * Copyright:	Copyright (c) 2010
 * Company:		Oathouse.com Ltd
 */
package com.oathouse.oss.storage.objectstore;

import com.oathouse.oss.storage.exceptions.MaxCountReachedException;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * The {@code ObjectEnum} Enumeration used for general consumption with regard
 * to the utility store classes
 *
 * @author Darryl Oatridge
 * @version 1.00 14-Jul-2010
 */
public enum ObjectEnum {

    DEFAULT_ID(0),
    DEFAULT_VALUE(0),
    INITIALISATION(-1),
    DEFAULT_KEY(-2),
    SINGLE_KEY(-2),
    SINGLE_YWD(-3),
    MIN_RESERVED(-9),
    MAX_RESERVED(0);

    private final int value;

    ObjectEnum(int value) {
        this.value = value;
    }

    /**
     * The value of this enumeration
     * @return the value associated with an enumeration
     */
    public int value() {
        return (this.value);
    }

    /**
     * A check to see if a value is a reserved value
     *
     * @param i value to check
     * @return true if is reserved else false
     */
    public static boolean isReserved(int i) {
        if(i < MIN_RESERVED.value() || i > MAX_RESERVED.value()) {
            return (false);
        }
        return (true);
    }

    public static int generateIdentifier(Set<Integer> excludes) throws MaxCountReachedException {
        return(generateIdentifier(1, excludes));
    }

    /**
     * A static utility method for generating an identifier.
     * @param startValue
     * @param excludes
     * @return
     * @throws MaxCountReachedException
     */
    public static int generateIdentifier(int startValue, Set<Integer> excludes) throws MaxCountReachedException {
        ConcurrentSkipListSet<Integer> ids = new ConcurrentSkipListSet<>();
        if(excludes != null) {
            ids.addAll(excludes);
        }
        for(ObjectEnum oe : ObjectEnum.values()) {
            ids.add(oe.value());
        }
        for(int i = startValue; i < Integer.MAX_VALUE; i++) {
            if(!ids.contains(i) && !isReserved(i)) {
                return (i);
            }
        }
        throw new MaxCountReachedException(
                "Identifier allocation used up from start number '" + startValue + "'");
    }

}
