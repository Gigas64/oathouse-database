/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.oathouse.oss.server.handler;

import com.oathouse.oss.server.handler.ServerRequestHandler;
import com.oathouse.oss.server.transport.Status;
import com.oathouse.oss.server.transport.Response;
import com.oathouse.oss.server.transport.Command;
import com.oathouse.oss.server.transport.Request;
import com.oathouse.oss.storage.objectstore.BeanBuilder;
import com.oathouse.oss.storage.objectstore.ObjectDataOptionsEnum;
import com.oathouse.oss.storage.objectstore.example.ExampleBean;
import java.io.File;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Darryl Oatridge
 */
public class ServerRequestHandlerTest {
    private ServerRequestHandler handler;
    private String authority = "mo00do";
    private String manager = "testManager";

    @Before
    public void setUp() {
        String rootStorePath = "." + File.separator;
        handler = new ServerRequestHandler(rootStorePath);
    }

    @After
    public void tearDown() {
        Request request = new Request(Command.CLEAR, authority, manager);
        Response response = handler.getResponse(request);
        assertEquals(Status.SUCCESS, response.getStatus());
    }

    /**
     * System test:
     */
    @Test
    public void system01_ServerRequestHandler() throws Exception {
        Request request = new Request(Command.ALIVE, authority, manager);
        Response response = handler.getResponse(request);
        assertEquals(Status.SUCCESS, response.getStatus());
    }

    /**
     * Unit test: set and get file
     */
    @Test
    public void unit01_ServerRequestHandler() throws Exception {
        String xml = BeanBuilder.addBeanValues(new ExampleBean()).toXML(ObjectDataOptionsEnum.TRIMMED, ObjectDataOptionsEnum.COMPACTED);
        Request request = new Request(Command.SET, authority, manager, 1, 2, xml);
        Response response = handler.getResponse(request);
        assertEquals(Status.SUCCESS, response.getStatus());
        request = new Request(Command.GET, authority, manager, 1, 2);
        response = handler.getResponse(request);
        assertEquals(Status.SUCCESS, response.getStatus());
        assertEquals(xml, response.getData());
    }

    /**
     * Unit test: setOrder and getOrder file
     */
    @Test
    public void unit02_ServerRequestHandler() throws Exception {
        String xml = BeanBuilder.addBeanValues(new ExampleBean()).toXML(ObjectDataOptionsEnum.TRIMMED, ObjectDataOptionsEnum.COMPACTED);
        Request request = new Request(Command.SET_ORDER, authority, manager, 1, xml);
        Response response = handler.getResponse(request);
        assertEquals(Status.SUCCESS, response.getStatus());
        request = new Request(Command.GET_ORDER, authority, manager, 1, 2);
        response = handler.getResponse(request);
        assertEquals(Status.SUCCESS, response.getStatus());
        assertEquals(xml, response.getData());
    }

    /**
     * Unit test: test the getIds and getKeys
     */
    @Test
    public void unit03_ServerRequestHandler() throws Exception {
        for(int key = 1; key < 5; key++) {
            for(int id = 0; id < 10; id++) {
                String xml = BeanBuilder.addBeanValues(new ExampleBean(), id).toXML(ObjectDataOptionsEnum.TRIMMED, ObjectDataOptionsEnum.COMPACTED);
                Request request = new Request(Command.SET, authority, manager, key, id, xml);
                Response response = handler.getResponse(request);
                assertEquals(Status.SUCCESS, response.getStatus());
            }
        }
        Request request = new Request(Command.GET_KEYS, authority, manager);
        assertEquals(4, handler.getResponse(request).getValues().size());
        request = new Request(Command.GET_IDS, authority, manager, 1);
        assertEquals(10, handler.getResponse(request).getValues().size());
        request = new Request(Command.GET_IDS, authority, manager, 4);
        assertEquals(10, handler.getResponse(request).getValues().size());
        request = new Request(Command.GET_IDS, authority, manager, 6);
        assertEquals(0, handler.getResponse(request).getValues().size());
    }

    /**
     * Unit test: remove and remove all
     */
    @Test
    public void unit04_ServerRequestHandler() throws Exception {
        String xml = BeanBuilder.addBeanValues(new ExampleBean()).toXML(ObjectDataOptionsEnum.TRIMMED, ObjectDataOptionsEnum.COMPACTED);
        Request request = new Request(Command.SET, authority, manager, 1, 3, xml);
        assertEquals(Status.SUCCESS, handler.getResponse(request).getStatus());
        request = new Request(Command.SET, authority, manager, 1, 5, xml);
        assertEquals(Status.SUCCESS, handler.getResponse(request).getStatus());
        request = new Request(Command.GET, authority, manager, 1, 3);
        assertEquals(Status.SUCCESS, handler.getResponse(request).getStatus());
        request = new Request(Command.REMOVE, authority, manager, 1, 3);
        assertEquals(Status.SUCCESS, handler.getResponse(request).getStatus());
        request = new Request(Command.GET, authority, manager, 1, 3);
        assertEquals(Status.ID_NOT_FOUND, handler.getResponse(request).getStatus());
        request = new Request(Command.REMOVE_ALL, authority, manager, 1);
        assertEquals(Status.SUCCESS, handler.getResponse(request).getStatus());
        request = new Request(Command.GET, authority, manager, 1, 3);
        assertEquals(Status.ID_NOT_FOUND, handler.getResponse(request).getStatus());
    }
}