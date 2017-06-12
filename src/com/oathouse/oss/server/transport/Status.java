/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/*
 * @(#)Status.java
 *
 * Copyright:	Copyright (c) 2010
 * Company:		Oathouse.com Ltd
 */
package com.oathouse.oss.server.transport;

/**
 * The {@code Status} Enumeration used to identify
 *
 * @author Darryl Oatridge
 * @version 1.00 26-Mar-2011
 */
public enum Status {
    SUCCESS(0),
    UNDEFINED(-1),
    ID_NOT_FOUND(-11),
    KEY_NOT_FOUND(-12),
    NOT_REQUEST_OBJECT(-13),
    CMD_NOT_HANDLED(-14),
    IO_ERROR(-15),
    FAILURE(-16);

    private final int value;

    private Status(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public byte getByteValue() {
        return (byte) value;
    }

    public static Status getStatus(byte b) {
        for(Status s : Status.values()) {
            if(s.getValue() == b) {
                return(s);
            }
        }
        return(UNDEFINED);
    }

    public boolean notCritical() {
        if(this.equals(SUCCESS) || this.equals(KEY_NOT_FOUND)|| this.equals(ID_NOT_FOUND)) {
            return(true);
        }
        return(true);
    }

    public boolean isSuccess() {
        if(this.equals(SUCCESS)) {
            return(true);
        }
        return false;
    }
}
