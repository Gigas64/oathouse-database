/*
 * @(#)YWDHolder.java
 *
 * Copyright:	Copyright (c) 2010
 * Company:		Oathouse.com Ltd
 */
package com.oathouse.oss.storage.valueholder;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * The {@code YWDHolder} Class allows for the storage and referring of Year Week Day values
 * and can generate a key value representative of the bean.
 *
 * @author Darryl Oatridge
 * @version 1.02 03-May-2010
 */
public class YWDHolder {
    /** The number of days in a week. Used for clarity of code when looping days in a week */
    public final static int DAYS_IN_WEEK = 7;
    /** An arbitrary ywd far in the future. Used when a date range wants to go on forever*/
    public final static int MAX_YWD = YWDHolder.getYWD(9999, 52, 6);
    /** an arbitrary ywd far in the past. Used when a date is needed far in the past */
    public final static int MIN_YWD = YWDHolder.getYWD(0, 1, 0);

    private int year;
    private int week;
    private int day;

    public YWDHolder(int year, int week, int day) {
        this.year = year;
        this.week = week;
        this.day = day;
    }

    public YWDHolder(int year, int week) {
        this.year = year;
        this.week = week;
        this.day = 0;
    }

    public YWDHolder(int ywd) {
        this.day = getDay(ywd);
        this.week = getWeek(ywd);
        this.year = getYear(ywd);
    }

    public int getYear() {
        return year;
    }

    public int getWeek() {
        return week;
    }

    public int getDay() {
        return day;
    }

    public int getYW() {
        return (getYWD(year, week, 0));
    }

    public int getYWD() {
        return (getYWD(year, week, day));
    }

    /* **********************************
     * U T I L I T Y   M E T H O D S
     * **********************************/
    public boolean inRange(int ywdStart, int ywdEnd) {
        if(this.getYWD() < ywdStart || this.getYWD() > ywdEnd) {
            return (false);
        }
        return (true);
    }

    public boolean before(int ywd) {
        return (this.getYWD() < ywd);
    }

    public boolean after(int ywd) {
        return (this.getYWD() > ywd);
    }

    public boolean equals(int ywd) {
        return (this.getYWD() == ywd);
    }

    public boolean beforeYW(int ywd) {
        return (this.getYW() < getYW(ywd));
    }

    public boolean afterYW(int ywd) {
        return (this.getYW() > getYW(ywd));
    }

    public boolean equalsYW(int ywd) {
        return (this.getYW() == getYW(ywd));
    }

    /* **********************************
     * S T A T I C   M E T H O D S
     * **********************************/
    public static int getDay(int ywd) {
        return (ywd % 10);
    }

    public static int getWeek(int ywd) {
        return (ywd % 1000 / 10);
    }

    public static int getYear(int ywd) {
        return (ywd / 1000);
    }

    public static int getYWD(int year, int week, int day) {
        int yFactor = year * 1000;
        int wFactor = week * 10;
        int dFactor = day;
        return (yFactor + wFactor + dFactor);
    }

    public static int getYW(int year, int week) {
        return (getYWD(year, week, 0));
    }

    public static int getYW(int ywd) {
        YWDHolder b = new YWDHolder(ywd);
        return (b.getYW());
    }

    /**
     * calculates the YWD value of a number of days that must be under 365 days
     * for example 18 days would be 2 weeks 4 days
     *
     * @param noOfDays the number of days to convert
     * @return a YWDHolder key value
     */
    public static int getYwdFromDays(int noOfDays) {
        int weeks = noOfDays / 7;
        int days = noOfDays % 7;
        return getYWD(0, weeks, days);
    }


    /**
     * Validates a ywd to ensure it is of a valid format
     *
     * @param ywd
     * @return
     */
    public static boolean isValid(int ywd) {
        if(ywd < 0) {
            return (false);
        }
        if(YWDHolder.getDay(ywd) > 6) {
            return (false);
        }
        if(YWDHolder.getWeek(ywd) < 1 || YWDHolder.getWeek(ywd) > weeksInYear(ywd)) {
            return (false);
        }
        if(YWDHolder.getYear(ywd) > 9999) {
            return (false);
        }
        return (true);
    }

    /**
     * Returns the maximum number of weeks for a given ywd year
     *
     * @param ywd the ywd of which you want the max weeks of that year
     * @return the maximum number of weeks
     */
    public static int weeksInYear(int ywd) {
        int year = YWDHolder.getYear(ywd);

        Locale locale = new Locale("en_GB");
        Calendar c = new GregorianCalendar(locale);
        c.setMinimalDaysInFirstWeek(4);
        c.setFirstDayOfWeek(Calendar.MONDAY);

        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, Calendar.DECEMBER);
        c.set(Calendar.DAY_OF_MONTH, 1);
        return (c.getActualMaximum(Calendar.WEEK_OF_YEAR));
    }

    /**
     * Adds a year,week,day interval (as a ywdKey) to a date given as year,week,day (as a ywdKey),
     * ensuring ISO 8601 week numbering is maintained and keeping days as 0-6 Mon-Sun per our system
     * @param ywd
     * @param ywdInterval
     * @return
     */
    public static int add(int ywd, int ywdInterval) {
        if(isValid(ywd) && YWDHolder.getDay(ywdInterval) < 7) {

            Locale locale = new Locale("en_GB");
            Calendar c = new GregorianCalendar(locale);
            c.setMinimalDaysInFirstWeek(4);
            c.setFirstDayOfWeek(Calendar.MONDAY);

            int year, week;
            YWDHolder ywdb = new YWDHolder(ywd);
            c.set(Calendar.YEAR, ywdb.getYear());
            c.set(Calendar.WEEK_OF_YEAR, ywdb.getWeek());

            // start of week Monday = day 0 in our system, which needs to be 2 for Calendar
            int day = ywdb.getDay() + 2;
            if(day > 6) {
                day -= 7;
            }
            c.set(Calendar.DAY_OF_WEEK, day);

            ywdb = new YWDHolder(ywdInterval);
            c.add(Calendar.YEAR, ywdb.getYear());
            c.add(Calendar.WEEK_OF_YEAR, ywdb.getWeek());
            c.add(Calendar.DAY_OF_WEEK, ywdb.getDay());
            // if the week number is over 51 and the month is January, still in previous year
            year = c.get(Calendar.YEAR);
            week = c.get(Calendar.WEEK_OF_YEAR);
            if(week > 51 && c.get(Calendar.MONTH) == 0) {
                year--;
            }
            if(week == 1 && c.get(Calendar.MONTH) == 11) {
                year++;
            }
            // convert back to our day numbering system
            day = c.get(Calendar.DAY_OF_WEEK) - 2;
            if(day < 0) {
                day += 7;
            }
            return (YWDHolder.getYWD(year, week, day));
        }
        throw new NumberFormatException("The YWD values given are not valid");
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
        final YWDHolder other = (YWDHolder) obj;
        if(this.year != other.year) {
            return false;
        }
        if(this.week != other.week) {
            return false;
        }
        if(this.day != other.day) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + this.year;
        hash = 97 * hash + this.week;
        hash = 97 * hash + this.day;
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder toStringBuilder = new StringBuilder();
        toStringBuilder.append("\nyear: ");
        toStringBuilder.append(year);
        toStringBuilder.append("\nweek: ");
        toStringBuilder.append(week);
        toStringBuilder.append("\nday: ");
        toStringBuilder.append(day);
        return toStringBuilder.toString();
    }
}
