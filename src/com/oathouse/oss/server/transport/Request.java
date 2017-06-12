/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/*
 * @(#)RequestBean.java
 *
 * Copyright:	Copyright (c) 2010
 * Company:		Oathouse.com Ltd
 */
package com.oathouse.oss.server.transport;

import java.io.Serializable;

/**
 * The {@code RequestBean} Class
 *
 * @author Darryl Oatridge
 * @version 1.00 11-Apr-2011
 */
public class Request implements Serializable {
    private static final long serialVersionUID = 5682049266589470361L;

    private final Command cmd;
    private final String authority;
    private final String manager;
    private final int key;
    private final int id;
    private final String data;

    /**
     * Constructor used for ALIVE, GET_KEYS, GET_ORDER_IDS and CLEAR
     *
     * @param cmd the command enumeration
     * @param authority an identifier for the application instance
     * @param manager an identifier for the module instance
     */
    public Request(Command cmd, String authority, String manager) {
        this(cmd, authority, manager, -1, -1, "");
    }

    /**
     *  Constructor used for GET_IDS, GET_ORDER, REMOVE_ORDER and REMOVE_ALL
     *
     * @param cmd the command enumeration
     * @param authority an identifier for the application instance
     * @param manager an identifier for the module instance
     * @param key the group reference
     */
    public Request(Command cmd, String authority, String manager, int key) {
        this(cmd, authority, manager, key, -1, "");
    }

    /**
     *  Constructor used for GET, EXISTS and REMOVE
     *
     * @param cmd the command enumeration
     * @param authority an identifier for the application instance
     * @param manager an identifier for the module instance
     * @param key the group reference
     * @param id the object reference
     */
    public Request(Command cmd, String authority, String manager, int key, int id) {
        this(cmd, authority, manager, key, id, "");
    }

    /**
     * Constructor used for SET_ORDER
     *
     * @param cmd the command enumeration
     * @param authority an identifier for the application instance
     * @param manager an identifier for the module instance
     * @param key the group reference
     * @param data
     */
    public Request(Command cmd, String authority, String manager, int key, String data) {
        this(cmd, authority, manager, key, -1, data);
    }

    /**
     * Constructor for all the bean parameters used for SET
     *
     * @param cmd the command enumeration
     * @param authority an identifier for the application instance
     * @param manager an identifier for the module instance
     * @param key the group reference
     * @param id the object reference
     * @param data 
     */
    public Request(Command cmd, String authority, String manager, int key, int id, String data) {
        this.cmd = cmd;
        this.authority = authority != null ? authority : "";
        this.manager = manager != null ? manager : "";
        this.key = key;
        this.id = id;
        this.data = data != null ? data : "";
    }

    public String getAuthority() {
        return authority;
    }

    public Command getCmd() {
        return cmd;
    }

    public String getData() {
        return data;
    }

    public int getId() {
        return id;
    }

    public int getKey() {
        return key;
    }

    public String getManager() {
        return manager;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        }
        if(getClass() != obj.getClass()) {
            return false;
        }
        final Request other = (Request) obj;
        if(this.cmd != other.cmd) {
            return false;
        }
        if((this.authority == null) ? (other.authority != null) : !this.authority.equals(other.authority)) {
            return false;
        }
        if((this.manager == null) ? (other.manager != null) : !this.manager.equals(other.manager)) {
            return false;
        }
        if(this.key != other.key) {
            return false;
        }
        if(this.id != other.id) {
            return false;
        }
        if((this.data == null) ? (other.data != null) : !this.data.equals(other.data)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.cmd != null ? this.cmd.hashCode() : 0);
        hash = 97 * hash + (this.authority != null ? this.authority.hashCode() : 0);
        hash = 97 * hash + (this.manager != null ? this.manager.hashCode() : 0);
        hash = 97 * hash + this.key;
        hash = 97 * hash + this.id;
        hash = 97 * hash + (this.data != null ? this.data.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "RequestBean{" + " cmd=" + cmd + " authority=" + authority + " manager=" + manager + " key=" + key + " id=" + id + " data=" + data + '}';
    }
}
