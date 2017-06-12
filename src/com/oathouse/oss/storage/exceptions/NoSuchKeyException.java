/**
 * @(#)NoSuchKeyException.java
 *
 * Copyright:	Copyright (c) 2009
 * Company:	Oathouse.com Ltd
 */
package com.oathouse.oss.storage.exceptions;

/** The {@code NoSuchKeyException} Class
 *
 * @author 	Darryl Oatridge
 * @version 	1.00 01-02-2010
 */
public class NoSuchKeyException extends ObjectStoreException {

    private static final long serialVersionUID = 4839741399624136004L;

    /**
     * Constructs an instance of <code>NoSuchKeyException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public NoSuchKeyException(String msg) {
        super(msg, false);
    }

    /**
     * Constructs an instance of <code>NoSuchKeyException</code> with the specified detail message.
     * @param msg the detail message.
     * @param isError
     */
    public NoSuchKeyException(String msg, boolean isError) {
        super(msg, isError);
    }
}
