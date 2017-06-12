/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/*
 * @(#)NioClientHandler.java
 *
 * Copyright:	Copyright (c) 2010
 * Company:		Oathouse.com Ltd
 */
package com.oathouse.oss.server.handler;

import com.oathouse.oss.server.OssProperties;
import com.oathouse.oss.server.transport.Command;
import com.oathouse.oss.server.transport.Request;
import com.oathouse.oss.server.transport.Response;
import com.oathouse.oss.server.transport.Status;
import com.oathouse.oss.server.transport.codec.RequestEncoder;
import com.oathouse.oss.server.transport.codec.ResponseEncoder;
import com.oathouse.oss.storage.exceptions.PersistenceException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * The {@code NioClientHandler} Class
 *
 * @author Darryl Oatridge
 * @version 1.00 03-Jun-2011
 */
public class NioClientHandler implements ObjectClientHandlerInterface{

    public static final int CONNECT_TIMEOUT = 300;
    private final String host;
    private final int port;
    private final int portRange;
    private volatile SocketChannel channel = null;

    public NioClientHandler() {
        this.host = OssProperties.getInstance().getHost();
        this.port = OssProperties.getInstance().getPort();
        this.portRange = OssProperties.getInstance().getPortRange();
    }

    @Override
    public void connect() throws PersistenceException {
        SocketAddress socket = null;
        // try to connect three times just incase the server is busy
        int portLimit = port + portRange;
        for(int tryPort = port; tryPort <= portLimit; tryPort++) {
            socket = new InetSocketAddress(host, tryPort);
            if(openSocket(socket)) {
                return;
            }
        }
        throw new PersistenceException("Client error: Unable to connect to server on " +
                                                    host + ", port range " + port + " to " + portLimit);
    }

    private boolean openSocket(SocketAddress socket) {
        try {
            channel = SocketChannel.open(socket);
        } catch(IOException ioe) {
            return(false);
        }
        return(true);
    }

    @Override
    public void disconnect() {
        if(channel != null && channel.isOpen()) {
            try {
                channel.close();
            } catch(IOException ioe) {
                // force it by making channel null
                channel = null;
            }
        }
    }

    @Override
    public boolean isConnected() {
        if(channel != null && channel.isConnected()) {
            return true;
        }
        return false;
    }

    @Override
    public Response sendRequest(Request request) throws PersistenceException {
        if(!isConnected()) {
            throw new PersistenceException("Client error: Unable to send request, no Server connection");
        }
        Response response = null;
        int maxAttempts = 3;
        for(int attempts = 0; attempts <= maxAttempts; attempts++) {
            response = writeAttempt(request);
            if(response != null) {
                return response;
            }
         }
        throw new PersistenceException("Client error: write to server failed or timed out after " + maxAttempts + " attempts");
    }

    private Response writeAttempt(Request request) throws PersistenceException {
        try {
            channel.write(RequestEncoder.encode(request));
        } catch(IOException ioe) {
            throw new PersistenceException("Client error: Unable to write to server because " + ioe.getMessage());
        }
        if(request.getCmd() == Command.CLOSE || request.getCmd() == Command.EXIT) {
            return(new Response(Status.SUCCESS));
        }
        ByteBuffer data = ByteBuffer.allocateDirect(8 * 1024);

        try {
            channel.read(data);
        } catch(IOException ioe) {
            throw new PersistenceException("Client error: Unable to read from server because " + ioe.getMessage());
        }
        data.flip();
        int len = data.getShort();
        return ResponseEncoder.decode(data);
    }

}
