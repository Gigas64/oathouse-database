/*
 * @(#)DPHolder.java
 *
 * Copyright:	Copyright (c) 2010
 * Company:		Oathouse.com Ltd
 */
package com.oathouse.oss.storage.valueholder;

/**
 * The {@code DPHolder} Class allows for the storage and referencing of a day/period
 * set of values as a single integer value.
 *
 * @author Darryl Oatridge
 * @version 1.00 03-Aug-2010
 */
public class DPHolder {
    private static final int DP_FACTOR = 100000000;

    private int day;
    private int period;

    /**
     * Constructor to create the read only DpBean
     * @param day
     * @param period
     */
    public DPHolder(int day, int period) {
        this.day = day;
        this.period = period;
    }

    /**
     * This constructor allows a key value to be passed as a parameter
     * @param dp
     */
    public DPHolder(int dp) {
        this.day = getDay(dp);
        this.period = getPeriod(dp);
    }

    final public int getDay() {
        return day;
    }

    final public int getPeriod() {
        return period;
    }

    final public int getDP() {
        return getDP(day, period);
    }

    /* **********************************
     * S T A T I C   M E T H O D S
     * **********************************/
    public static int getDay(int dp) {
        return (dp / DP_FACTOR);
    }

    public static int getPeriod(int dp) {
        return (dp % DP_FACTOR);
    }

    public static int getDP(int day, int period) {
        int dFactor = day * DP_FACTOR;
        int pFactor = period;
        return (dFactor + pFactor);
    }

    /* **********************************
     * O V E R R I D E   M E T H O D S
     * **********************************/
    @Override
    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        }
        if(getClass() != obj.getClass()) {
            return false;
        }
        final DPHolder other = (DPHolder) obj;
        if(this.day != other.day) {
            return false;
        }
        if(this.period != other.period) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + this.day;
        hash = 59 * hash + this.period;
        return hash;
    }

}
