/**
 * @(#)NullObjectException.java
 *
 * Copyright:	Copyright (c) 2009
 * Company:	Oathouse.com Ltd
 */
package com.oathouse.oss.storage.exceptions;

/** The {@code NullObjectException} Class
 *
 * @author 	Darryl Oatridge
 * @version 	1.00 28-Feb-2009
 */
public class NullObjectException extends ObjectStoreException {

    private static final long serialVersionUID = 3551178462033938955L;

    /**
     * Constructs an instance of <code>NullObjectException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public NullObjectException(String msg) {
        super(msg, false);
    }

    /**
     * Constructs an instance of <code>NullObjectException</code> with the specified detail message.
     * @param msg the detail message.
     * @param isError
     */
    public NullObjectException(String msg, boolean isError) {
        super(msg, isError);
    }
}
