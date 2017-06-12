/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oathouse.oss.storage.objectstore.example;

import com.oathouse.oss.storage.objectstore.BuildBeanTester;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Darryl Oatridge
 */
public class ExampleBeanGroupTest {

    @Test
    public void test() throws Exception {
        String className = "com.oathouse.oss.storage.objectstore.example.ExampleGroupBean";
        BuildBeanTester.testObjectBean(className, false, true);
    }

}