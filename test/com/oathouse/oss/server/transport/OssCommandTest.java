/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oathouse.oss.server.transport;

import com.oathouse.oss.server.transport.Command;
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
public class OssCommandTest {
    @Before
    public void setUp() {
    }


    /**
     * Unit test: test the byte conversion works
     */
    @Test
    public void unit01_OssCommand() throws Exception {
        Command first = Command.ALIVE;
        Command last = Command.CLOSE;

        Byte bFirst = first.getByteValue();
        Byte bLast = last.getByteValue();
        Byte undef = 99;

        assertEquals(Command.ALIVE, Command.getCommand(bFirst));
        assertEquals(Command.CLOSE, Command.getCommand(bLast));
        assertEquals(Command.UNDEFINED, Command.getCommand(undef));
    }

}
