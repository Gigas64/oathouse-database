
/*
 * @(#)ClientRequestHandler.java
 *
 * Copyright:	Copyright (c) 2010
 * Company:		Oathouse.com Ltd
 */
package com.oathouse.oss.server.handler;

import com.oathouse.oss.server.OssProperties;
import com.oathouse.oss.server.transport.Command;
import com.oathouse.oss.server.transport.Request;
import com.oathouse.oss.server.transport.Response;
import com.oathouse.oss.storage.exceptions.PersistenceException;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * The {@code ClientRequestHandler} Class
 *
 * @author Darryl Oatridge
 * @version 1.00 27-Apr-2011
 */
public class ClientRequestHandler implements ObjectRequestHandlerInterface{

    private final ObjectClientHandlerInterface client;
    private final String authority;
    private final String manager;

    public ClientRequestHandler(String manager) {

        switch(OssProperties.getInstance().getConnection()) {
            case NIO:
            default:
                this.client = new NioClientHandler();

        }
        this.authority = OssProperties.getInstance().getAuthority();
        this.manager = manager;
    }

    @Override
    public String getAuthority() {
        return authority;
    }

    @Override
    public String getManager() {
        return manager;
    }

    @Override
    public boolean isAlive() throws PersistenceException {
        if(!client.isConnected()) {
            try {
                client.connect();
            } catch(PersistenceException pe) {
                return(false);
            }
        }
        final Response response = client.sendRequest(new Request(Command.ALIVE, authority, manager));
        if(response.getStatus().isSuccess()) {
            return true;
        }
        if(response.getStatus().notCritical()) {
            return false;
        }
        throw new PersistenceException("Server Respnse Error: " + response.getStatus());
    }

    @Override
    public String getObject(int key, int identifier) throws PersistenceException {
        if(!client.isConnected()) {
            client.connect();
        }
        final Response response = client.sendRequest(new Request(Command.GET, authority, manager, key, identifier));
        if(response.getStatus().isSuccess()) {
            return response.getData();
        }
        if(response.getStatus().notCritical()) {
            return null;
        }
        throw new PersistenceException("Server Respnse Error: " + response.getStatus());
    }

    @Override
    public String getOrder(int key) throws PersistenceException {
        if(!client.isConnected()) {
            client.connect();
        }
        final Response response = client.sendRequest(new Request(Command.GET_ORDER, authority, manager, key));
        if(response.getStatus().isSuccess()) {
            return response.getData();
        }
        if(response.getStatus().notCritical()) {
            return null;
        }
        throw new PersistenceException("Server Respnse Error: " + response.getStatus());
    }

    @Override
    public Set<Integer> getKeys() throws PersistenceException {
        if(!client.isConnected()) {
            client.connect();
        }
        final Response response = client.sendRequest(new Request(Command.GET_KEYS, authority, manager));
        if(response.getStatus().isSuccess()) {
            return response.getValues();
        }
        if(response.getStatus().notCritical()) {
            return new ConcurrentSkipListSet<>();
        }
        throw new PersistenceException("Server Respnse Error: " + response.getStatus());
    }

    @Override
    public Set<Integer> getObjectIds(int key) throws PersistenceException {
        if(!client.isConnected()) {
            client.connect();
        }
        final Response response = client.sendRequest(new Request(Command.GET_IDS, authority, manager, key));
        if(response.getStatus().isSuccess()) {
            return response.getValues();
        }
        if(response.getStatus().notCritical()) {
            return new ConcurrentSkipListSet<>();
        }
        throw new PersistenceException("Server Respnse Error: " + response.getStatus());
    }

    @Override
    public Set<Integer> getOrderIds() throws PersistenceException {
        if(!client.isConnected()) {
            client.connect();
        }
        final Response response = client.sendRequest(new Request(Command.GET_ORDER_IDS, authority, manager));
        if(response.getStatus().isSuccess()) {
            return response.getValues();
        }
        if(response.getStatus().notCritical()) {
            return new ConcurrentSkipListSet<>();
        }
        throw new PersistenceException("Server Respnse Error: " + response.getStatus());
    }

