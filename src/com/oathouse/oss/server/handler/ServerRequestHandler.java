/*
 * @(#)ServerRequestHandler.java
 *
 * Copyright:	Copyright (c) 2010
 * Company:		Oathouse.com Ltd
 */
package com.oathouse.oss.server.handler;

import com.oathouse.oss.server.transport.Request;
import com.oathouse.oss.server.transport.Response;
import com.oathouse.oss.server.transport.Status;
import com.oathouse.oss.storage.exceptions.PersistenceException;
import org.apache.log4j.Logger;

/**
 * The {@code ServerRequestHandler} Class Handles all requests coming from the server
 *
 * @author Darryl Oatridge
 * @version 1.00 09-Apr-2011
 */
public class ServerRequestHandler {

    private static Logger logger = Logger.getLogger(ServerRequestHandler.class);
    private final FileRequestHandler handler;

    public ServerRequestHandler(String rootStorepath) {
        handler = new FileRequestHandler(rootStorepath, "", "");
    }

    public Response getResponse(Request request) {
        handler.setAuthority(request.getAuthority());
        handler.setManager(request.getManager());
        if(logger.isDebugEnabled()) {
            logger.debug(request.getAuthority() + ":" + request.getManager() + " -> " + request.getCmd().toString());
        }
        switch(request.getCmd()) {
            case ALIVE:
                return (new Response(Status.SUCCESS));
            case GET:
                return getFile(request);
            case GET_KEYS:
                return getKeys(request);
            case GET_IDS:
                return getFileIds(request);
            case GET_ORDER:
                return getOrder(request);
            case GET_ORDER_IDS:
                return getOrderIds(request);
            case SET:
                return setFile(request);
            case SET_ORDER:
                return setOrder(request);
            case REMOVE:
                return removeFile(request);
            case REMOVE_ORDER:
                return removeOrder(request);
            case REMOVE_ALL:
                return removeAll(request);
            case CLEAR:
                return clear(request);
            case CLOSE:
                // not sure what to do
            default:
                return (new Response(Status.CMD_NOT_HANDLED));
        }
    }

    private Response getFile(Request request) {
        try {
            String xml = handler.getObject(request.getKey(), request.getId());
            if(xml != null) {
                return (new Response(Status.SUCCESS, xml));
            }
            return(new Response(Status.ID_NOT_FOUND));
        } catch(PersistenceException pe) {
            return (new Response(Status.IO_ERROR));
        }
    }

    private Response getOrder(Request request) {
        try {
            String xml = handler.getOrder(request.getKey());
            if(xml != null) {
                return (new Response(Status.SUCCESS, xml));
            }
            return(new Response(Status.ID_NOT_FOUND));
        } catch(PersistenceException pe) {
            return (new Response(Status.IO_ERROR));
        }
    }

    private Response getKeys(Request request) {
        try {
            return (new Response(Status.SUCCESS, handler.getKeys()));
        } catch(PersistenceException persistenceException) {
            return (new Response(Status.IO_ERROR));
        }
    }

    private Response getOrderIds(Request request) {
        try {
            return (new Response(Status.SUCCESS, handler.getOrderIds()));
        } catch(PersistenceException persistenceException) {
            return (new Response(Status.IO_ERROR));
        }
    }

    private Response getFileIds(Request request) {
        try {
            return (new Response(Status.SUCCESS, handler.getObjectIds(request.getKey())));
        } catch(PersistenceException persistenceException) {
            return (new Response(Status.IO_ERROR));
        }
    }

    private Response setFile(Request request) {
        try {
            if(handler.setObject(request.getKey(), request.getId(), request.getData())) {
                return (new Response(Status.SUCCESS));
            } else {
                return (new Response(Status.FAILURE));
            }
        } catch(PersistenceException persistenceException) {
            return (new Response(Status.IO_ERROR));
        }
    }

    private Response setOrder(Request request) {
        try {
            if(handler.setOrder(request.getKey(), request.getData())) {
                return (new Response(Status.SUCCESS));
            } else {
                return (new Response(Status.FAILURE));
            }
        } catch(PersistenceException persistenceException) {
            return (new Response(Status.IO_ERROR));
        }
    }

    private Response removeFile(Request request) {
        try {
            if(handler.removeObject(request.getKey(), request.getId())) {
                return (new Response(Status.SUCCESS));
            } else {
                return (new Response(Status.FAILURE));
            }
        } catch(PersistenceException persistenceException) {
            return (new Response(Status.IO_ERROR));
        }
    }

    private Response removeOrder(Request request) {
        try {
            if(handler.removeOrder(request.getKey())) {
                return (new Response(Status.SUCCESS));
            } else {
                return (new Response(Status.FAILURE));
            }
        } catch(PersistenceException persistenceException) {
            return (new Response(Status.IO_ERROR));
        }
    }

    private Response removeAll(Request request) {
        try {
            if(handler.removeAllObjects(request.getKey())) {
                return (new Response(Status.SUCCESS));
            } else {
                return (new Response(Status.FAILURE));
            }
        } catch(PersistenceException persistenceException) {
            return (new Response(Status.IO_ERROR));
        }
    }

    private Response clear(Request request) {
        try {
            if(handler.clear()) {
                return (new Response(Status.SUCCESS));
            } else {
                return (new Response(Status.FAILURE));
            }
        } catch(PersistenceException persistenceException) {
            return (new Response(Status.IO_ERROR));
        }
    }
}
