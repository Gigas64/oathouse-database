/*
 * @(#)ObjectBeanSopException.java
 *
 * Copyright:	Copyright (c) 2010
 * Company:		Oathouse.com Ltd
 */
package com.oathouse.oss.storage.exceptions;

/**
 * The {@code ObjectBeanSopException} Enumeration
 *
 * @author Darryl Oatridge
 * @version 1.00 17-Mar-2011
 */
public class ObjectBeanSopException extends ObjectStoreException {
    private static final long serialVersionUID = 5337998960457440844L;

    /**
     * Constructs an instance of {@code ObjectBeanSopException} with the specified detail message.
     * @param msg the detail message.
     */
    public ObjectBeanSopException(String msg) {
        super(msg,true);
    }
}
