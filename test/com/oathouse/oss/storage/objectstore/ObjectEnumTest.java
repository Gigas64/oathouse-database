package com.oathouse.oss.storage.objectstore;

import com.oathouse.oss.storage.objectstore.ObjectEnum;
import java.util.concurrent.ConcurrentSkipListSet;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Darryl
 */
public class ObjectEnumTest {

    /**
     * Test of value method, of class ObjectEnum.
     */
    @Test
    public void testValue() {
    }

    /**
     * Test of isMemory method, of class ObjectEnum.
     */
    @Test
    public void testIsMemory() {
    }

    /**
     * Test of isPersist method, of class ObjectEnum.
     */
    @Test
    public void testIsPersist() {
    }

    /**
     * Test of isStoreType method, of class ObjectEnum.
     */
    @Test
    public void testIsStoreType() {
    }

    /**
     * Test of generateIdentifier method, of class ObjectEnum.
     */
    @Test
    public void testGenerateIdentifier_Set() throws Exception {
        ConcurrentSkipListSet<Integer> exclusions = new ConcurrentSkipListSet<Integer>();
        assertEquals(-10,ObjectEnum.generateIdentifier(-10, exclusions));
        assertEquals(1,ObjectEnum.generateIdentifier(-9, exclusions));
        assertEquals(1,ObjectEnum.generateIdentifier(-7, exclusions));
        assertEquals(1,ObjectEnum.generateIdentifier(-6, exclusions));
        assertEquals(1,ObjectEnum.generateIdentifier(0, exclusions));
        assertEquals(1,ObjectEnum.generateIdentifier(1, exclusions));
        assertEquals(2,ObjectEnum.generateIdentifier(2, exclusions));
        assertEquals(1,ObjectEnum.generateIdentifier(1, null));
        assertEquals(2,ObjectEnum.generateIdentifier(2, null));

        assertEquals(1,ObjectEnum.generateIdentifier(exclusions));
        assertEquals(1,ObjectEnum.generateIdentifier(null));

        exclusions.add(1);
        assertEquals(2,ObjectEnum.generateIdentifier(1, exclusions));
        assertEquals(2,ObjectEnum.generateIdentifier(2, exclusions));
        exclusions.add(2);
        assertEquals(3,ObjectEnum.generateIdentifier(exclusions));
        exclusions.add(4);
        assertEquals(3,ObjectEnum.generateIdentifier(exclusions));
        exclusions.add(3);
        assertEquals(5,ObjectEnum.generateIdentifier(exclusions));
    }

}