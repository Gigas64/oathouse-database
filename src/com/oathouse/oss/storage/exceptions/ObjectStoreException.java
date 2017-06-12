/**
 * @(#)ObjectStoreException.java
 *
 * Copyright:	Copyright (c) 2009
 * Company:     Oathouse.com Ltd
 */

package com.oathouse.oss.storage.exceptions;

/**
 * The {@code ObjectStoreException} Class is the super class of all ObjectStore exceptions
 *
 * @author      Darryl Oatridge
 * @version 	1.00 29-Jun-2009
 */
public class ObjectStoreException extends OathouseException {
    private static final long serialVersionUID = -7208081664891963383L;
    private boolean reallyBad;

    /**
     * Constructs an instance of <code>ObjectStoreException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public ObjectStoreException(String msg) {
        super(msg);
        reallyBad = false;
    }

    /**
     * Constructs an instance of <code>ObjectStoreException</code> with the specified detail message and a severity
     * boolean indicating if the exception is application threatening (high severity) as apposed to application error
     * (medium severity) or application warning (low severity) exception.
     * @param msg the detail message.
     * @param isReallyBad true if high severity, false if medium or low.
     */
    public ObjectStoreException(String msg, boolean isReallyBad) {
        super(msg);
        this.reallyBad = isReallyBad;
    }

    /**
     * Used to retried the severity of the exception
     * @return
     */
    @Override
    public boolean isReallyBad() {
        return reallyBad;
    }
}
