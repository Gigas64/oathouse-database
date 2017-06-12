/**
 * @(#)IllegalActionException.java
 *
 * Copyright:	Copyright (c) 2009
 * Company:	Oathouse.com Ltd
 */
package com.oathouse.oss.storage.exceptions;

/** The {@code IllegalActionException} Class
 *
 * @author 	Darryl Oatridge
 * @version 	1.00 23-Jun-2009
 */
public class IllegalActionException extends ObjectStoreException {

    private static final long serialVersionUID = 4839741399624136004L;

    /**
     * Constructs an instance of <code>IllegalActionException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public IllegalActionException(String msg) {
        super(msg, false);
    }

    /**
     * Constructs an instance of <code>IllegalActionException</code> with the specified detail message.
     * @param msg the detail message.
     * @param isError
     */
    public IllegalActionException(String msg, boolean isError) {
        super(msg, isError);
    }
}
