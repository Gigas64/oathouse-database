package com.oathouse.oss.server.client.example;

import com.oathouse.oss.server.OssProperties;
import com.oathouse.oss.storage.exceptions.PersistenceException;
import com.oathouse.oss.storage.objectstore.BeanBuilder;
import com.oathouse.oss.storage.objectstore.ObjectOrderMapStore;
import com.oathouse.oss.storage.objectstore.example.ExampleBean;
import org.apache.log4j.BasicConfigurator;

public class MultiThreadClientExample extends Thread {
    public static int counter = 0;

    @Override
    public void run() {
        BasicConfigurator.configure();
        OssProperties props = OssProperties.getInstance();
        props.setStorePath("./datastore");
        props.setHost("localhost");
        props.setPort(21021);
//        props.setHost("oathouse.com");
//        props.setPort(12667);
        props.setAuthority("MultiTest");
        props.setConnection(OssProperties.Connection.NIO);

        ObjectOrderMapStore<ExampleBean> manager = new ObjectOrderMapStore<ExampleBean>(this.getName());
        System.out.println("Started manager [" + this.getName() + "] ...");
        try {
            manager.clear();
            manager.init();
            for(int i = 0; i < 1000; i++) {
                int key = (int) (Math.random() * 10);
                int ranKey = (int) (Math.random() * 10);
                int id = (int) (Math.random() * 50);
                int ranId = (int) (Math.random() * 50);
                int remKey = (int) (Math.random() * 20);
                long sleep = (long) (Math.random() * 1000 * 10);
                Thread.sleep(sleep);
                manager.setObject(key, (ExampleBean) BeanBuilder.addBeanValues(new ExampleBean(), id));
                if(manager.isIdentifier(ranKey, ranId)) {
                    manager.removeObject(ranKey, ranId);
                }
                if(manager.getAllKeys().contains(remKey)) {
                    manager.removeKey(remKey);
                    manager.init();
                }
            }
        } catch(Exception e) {
            counter++;
            System.out.println("Exception thrown [" + counter + "] : " + e.getMessage());
        }
        try {
            manager.clear();
        } catch(PersistenceException ex) {
            // System.out.println("Exception clearing: " + ex.getMessage());

        }
        System.out.println("Shutting down " + this.getName());
        try {
            manager.shutdown();
        } catch(PersistenceException ex) {
        }
    }

    public static void main(String[] args) throws Exception {
        //int nThreads = Integer.parseInt(args[0]);
        int nThreads = 200;
        MultiThreadClientExample[] clients = new MultiThreadClientExample[nThreads];
        for(int i = 0; i < nThreads; i++) {
            clients[i] = new MultiThreadClientExample();
            clients[i].setName("manager[" + Integer.toString(i) + "]");
            clients[i].start();
        }
        for(int i = 0; i < nThreads; i++) {
            clients[i].join();
        }
    }
}
