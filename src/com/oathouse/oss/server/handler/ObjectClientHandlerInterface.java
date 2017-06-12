/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oathouse.oss.server.handler;

import com.oathouse.oss.server.transport.Request;
import com.oathouse.oss.server.transport.Response;
import com.oathouse.oss.storage.exceptions.PersistenceException;

/**
 *
 * @author Darryl Oatridge
 */
public interface ObjectClientHandlerInterface {

    public void connect() throws PersistenceException;

    public void disconnect();

    public boolean isConnected();

    public Response sendRequest(Request request) throws PersistenceException;
}
