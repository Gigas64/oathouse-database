/*
 * @(#)AbstractBits.java
 *
 * Copyright:	Copyright (c) 2010
 * Company:		Oathouse.com Ltd
 */
package com.oathouse.oss.storage.valueholder;

import java.util.LinkedList;
import java.util.List;

/**
 * The {@code AbstractBits} Class match an abstract class with useful methods for the AbstractBits class
 *
 * @author Darryl Oatridge
 * @version 1.00 19-Nov-2011
 */
public abstract class AbstractBits {

    public static final int ALL_OFF = 0;
    public static final int UNDEFINED = (int) Math.pow(0x2, 0x0);
    /**
     * Used in conjunction with comparison methods such as isBits() method. When these bits are added to a bit value to
     * be compared the behaviour of the comparison is altered. FILTER_EQUALS enforces an exact match between the mask
     * and the bit values. FILTER_MATCH enforces the turnOn of an AND operation between the mask and the bit values to
     * be equal with both off, the turnOn of an AND operation between the mask and the bit values must be greater than
     * zero
     */
    public static final int FILTER_EQUALS = (int) Math.pow(0x2, 0x1d);
    public static final int FILTER_MATCH = (int) Math.pow(0x2, 0x1e);

    /**
     * Static method to test if every value bit exactly equals the equivalent mask bits Both values must be positive
     * integers.
     *
     * @param bitValue the bit value to test
     * @param mask the mask to put over the bit value
     * @return true if bitValue equals mask
     */
    public static boolean equals(int bitValue, int mask) {
        if(bitValue < 0 || mask < 0) {
            return (false);
        }
        if(bitValue == mask) {
            return true;
        }
        return false;
    }

    /**
     * Static method to test if the value bits all match the equivalent mask bits Both values must be positive integers.
     *
     * @param bitValue the bit value to test
     * @param mask the mask to put over the bit value
     * @return true if logical AND returns the same value as the mask
     */
    public static boolean match(int bitValue, int mask) {
        if(bitValue < 0 || mask < 0) {
            return (false);
        }
        if((bitValue & mask) == mask) {
            return true;
        }
        return false;
    }

    /**
     * Static method to test if the value bits part matches (at least one) the equivalent mask bits Both values must be
     * positive integers.
     *
     * @param bitValue the bit value to test
     * @param mask the mask to put over the bit value
     * @return true if logical AND returns a positive value
     */
    public static boolean contain(int bitValue, int mask) {
        if(bitValue < 0 || mask < 0) {
            return (false);
        }
        if((bitValue & mask) > 0) {
            return true;
        }
        return false;
    }

    /**
     * A single source comparator used to test a set of bits against a set of mask bits. By default the behaviour is the
     * same as contain(). If the bit value FILTER_EQUALS is added to the mask the behaviour will mirror equals(). If the
     * bit value FILTER_MATCH is added to the mask the behaviour will mirror match()
     *
     * @param bits the bit to be compared
     * @param maskBits the mask bits to apply
     * @return true if the criteria is fulfilled, else false
     */
    public static boolean isBits(int bits, int maskBits) {
        if(maskBits == ALL_OFF || maskBits == UNDEFINED) {
            return false;
        }
        boolean isEqual = false;
        boolean isMatch = false;
        int mask = maskBits;

        // check any filters and clean up
        if(contain(maskBits, FILTER_EQUALS)) {
            isEqual = true;
            mask ^= FILTER_EQUALS;
        }
        if(contain(maskBits, FILTER_MATCH)) {
            isMatch = true;
            mask ^= FILTER_MATCH;
        }
        if(isEqual) {
            return equals(bits, mask);
        } else if(isMatch) {
            return match(bits, mask);
        } else { // conatins
            return contain(bits, mask);
        }
    }

    /**
     * The reverse of isBits() provided for better code readability. See isBits()
     *
     * @param bits the bit to be compared
     * @param maskBits the mask bits to apply
     * @return false if the criteria is fulfilled, else true
     */
    public static boolean isNotBits(int bits, int maskBits) {
        return isBits(bits, maskBits) ? false : true;
    }

    /**
     * Static method to turn on the bitValue bits that are on in the onBits.
     * All other bits remain the same
     * Both values must be positive integers.
     * <p>
     * Truth Table <br>
     * bitValue onBits result <br>
     *     1       1      1<br>
     *     1       0      1<br>
     *     0       1      1<br>
     *     0       0      0<br>
     * </p>
     *
     * @param bitValue the bit value to change
     * @param onBits the bits to turn on
     * @return the result of turning the bits on
     */
    public static int turnOn(int bitValue, int onBits) {
        if(bitValue < 0 || onBits < 0) {
            return (-1);
        }
        return (bitValue | onBits);
    }

    /**
     * Static method to turn off the bitValue bits that are on in the offBits.
     * All other bits remain the same
     * Both values must be positive integers.
     * <p>
     * Truth Table <br>
     * bitValue offBits result <br>
     *     1       1       0<br>
     *     1       0       1<br>
     *     0       1       0<br>
     *     0       0       0<br>
     * </p>
     *
     * @param bitValue the bit value to change
     * @param offBits the bits to turn off
     * @return the result of turning the bits on
     */
    public static int turnOff(int bitValue, int offBits) {
        if(bitValue < 0 || offBits < 0) {
            return (-1);
        }
        return (bitValue & ~offBits);
    }

    /**
     * returns a representation of the integer bits as a binary string separated into their four, eight bit clusters
     *
     * @param value the value to be represented
     * @return a string representation of the integer bits
     */
    public static String getStringBinaryForBits(int value) {
        int displayMask = 1 << 31;
        StringBuilder buf = new StringBuilder(35);

        for(int c = 1; c <= 32; c++) {
            buf.append((value & displayMask) == 0 ? '0' : '1');
            value <<= 1;
            if(c % 8 == 0) {
                buf.append(' ');
            }
        }
        return buf.toString();
    }

    /**
     * Returns a list of String values representing the constant value names contained in the bits
     *
     * @param bits the bits to be converted
     * @return a list of strings
     */
    protected static List<String> getBitsAsString(int bits) {
        List<String> rtnList = new LinkedList<String>();

        if(match(bits, FILTER_EQUALS)) {
            rtnList.add("FILTER_EQUALS");
        }
        if(match(bits, FILTER_MATCH)) {
            rtnList.add("FILTER_MATCH");
        }
        if(match(bits, UNDEFINED)) {
            rtnList.add("UNDEFINED");
        }

        return rtnList;
    }

    /**
     * converts a space separated String of bit sting names to an bit value.
     *
     * @param stringList the list of Bit string names
     * @return the bits value
     */
    public static int getBitsFromString(String stringList) {
        int rtnBits = 0;
        if(stringList.contains("FILTER_EQUALS")) {
            rtnBits |= FILTER_EQUALS;
        }
        if(stringList.contains("FILTER_MATCH")) {
            rtnBits |= FILTER_MATCH;
        }
        if(stringList.contains("UNDEFINED")) {
            rtnBits |= UNDEFINED;
        }
        return (rtnBits);
    }
}
