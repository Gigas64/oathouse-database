/**
 * @(#)OathouseException.java
 *
 * Copyright:	Copyright (c) 2009
 * Company:     Oathouse.com Ltd
 */

package com.oathouse.oss.storage.exceptions;

/**
 * The {@code OathouseException} Class is the super class of all oathouse
 * exceptions
 *
 * @author      Darryl Oatridge
 * @version 	1.00 29-Jun-2009
 */
public class OathouseException extends Exception {
    private static final long serialVersionUID = -7208081664891963383L;
    private boolean reallyBad;

    /**
     * Constructs an instance of <code>OathouseException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public OathouseException(String msg) {
        super(msg);
        reallyBad = false;
    }

    /**
     * Constructs an instance of <code>OathouseException</code> with the specified detail message and a severity
     * boolean indicating if the exception is application threatening (high severity) as aposed to application error
     * (medium severity) or application warning (low severity) exception.
     * @param msg the detail message.
     * @param isReallyBad true if high severity, false if medium or low.
     */
    public OathouseException(String msg, boolean isReallyBad) {
        super(msg);
        this.reallyBad = isReallyBad;
    }

    /**
     * Used to retried the severity of the exception
     * @return
     */
    public boolean isReallyBad() {
        return reallyBad;
    }
}
