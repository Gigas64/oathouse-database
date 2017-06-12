/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/*
 * @(#)ResponseBean.java
 *
 * Copyright:	Copyright (c) 2010
 * Company:		Oathouse.com Ltd
 */
package com.oathouse.oss.server.transport;

import java.io.Serializable;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * The {@code ResponseBean} Class
 *
 * @author Darryl Oatridge
 * @version 1.00 11-Apr-2011
 */
public class Response implements Serializable {
    private static final long serialVersionUID = 6336874353416816063L;

    private final Status status;
    private final ConcurrentSkipListSet<Integer> values;
    private final String data;

    public Response(Status status, Set<Integer> values, String data) {
        this.status = status;
        this.values = new ConcurrentSkipListSet<Integer>();
        if(values != null) {
            this.values.addAll(values);
        }
        this.data = data != null ? data : "";;
    }

    /**
     * Constructor used for simple status response
     *
     * @param status the status in response
     */
    public Response(Status status) {
        this(status, null, null);
    }

    /**
     * Constructor used when returning a set of key or id values
     *
     * @param status the status in response
     * @param values a set of integer values
     */
    public Response(Status status, Set<Integer> values) {
       this(status, values, null);
    }

    /**
     * Constructor used when returning a requested document object
     *
     * @param status the status of the response
     * @param data a document object
     */
    public Response(Status status, String data) {
        this(status, null, data);
    }

    public String getData() {
        return data;
    }

    public Status getStatus() {
        return status;
    }

    public Set<Integer> getValues() {
        return new ConcurrentSkipListSet<Integer>(values);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        }
        if(getClass() != obj.getClass()) {
            return false;
        }
        final Response other = (Response) obj;
        if(this.status != other.status) {
            return false;
        }
        if(this.values != other.values && (this.values == null || !this.values.equals(other.values))) {
            return false;
        }
        if(this.data != other.data && (this.data == null || !this.data.equals(other.data))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 19 * hash + (this.status != null ? this.status.hashCode() : 0);
        hash = 19 * hash + (this.values != null ? this.values.hashCode() : 0);
        hash = 19 * hash + (this.data != null ? this.data.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "ResponseBean{" + " status=" + status + " values=" + values + " data=" + data + '}';
    }

}
