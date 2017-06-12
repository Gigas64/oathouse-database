/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/*
 * @(#)NioServerHandler.java
 *
 * Copyright:	Copyright (c) 2010
 * Company:		Oathouse.com Ltd
 */
package com.oathouse.oss.server.handler;

import com.oathouse.oss.server.transport.Command;
import com.oathouse.oss.server.transport.Request;
import com.oathouse.oss.server.transport.Response;
import com.oathouse.oss.server.transport.codec.RequestEncoder;
import com.oathouse.oss.server.transport.codec.ResponseEncoder;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.log4j.Logger;

/**
 * The {@code NioServerHandler} Class
 *
 * @author Darryl Oatridge
 * @version 1.00 04-Jun-2011
 */
public class NioServerHandler {

    private static Logger LOGGER = Logger.getLogger(NioServerHandler.class);
    public static final int CONNECT_TIMEOUT = 3000;
    private ServerRequestHandler requestHandler;
    private ServerSocketChannel channel = null;
    private boolean done = false;
    private Selector selector;
    private int port;

    private static class ClientInfo {

        ByteBuffer inBuf = ByteBuffer.allocateDirect(1024 * 8);
        ByteBuffer outBuf;
        boolean outputPending = false;
        SocketChannel channel;
    }
    private Map<SocketChannel, ClientInfo> allClients = new ConcurrentHashMap<SocketChannel, ClientInfo>();

    public NioServerHandler(int port, String rootStorepath) {
        this.port = port;
        this.requestHandler = new ServerRequestHandler(rootStorepath);

    }

    public void startServer() throws IOException {
        channel = ServerSocketChannel.open();
        channel.configureBlocking(false);
        ServerSocket serverSocket = channel.socket();
        serverSocket.bind(new InetSocketAddress(port));
        selector = Selector.open();
        channel.register(selector, SelectionKey.OP_ACCEPT);
        if(LOGGER.isDebugEnabled()) {
            LOGGER.debug("Server started on port " + port);
        }
    }

    public synchronized void stopServer() throws IOException {
        done = true;
        channel.close();
        if(LOGGER.isDebugEnabled()) {
            LOGGER.debug("Server Stopped");
        }
    }

    protected synchronized boolean getDone() {
        return done;
    }

    public void run() {
        try {
            startServer();
        } catch(IOException ioe) {
            LOGGER.error("Can't Start Server: ", ioe);
            return;
        }
        while(!getDone()) {
            try {
                if(selector.select(CONNECT_TIMEOUT) == 0) {
                    continue;
                }
            } catch(IOException ioe) {
                LOGGER.error("Server Error: Selector select failed. ", ioe);
                return;
            }
            Iterator<SelectionKey> it = selector.selectedKeys().iterator();
            while(it.hasNext()) {
                SelectionKey key = it.next();
                if(key.isReadable() || key.isWritable()) {
                    try {
                        handleClient(key);
                    } catch(IOException ioe) {
                        key.cancel();
                        LOGGER.error("Server Error: Client connection error. " + ioe.getMessage());
                    }
                } else if(key.isAcceptable()) {
                    if(LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Connection accepted");
                    }
                    try {
                        handleServer(key);
                    } catch(IOException ioe) {
                        LOGGER.error("Server Error: Unable to register client. ", ioe);
                    }
                }
                it.remove();
            }
        }

    }

    protected void handleServer(SelectionKey key) throws IOException {
        SocketChannel socketChannel = channel.accept();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
        registeredClient(socketChannel);
        if(LOGGER.isDebugEnabled()) {
            LOGGER.debug("Client registered");
        }

    }

    protected void registeredClient(SocketChannel sc) throws IOException {
        ClientInfo ci = new ClientInfo();
        ci.channel = sc;
        if(ci.outBuf!= null) {
            ci.outBuf.clear();
        }
        allClients.put(sc, ci);
    }

    protected void handleClient(SelectionKey key) throws IOException {
        SocketChannel sc = (SocketChannel) key.channel();
        ClientInfo ci = allClients.get(sc);
        if(ci == null) {
            LOGGER.error("Server Handler Error: No client registered on socket channel");
        }
        if(key.isWritable()) {
            send(sc, ci);
        }
        if(key.isReadable()) {
            recv(sc, ci);
        }
    }

    private void recv(SocketChannel sc, ClientInfo ci) throws IOException {
        if(ci.outputPending) {
            // must be a problem with sending so assume client dead
            ci.channel.close();
            allClients.remove(sc);
            throw new IOException("Server Error: pending items to send, assuming client dead");
        }
        ci.channel.read(ci.inBuf);
        ci.inBuf.flip();
        if(ci.inBuf.hasRemaining()) {
            int len = ci.inBuf.getShort();
            if(LOGGER.isTraceEnabled()) {
                LOGGER.trace("Bytes read/expected/capacity = " + ci.inBuf.remaining() + "/" + len + "/" + ci.inBuf.capacity());
            }
            Request request = RequestEncoder.decode(ci.inBuf);
            if(request.getCmd() == Command.CLOSE || request.getCmd() == Command.EXIT) {
                ci.channel.close();
                allClients.remove(sc);
                if(LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Client closed");
                }
                if(request.getCmd() == Command.EXIT) {
                    stopServer();
                }
                return;
            }
            Response response = requestHandler.getResponse(request);
            ci.outBuf = ResponseEncoder.encode(response);
            ci.inBuf.clear();
        }
        send(sc, ci);
    }

    private void send(SocketChannel sc, ClientInfo ci) throws IOException {
        int len = ci.outBuf.remaining();
        int nBytes = sc.write(ci.outBuf);
        if(LOGGER.isTraceEnabled()) {
            LOGGER.trace("Bytes write/written = " + len + "/" + nBytes);
        }
        if(nBytes != len) {
            // Client not ready to receive data
            ci.outputPending = true;
            ci.channel.register(selector,
                    SelectionKey.OP_READ | SelectionKey.OP_WRITE);
        } else {
            ci.outBuf.clear();
            if(ci.outputPending) {
                ci.outputPending = false;
                ci.channel.register(selector, SelectionKey.OP_READ);
            }
        }
    }
}
