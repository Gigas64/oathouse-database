/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oathouse.oss.server.transport;

import com.oathouse.oss.server.transport.Status;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Darryl Oatridge
 */
public class OssStatusTest {

    @Before
    public void setUp() {
    }

    /**
     * Unit test: test the byte conversion works
     */
    @Test
    public void unit01_OssStatus() throws Exception {
        Status success = Status.SUCCESS;
        Status failure = Status.FAILURE;

        Byte bSuccess = success.getByteValue();
        Byte bFailure = failure.getByteValue();
        Byte undef = 99;

        assertEquals(Status.SUCCESS, Status.getStatus(bSuccess));
        assertEquals(Status.FAILURE, Status.getStatus(bFailure));
        assertEquals(Status.UNDEFINED, Status.getStatus(undef));
    }

}
