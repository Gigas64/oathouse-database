/**
 * @(#)PersistenceException.java
 *
 * Copyright:	Copyright (c) 2009
 * Company:	Oathouse.com Ltd
 */

package com.oathouse.oss.storage.exceptions;

/**
 * The {@code PersistenceException} Class
 *
 * @author 	Darryl Oatridge
 * @version 	1.00 29-Jun-2009
 */
public class PersistenceException extends ObjectStoreException {
    private static final long serialVersionUID = -7592630156355006762L;

    /**
     * Constructs an instance of <code>PersistenceException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public PersistenceException(String msg) {
        super(msg,true);
    }
}
