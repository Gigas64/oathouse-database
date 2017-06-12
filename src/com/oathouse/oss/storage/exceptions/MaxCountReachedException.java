/**
 * @(#)MaxCountReachedException.java
 *
 * Copyright:	Copyright (c) 2009
 * Company:	Oathouse.com Ltd
 */

package com.oathouse.oss.storage.exceptions;

/**
 * The {@code MaxCountReachedException} Class
 *
 * @author 	Darryl Oatridge
 * @version 	1.00 23-Jun-2009
 */
public class MaxCountReachedException extends ObjectStoreException {
    private static final long serialVersionUID = -4265137233086837239L;

    /**
     * Constructs an instance of <code>MaxCountReachedException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public MaxCountReachedException(String msg) {
        super(msg,true);
    }
}
