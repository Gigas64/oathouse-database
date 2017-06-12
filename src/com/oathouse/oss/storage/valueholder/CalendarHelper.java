package com.oathouse.oss.storage.valueholder;

import com.oathouse.oss.storage.exceptions.IllegalValueException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;


/**
 * Utility to allow application to have awareness of current date and time with
 * reference to YWDHolder and SDHOlder and to deal with conversion of
 * ywd to Calendar/timestamp and vice versa.
 *
 * @author Nick Maunder
 * @version 2.01 29-08-2010
 */
public class CalendarHelper {

    private TimeZone timeZone;
    private Locale locale;

    /**
     * The constructor needs to be provided with a recognised TimeZone name
     * (for example "Europe/London") in order to detach the application's time from the
     * time zone used by the server's operating system
     * @param timeZoneName
     * @param localeName
     */
    public CalendarHelper(String timeZoneName, String localeName) {

        this.timeZone = TimeZone.getTimeZone(timeZoneName == null || timeZoneName.isEmpty() ? "Europe/London" : timeZoneName);
        this.locale = new Locale(localeName == null || localeName.isEmpty() ? "en_GB" : localeName);
        CalendarStatic.setTimeZone(timeZoneName);
        CalendarStatic.setLocale(localeName);
    }

    /**
     * Provides an array of size 2 containing the first day of the month and the last day of the month as a ywd value
     * @param monthOffset example: "this" month is (0), "next" month is (1), "last" month is (-1)
     * @return array [0]-&gt;firstYwd [1]-&gt;lastYwd
     */
    public int[] getRelativeMonthStartEnd(int monthOffset) {
        int[] rtnArray = new int[2];
        Calendar c = getTzCal();
        c.add(Calendar.MONTH, monthOffset);
        c.set(Calendar.DAY_OF_MONTH, c.getActualMinimum(Calendar.DAY_OF_MONTH));
        rtnArray[0] = getYWD(c);
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        rtnArray[1] = getYWD(c);
        return rtnArray;
    }

    /**
     * Provides the equivalent of YWDHolder.getYW for weeks relative to "this"/current week.
     * Day is set to 0 - in other words this method provides a yw0
     * @param weekOffset example: "this" week is (0), "next" week is (1), "last" week is (-1)
     * @return yw0 YWDHolder key value
     */
    public int getRelativeYW(int weekOffset) {
        Calendar c = getTzCal();
        c.add(Calendar.WEEK_OF_YEAR, weekOffset);
        return YWDHolder.getYW(getYWD(c));
    }

    /**
     * Provides the equivalent of YWDHolder.getYW for weeks relative to a given yw0.
     * Day is set to 0 - in other words this method provides a yw0
     * @param yw0 the starting yw0 the offset should be from
     * @param weekOffset example: yw0 plus one week (1), yw0 minus one week (-1)
     * @return yw0 YWDHolder key value
     */
    public int getRelativeYW(int yw0, int weekOffset) {
        int ywd = YWDHolder.getYW(yw0);
        Calendar c = getTzCal(ywd);
        c.add(Calendar.WEEK_OF_YEAR, weekOffset);
        return YWDHolder.getYW(getYWD(c));
    }

    /**
     * Provides the equivalent of YWDHolder.getYWD for a day relative to "today".
     * In other words, this method provides a ywd.
     * @param dayOffset example: today is (0), tomorrow is (1), yesterday is (-1)
     * @return
     */
    public int getRelativeYWD(int dayOffset) {
        Calendar c = getTzCal();
        c.add(Calendar.DAY_OF_YEAR, dayOffset);
        return (getYWD(c));
    }

    /**
     * Provides the equivalent of YWDHolder.getYWD for a day relative to a given ywd.
     * In other words, this method provides a ywd.
     * @param ywd the starting ywd the offset should be from
     * @param dayOffset example:  ywd plus one day (1), ywd minus one day (-1)
     * @return
     */
    public int getRelativeYWD(int ywd, int dayOffset) {
        Calendar c = getTzCal(ywd);
        c.add(Calendar.DAY_OF_YEAR, dayOffset);
        return (getYWD(c));
    }

