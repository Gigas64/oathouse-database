/*
 * @(#)SDBits.java
 *
 * Copyright:	Copyright (c) 2010
 * Company:		Oathouse.com Ltd
 */
package com.oathouse.oss.storage.valueholder;

/**
 * The {@code SDBits} Class match a bitwise helper to provides a numerical
 * logic values for comparison of periodSd
 * <p>
 * Logic follows the following bitwise logic <br>
 *  &amp; - true if both operands are true, otherwise false <br>
 *  ^ - true if both operands are different, otherwise false <br>
 *  | - false if both operands are false, otherwise, true <br>
 *</p>
 *
 * <p>
 * An example of how to build a bitmask for  might be:
 * </p>
 *
 * <blockquote>
 * <pre>
 *     int bitflagMask = BTypeFlag.ATTENDING + BTypeFlag.PRECHARGE;
 * </pre>
 * </blockquote>
 *
 * @author Darryl Oatridge
 * @version 1.00 16-Nov-2011
 */
public class SDBits extends AbstractBits {
    public static final int COMPARISON_ERROR = -1;

    public static final int START_BEFORE = (int) Math.pow(2, 1);
    public static final int START_EQUALS = (int) Math.pow(2, 2);
    public static final int START_INSIDE = (int) Math.pow(2, 3);
    public static final int START_AT_END = (int) Math.pow(2, 4);
    public static final int START_LINKED = (int) Math.pow(2, 5);
    public static final int START_AFTER_LINK = (int) Math.pow(2, 6);

    public static final int END_BEFORE_LINK = (int) Math.pow(2, 7);
    public static final int END_LINKED = (int) Math.pow(2, 8);
    public static final int END_AT_START = (int) Math.pow(2, 9);
    public static final int END_INSIDE = (int) Math.pow(2, 10);
    public static final int END_EQUALS= (int) Math.pow(2, 11);
    public static final int END_AFTER = (int) Math.pow(2, 12);
    // helper masks
    public static final int START_AFTER = START_LINKED | START_AFTER_LINK;
    public static final int END_BEFORE = END_LINKED | END_BEFORE_LINK;

    public static final int LINKED_TO = START_BEFORE | END_LINKED;
    public static final int LINKED_FROM = START_LINKED | END_AFTER;
    public static final int LINKED = LINKED_TO | LINKED_FROM;

    public static final int IN_PERIOD = START_EQUALS | START_INSIDE | START_AT_END | END_AT_START | END_INSIDE | END_EQUALS;

    public static final int IN_RANGE = START_BEFORE | IN_PERIOD | END_AFTER;

    public static final int AT_LEAST = START_BEFORE | START_EQUALS | END_EQUALS | END_AFTER;
    public static final int AT_MOST = IN_PERIOD;
    public static final int SOME_PART = IN_RANGE;


}
