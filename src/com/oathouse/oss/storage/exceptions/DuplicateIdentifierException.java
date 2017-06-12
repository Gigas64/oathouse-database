/**
 * @(#)DuplicateIdentifierException.java
 *
 * Copyright:	Copyright (c) 2009
 * Company:	Oathouse.com Ltd
 */
package com.oathouse.oss.storage.exceptions;

/** The {@code DuplicateIdentifierException} Class
 *
 * @author 	Darryl Oatridge
 * @version 	1.00 03-Feb-2011
 */
public class DuplicateIdentifierException extends ObjectStoreException {

    private static final long serialVersionUID = 20100203100L;

    /**
     * Constructs an instance of <code>DuplicateIdentifierException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public DuplicateIdentifierException(String msg) {
        super(msg, false);
    }

    /**
     * Constructs an instance of <code>DuplicateIdentifierException</code> with the specified detail message.
     * @param msg the detail message.
     * @param isError
     */
    public DuplicateIdentifierException(String msg, boolean isError) {
        super(msg, isError);
    }
}