    /**
     * A convenience helper method that is the equivalent of getRelativeYWD(0). This
     * Method provides the YWD for today
     * @return the ywd for today
     */
    public int getToday() {
        return this.getRelativeYWD(0);
    }

    /**
     * Finds the most recent example of a date stored as a ywd (eg finds the last occurrence of
     * a birthday given a date of birth).  Brings a date of 29 Feb forward to most recent 28 Feb
     * @param ywd
     * @return
     */
    public int getRecentYWD(int ywd) {
        Calendar c = getTzCal(ywd);
        boolean leap = false;
        if(c.get(Calendar.MONTH)==1 && c.get(Calendar.DAY_OF_MONTH)==29) {
            ywd = YWDHolder.add(ywd, -1); // back to 28 Feb
            leap = true;
        }
        int aYearAgo = YWDHolder.add(getRelativeYWD(0), -1000);
        while(ywd<aYearAgo) {
            ywd = YWDHolder.add(ywd, 1000);
        }
        if(leap) {
            // see if the result year has a 29 Feb
            c = getTzCal(ywd);
            c.add(Calendar.DAY_OF_YEAR, 1);
            if(c.get(Calendar.DAY_OF_MONTH)==29) {
                ywd = YWDHolder.add(ywd, 1);
            }
        }
        return ywd;
    }

    /**
     * Converts a month/day pair into the most recent ywd.  Month is 0-11
     * @param mmDD
     * @param isMonthDayPair
     * @return
     * @throws IllegalValueException if the month/day pair is invalid of if 29 Feb is supplied
     */
    public int getRecentYWD(int mmDD, boolean isMonthDayPair) throws IllegalValueException {
        int month = mmDD/100;
        int dayOfMonth = mmDD%100;
        if(month==1 && dayOfMonth==29) {
            throw new IllegalValueException("the month/day pair supplied was 29 Feb", false);
        }
        Calendar c = getTzCal();
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        if(c.get(Calendar.MONTH)!=month || c.get(Calendar.DAY_OF_MONTH)!=dayOfMonth) {
            throw new IllegalValueException("the month/day pair supplied [" + mmDD + "] is invalid", false);
        }
        if(c.getTimeInMillis()>getTzCal().getTimeInMillis()) {
            c.set(Calendar.YEAR, c.get(Calendar.YEAR)-1);
        }
        return (getYWD(c.get(Calendar.YEAR), month, dayOfMonth));
    }

    /**
     * Finds the interval in days between two ywds
     * @param ywd1
     * @param ywd2
     * @return
     */
    public int getDayInterval(int ywd1, int ywd2) {
        return Math.round((float)(getTzCal(ywd2).getTimeInMillis()-getTzCal(ywd1).getTimeInMillis())/(float)(1000*60*60*24));
    }