    public Set<String> getAuthorities() throws PersistenceException {
        if(!client.isConnected()) {
            client.connect();
        }
        final Response response = client.sendRequest(new Request(Command.GET_AUTHORITIES, authority, manager));
        if(response.getStatus().isSuccess()) {
            return null;
        }
        if(response.getStatus().notCritical()) {
            return new ConcurrentSkipListSet<>();
        }
        throw new PersistenceException("Server Respnse Error: " + response.getStatus());
    }

    @Override
    public boolean setObject(int key, int identifier, String data) throws PersistenceException {
        if(!client.isConnected()) {
            client.connect();
        }
        final Response response = client.sendRequest(new Request(Command.SET, authority, manager, key, identifier, data));
        if(response.getStatus().isSuccess()) {
            return true;
        }
        if(response.getStatus().notCritical()) {
            return false;
        }
        throw new PersistenceException("Server Respnse Error: " + response.getStatus());
    }

    @Override
    public boolean setOrder(int key, String data) throws PersistenceException {
        if(!client.isConnected()) {
            client.connect();
        }
        final Response response = client.sendRequest(new Request(Command.SET_ORDER, authority, manager, key, data));
        if(response.getStatus().isSuccess()) {
            return true;
        }
        if(response.getStatus().notCritical()) {
            return false;
        }
        throw new PersistenceException("Server Respnse Error: " + response.getStatus());
    }

    @Override
    public boolean removeObject(int key, int identifier) throws PersistenceException {
        if(!client.isConnected()) {
            client.connect();
        }
        final Response response = client.sendRequest(new Request(Command.REMOVE, authority, manager, key, identifier));
        if(response.getStatus().isSuccess()) {
            return true;
        }
        if(response.getStatus().notCritical()) {
            return false;
        }
        throw new PersistenceException("Server Respnse Error: " + response.getStatus());
    }

    @Override
    public boolean removeOrder(int key) throws PersistenceException {
        if(!client.isConnected()) {
            client.connect();
        }
        final Response response = client.sendRequest(new Request(Command.REMOVE_ORDER, authority, manager, key));
        if(response.getStatus().isSuccess()) {
            return true;
        }
        if(response.getStatus().notCritical()) {
            return false;
        }
        throw new PersistenceException("Server Respnse Error: " + response.getStatus());
    }

    @Override
    public boolean removeAllObjects(int key) throws PersistenceException {
        if(!client.isConnected()) {
            client.connect();
        }
        final Response response = client.sendRequest(new Request(Command.REMOVE_ALL, authority, manager, key));
        if(response.getStatus().isSuccess()) {
            return true;
        }
        if(response.getStatus().notCritical()) {
            return false;
        }
        throw new PersistenceException("Server Respnse Error: " + response.getStatus());
    }

    @Override
    public boolean removeObjectArchive(int key, int identifier) throws PersistenceException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean clear() throws PersistenceException {
        if(!client.isConnected()) {
            client.connect();
        }
        final Response response = client.sendRequest(new Request(Command.CLEAR, authority, manager));
        if(response.getStatus().isSuccess()) {
            return true;
        }
        if(response.getStatus().notCritical()) {
            return false;
        }
        throw new PersistenceException("Server Respnse Error: " + response.getStatus());
    }

    @Override
    public void exit() throws PersistenceException {
        if(client.isConnected()) {
            client.sendRequest(new Request(Command.CLOSE, authority, manager));
            client.disconnect();
        }
    }

    /**
     * This can be used by an application to cleanly close the server
     * @throws PersistenceException
     */
    public void serverClose() throws PersistenceException {
        if(client.isConnected()) {
            client.sendRequest(new Request(Command.EXIT, authority, manager));
            client.disconnect();
        }
    }

    @Override
    public String getObjectArchive(int key, int identifier) throws PersistenceException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getOrderArchive(int key) throws PersistenceException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Set<Integer> getKeysArchive() throws PersistenceException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Set<Integer> getObjectIdsArchive(int key) throws PersistenceException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Set<Integer> getOrderIdsArchive() throws PersistenceException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean setObjectArchive(int key, int identifier, String data) throws PersistenceException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean setOrderArchive(int key, String data) throws PersistenceException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean removeOrderArchive(int key) throws PersistenceException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
