package com.oathouse.oss.server.client.example;

import com.oathouse.oss.server.OssProperties;
import com.oathouse.oss.storage.exceptions.PersistenceException;
import com.oathouse.oss.storage.objectstore.BeanBuilder;
import com.oathouse.oss.storage.objectstore.ObjectOrderMapStore;
import com.oathouse.oss.storage.objectstore.example.ExampleBean;
import org.apache.log4j.BasicConfigurator;

public class MultiStartClient extends Thread {
    public static int runCount = 0;
    public static int clearCount = 0;

    @Override
    public void run() {
        BasicConfigurator.configure();
        OssProperties props = OssProperties.getInstance();
        props.setStorePath("./datastore");
        props.setHost("oathouse.com");
        props.setPort(13667);
        props.setPortRange(3);
//        props.setHost("oathouse.com");
//        props.setPort(12667);
        props.setAuthority("MultiTest");
        props.setConnection(OssProperties.Connection.NIO);

        ObjectOrderMapStore<ExampleBean> manager = new ObjectOrderMapStore<ExampleBean>(this.getName());
//        System.out.println("Started manager [" + this.getName() + "] ...");
        try {
            manager.clear();
            manager.init();
            for(int i = 0; i < 10; i++) {
                manager.setObject(10, (ExampleBean) BeanBuilder.addBeanValues(new ExampleBean(), i));
            }
        } catch(Exception e) {
            runCount++;
            // System.out.println("Exception thrown [" + runCount + "] : " + e.getMessage());
        }
        try {
            manager.clear();
        } catch(PersistenceException ex) {
            clearCount++;
            // System.out.println("Exception clearing: " + ex.getMessage());

        }
        // System.out.println("Shutting down " + this.getName());
        try {
            manager.shutdown();
        } catch(PersistenceException ex) {
        }
    }

    public static void main(String[] args) throws Exception {
        //int nThreads = Integer.parseInt(args[0]);
        int nThreads = 100;
        MultiStartClient[] clients = new MultiStartClient[nThreads];
        for(int i = 0; i < nThreads; i++) {
            clients[i] = new MultiStartClient();
            clients[i].setName("manager[" + Integer.toString(i) + "]");
            clients[i].start();
        }
        for(int i = 0; i < nThreads; i++) {
            clients[i].join();
        }
        System.out.println("Shutdown with " + runCount + " run failures and "+ clearCount + " clear failures");
    }
}
