/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/*
 * @(#)RequestCmd.java
 *
 * Copyright:	Copyright (c) 2010
 * Company:		Oathouse.com Ltd
 */
package com.oathouse.oss.server.transport;

/**
 * The {@code RequestCmd} Enumeration used to identify the URI commands
 *
 * @author Darryl Oatridge
 * @version 1.00 31-Mar-2011
 */
public enum Command {
    UNDEFINED(-1),
    ALIVE(101),
    SET(102),
    SET_ORDER(103),
    GET(104),
    GET_IDS(105),
    GET_KEYS(106),
    GET_ORDER(107),
    GET_ORDER_IDS(108),
    GET_AUTHORITIES(109),
    REMOVE(110),
    REMOVE_ALL(111),
    REMOVE_ORDER(112),
    CLEAR(113),
    CLOSE(114),
    EXIT(115);

    private final int value;

    private Command(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public byte getByteValue() {
        return (byte) value;
    }

        public static Command getCommand(byte b) {
        for(Command s : Command.values()) {
            if(s.getValue() == b) {
                return(s);
            }
        }
        return(UNDEFINED);
    }

}
