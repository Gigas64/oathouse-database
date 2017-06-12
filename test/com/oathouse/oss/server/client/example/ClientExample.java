/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/*
 * @(#)ClientExample.java
 *
 * Copyright:	Copyright (c) 2010
 * Company:		Oathouse.com Ltd
 */
package com.oathouse.oss.server.client.example;

import com.oathouse.oss.server.OssProperties;
import com.oathouse.oss.storage.exceptions.PersistenceException;
import com.oathouse.oss.storage.objectstore.BeanBuilder;
import com.oathouse.oss.storage.objectstore.ObjectMapStore;
import com.oathouse.oss.storage.objectstore.example.ExampleInheritBean;
import org.apache.log4j.BasicConfigurator;

/**
 * The {@code ClientExample} Class
 *
 * @author Darryl Oatridge
 * @version 1.00 13-Jun-2011
 */
public class ClientExample {

    public void run() throws InterruptedException {
        BasicConfigurator.configure();
        OssProperties props = OssProperties.getInstance();
        props.setStorePath("./datastore");
        props.setHost("oathouse.com");
        props.setPort(12667);
        props.setAuthority("MultiTest");
        props.setConnection(OssProperties.Connection.NIO);

        ObjectMapStore<ExampleInheritBean> manager = new ObjectMapStore<ExampleInheritBean>("ClientManager");
        //System.out.println("Started manager [" + this.getName() + "] ...");
        try {
            manager.clear();
            manager.init();
            manager.setObject(10, (ExampleInheritBean) BeanBuilder.addBeanValues(new ExampleInheritBean(), 1));
            ExampleInheritBean bean = manager.getObject(10,1);
            System.out.println(bean.toXML());
        } catch(Exception e) {
            System.out.println("Exception thrown: " + e.getMessage());
        }
        try {
            manager.clear();
        } catch(PersistenceException ex) {
            System.out.println("Exception thrown: " + ex.getMessage());
        }
        Thread.sleep(10 * 1000);
        try {
            manager.shutdown();
        } catch(PersistenceException ex) {
        }
    }


    public static void main(String[] args) throws Exception {
        ClientExample client = new ClientExample();
        client.run();
    }

}