    /**
     * Provides the number of weeks in a year per ISO 8601
     * @param year example 2010.  In 4-digit format.
     * @return the number of weeks in that year
     */
    public int getWeeksInYear(int year) {
        Calendar c = getTzCal();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, Calendar.DECEMBER);
        c.set(Calendar.DAY_OF_MONTH, 1);
        return(c.getActualMaximum(Calendar.WEEK_OF_YEAR));
    }

    /**
     * Gets an application-specific Gregorian Calendar from a ywd (YWDHolder.getYWD).
     * Uses ISO 8601 year/week reference and day 0 = Monday through to day 6 = Sunday (ISO days are 1-7)
     * Time zone is set to application time zone and first day of week is set to Monday.
     * @param ywd The YWDHolder key value
     * @return GregorianCalendar with time zone set to application time zone, Monday as day 0,
     * date set per year,week,day and time set to midnight at start of day
     */
    public Calendar getTzCal(int ywd) {
        YWDHolder ywdb = new YWDHolder(ywd);
        Calendar c = getTzCal();
        c.set(Calendar.YEAR, ywdb.getYear());
        c.set(Calendar.WEEK_OF_YEAR, ywdb.getWeek());
        // start of week (Monday = day 0 in our system, which needs to be 1 under ISO)
        int day = ywdb.getDay() + 2;
        if(day>6) {
            day-=7;
        }
        c.set(Calendar.DAY_OF_WEEK, day);
        return c;
    }

    /**
     * Uses ISO 8601 year/week reference and day 0 = Monday through to day 6 = Sunday
     * @param ywd
     * @return long timestamp for the start of the day referenced by ywd
     */
    public long getTimestamp(int ywd) {
        // uses internal method
        Calendar c = getTzCal(ywd);
        return (c.getTimeInMillis());
    }

    /**
     * Finds ywdKey from year, month (0-11, as java.util.Calendar) and day of month.
     * @param year
     * @param monthZeroToEleven
     * @param dayOfMonth
     * @return
     * @throws IllegalValueException
     */
    public int getYWD(int year, int monthZeroToEleven, int dayOfMonth) throws IllegalValueException {
        Calendar c = getTzCal();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, monthZeroToEleven);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        if(c.get(Calendar.YEAR)!=year || c.get(Calendar.MONTH)!=monthZeroToEleven || c.get(Calendar.DAY_OF_MONTH)!=dayOfMonth) {
            throw new IllegalValueException("invalid date: "+year+", "+monthZeroToEleven+", "+dayOfMonth, false);
        }
        return(getYWD(c));
    }

    /**
     * Finds a ywdKey for a timestamp to ISO 8601
     * @param timestamp
     * @return
     * @throws IllegalValueException
     */
    public int getYWDFromTimestamp(long timestamp) throws IllegalValueException {
        Calendar c = getTzCal();
        c.setTimeInMillis(timestamp);
        return (getYWD(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)));
    }

    /**
     * Finds a SDholder format start with a duration of 0 for a timestamp
     * @param timestamp
     * @return SDHolder value with zero duration
     * @throws IllegalValueException
     */
    public int getSDFromTimestamp(long timestamp) throws IllegalValueException {
        Calendar c = getTzCal();
        c.setTimeInMillis(timestamp);
        int startTime = c.get(Calendar.HOUR_OF_DAY) * 60 + c.get(Calendar.MINUTE);
        return SDHolder.getSD(startTime, 0);
    }

    /**
     * Finds ywdKey from a Calendar date.  The calendar date provided as a parameter needs to be
     * application specific - ie started from a getTzCal() before manipulation
     * @param date
     * @return a YWDHolder key value of the Calender date
     */
    public int getYWD(Calendar date) {
        int y = date.get(Calendar.YEAR);
        int w = date.get(Calendar.WEEK_OF_YEAR);
        int d = date.get(Calendar.DAY_OF_WEEK)-2;
        int m = date.get(Calendar.MONTH);
        // ISO 8601: if this is "week 53", it should not contain the next year's first thursday
        // if it does, then it is week 01 of next year
        // see http://en.wikipedia.org/wiki/ISO_week_date
        // NB MS Outlook uses US system not ISO 8601 (Calendar uses ISO 8601)
        if(w>51 && m==0) {
            y--;
        }
        if(m==11 && w==1) {
            y++;
        }
        if(d<0) {
            d += 7;
        }
        return(YWDHolder.getYWD(y, w, d));
    }

    /**
     * Provides an approximate yw0 from an integer number of months
     * @param months
     * @return
     */
    public int getApproxYW0(int months) {
        int year = 0;
        if(months>=12) {
            year = months/12;
            months = months%12;
        }
        int week;
        switch(months) {
            case 0:
                week = 0;
                break;
            case 1:
                week = 5;
                break;
            case 2:
                week = 9;
                break;
            case 3:
                week = 14;
                break;
            case 4:
                week = 18;
                break;
            case 5:
                week = 22;
                break;
            case 6:
                week = 27;
                break;
            case 7:
                week = 31;
                break;
            case 8:
                week = 35;
                break;
            case 9:
                week = 40;
                break;
            case 10:
                week = 44;
                break;
            case 11:
                week = 49;
                break;
            default:
                week = 0;
        }
        return YWDHolder.getYW(year, week);
    }

    /**
     * Finds a person's age in years [0] and months [1] today
     * @param dobYwd
     * @return
     */
    public int[] getAgeYearsMonths(int dobYwd) {
        return getAgeYearsMonths(dobYwd, false);
    }

    /**
     * Finds a person's age in years [0] and months [1], with future dob returning {0,0}
     * @param dobYwd
     * @param onYwd the ywd on which the age is to be determined
     * @return
     */
    public int[] getAgeYearsMonths(int dobYwd, int onYwd) {
        return getAgeYearsMonths(dobYwd, onYwd, false);
    }

    /**
     * Finds a person's age in years [0] and months [1] for today
     * @param dobYwd
     * @param futureAsNegatives if you want a time to go to a future dob, make this positive
     * @return
     */
    public int[] getAgeYearsMonths(int dobYwd, boolean futureAsNegatives) {
        return getAgeYearsMonths(dobYwd, getRelativeYWD(0), futureAsNegatives);
    }

    /**
     * Finds a person's age today in just months
     * @param dobYwd
     * @return
     */
    public int getAgeMonths(int dobYwd) {
        int[] ym = getAgeYearsMonths(dobYwd, false);
        return (12*ym[0] + ym[1]);
    }

    /**
     * Finds a person's age onYwd in just months
     * @param dobYwd
     * @param onYwd
     * @return
     */
    public int getAgeMonths(int dobYwd, int onYwd) {
        int[] ym = getAgeYearsMonths(dobYwd, onYwd, false);
        return (12*ym[0] + ym[1]);
    }

    /**
     * Finds a person's age in years [0] and months [1]
     * @param dobYwd
     * @param onYwd the ywd on which the age is to be determined
     * @param futureAsNegatives if you want a time to go to a future dob, make this positive
     * @return
     */
    public int[] getAgeYearsMonths(int dobYwd, int onYwd, boolean futureAsNegatives) {
        Calendar dob = getTzCal();
        long ageL = getTimestamp(onYwd) - getTimestamp(dobYwd);
        boolean neg = false;
        if(!futureAsNegatives && ageL<0) {
            int[] age = {0,0};
            return age;
        }
        if(ageL<0) {
            neg = true;
            ageL = getTimestamp(dobYwd) - System.currentTimeMillis();
        }
        dob.setTimeInMillis(ageL);
        int[] age = new int[2];
        if(!neg) {
            age[0] = dob.get(Calendar.YEAR) - 1970;
            age[1] = dob.get(Calendar.MONTH);
        }
        else {
            age[0] = 0 - (dob.get(Calendar.YEAR) - 1970);
            age[1] = 0 - (dob.get(Calendar.MONTH));
        }
        return age;
    }

    /**
     * Finds the number of weeks (including partial ones) between two ywds
     * Example 2010016 -&gt; 2010020 = 1 week difference
     * @param firstYwd
     * @param lastYwd
     * @return
     * @throws IllegalValueException
     */
    public int getWeekDifference(int firstYwd, int lastYwd) throws IllegalValueException {

        if(firstYwd > lastYwd) {
            throw new IllegalValueException("firstYwd>lastYwd", false);
        }

        int weeks = 0;
        int year0 = YWDHolder.getYear(firstYwd);
        int week0 = YWDHolder.getWeek(firstYwd);
        int year1 = YWDHolder.getYear(lastYwd);
        int week1 = YWDHolder.getWeek(lastYwd);

        // the first year
        if(year0 == year1) {
            return (week1 - week0);
        }
        weeks += (YWDHolder.weeksInYear(firstYwd) - week0);

        // the last year
        weeks += week1;

        // any years in between
        int yearDiff = year1 - year0;
        if(yearDiff > 1) {
            for(int i=0; i<(yearDiff - 1); i++) {
                year0++;
                weeks += YWDHolder.weeksInYear(year0);
            }
        }

        return weeks;
    }

    /* ***************************************************
     * R O O T   L E V E L   M E T H O D S
     * ***************************************************/
    /**
     * Root source of a new Gregorian Calendar with time zone set for this application,
     * first day of the week set to Monday, and time set to previous midnight of current day.
     * Public because used outside booking system
     * @return a new GregorianCalendar
     */
    public Calendar getTzCal() {
        Calendar c = new GregorianCalendar(timeZone, locale);
        c.setMinimalDaysInFirstWeek(4);
        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c;
    }

}