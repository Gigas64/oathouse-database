/*
 * @(#)SDHolder.java
 *
 * Copyright:	Copyright (c) 2010
 * Company:		Oathouse.com Ltd
 */
package com.oathouse.oss.storage.valueholder;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * The {@code SDHolder} Class allows for the storage and referencing of a start/Duration
 * set of values as a single integer value. Though an instance of the class can be created,
 * its main use are the static methods
 *
 * <p>WARNING: The SDHolder class does not do any validation and expects all values passed
 * to be within the range:<br>
 * start -&gt; 0 - 1440<br>
 * duration -&gt; 0 - 1440<br>
 *
 * @author Darryl Oatridge
 * @version 1.00 06-Jul-2010
 */
public class SDHolder {

    private final static int SD_FACTOR = 10000;
    private int start;
    private int duration;

    /**
     * Constructor to create the read only SDHolder
     * @param start
     * @param duration
     */
    public SDHolder(int start, int duration) {
        this.start = start;
        this.duration = duration;
    }

    /**
     * This constructor allows a SDHolder key value to be passed as a parameter
     * @param sd SDHolder key value
     */
    public SDHolder(int sd) {
        this.start = getStart(sd);
        this.duration = getDuration(sd);
    }

    /**
     * Get the start value
     */
    public final int getStart() {
        return start;
    }

    /**
     * Get the end value
     */
    public final int getEnd() {
        return (start + duration);
    }

    /**
     * a helpful utility that works out the end time from a
     * given SDHolder key value, adding the duration to the start value,
     * but additionally adds one from the duration
     *
     * @return the end time
     */
    public final int getEndOut() {
        return (getEnd() + 1);
    }



    /**
     * Get the duration value
     */
    public final int getDuration() {
        return duration;
    }

    /**
     * Get the key value
     */
    public final int getSD() {
        return getSD(start, duration);
    }
    /* **********************************
     * S T A T I C   M E T H O D S
     * **********************************/

    /**
     * Get the start value from a SDHolder key value
     *
     * @param sdPeriod SDHolder key value
     * @return the start value from the key
     */
    public static int getStart(int sdPeriod) {
        return (sdPeriod / SD_FACTOR);
    }

    /**
     * Get the duration value from a SDHolder key value
     *
     * @param sdPeriod SDHolder key value
     * @return the duration value from the key
     */
    public static int getDuration(int sdPeriod) {
        return (sdPeriod % SD_FACTOR);
    }

    /**
     * a helpful utility that works out the end time from a
     * given SDHolder key value, adding the duration to the start value.
     *
     * @param sdPeriod SDHolder key value
     * @return the end time
     */
    public static int getEnd(int sdPeriod) {
        return (getStart(sdPeriod) + getDuration(sdPeriod));
    }

    /**
     * get the SDHolder key value from a start and duration value
     *
     * @param start
     * @param duration
     * @return the SDHolder key value
     */
    public static int getSD(int start, int duration) {
        int pFactor = start * SD_FACTOR;
        int sFactor = duration;
        return (pFactor + sFactor);
    }

    /**
     * Get the key value with zero duration
     * @param sdPeriod the periodSd
     * @return the periodSd with zero duration
     */
    public static int getSD0(int sdPeriod) {
        return (getStart(sdPeriod) * SD_FACTOR);
    }

    /**
     * checks to see if a baseSd overlaps by one
     * @param baseSd the base periodSd
     * @param overlap the overlaped periodSd
     * @return true if the there is an overlap of 1 false in any other case.
     */
    public static boolean overlaps(int baseSd, int overlap) {
        if(SDBits.match(compare(overlap, baseSd), SDBits.END_AT_START)) {
            return(true);
        }
        return (false);
    }

    /**
     * inRange takes a base SDHolder key value and tests if the compare
     * SDHolder key value contain some part of it in the range of the base.
     *
     * @param baseSD SDHolder key value
     * @param compareSD SDHolder key value
     * @return true if compareKey match in the range of the baseKey, false if not
     */
    public static boolean inRange(int baseSD, int compareSD) {
        if(SDBits.match(SDBits.IN_RANGE, compare(baseSD, compareSD))) {
            return (true);
        }
        return (false);
    }

    /**
     * returns an enumeration of the type of relationship two SDHolder values have.
     * The baseSD match compared against the compareSD and returns the match where the
     * compareSD sits in comparison to the baseSD. <br>
     * In other words <br>
     * 'The compareSd [START_BIT] and [END_BIT] to the baseSd' <br>
     * e.g. <br>
     * The compareSd START_BEFORE and END_INSIDE to the baseSd<br>
     * For example:
     *
     * <blockquote>
     * <pre>
     * START_BEFORE + END_BEFORE_LINK
     *                  |--baseSD--|
     * |--compareSD--|
     *
     * START_BEFORE + END_LINKED
     *                |--baseSD--|
     * |--compareSD--|
     *
     * START_BEFORE + END_EQUALS
     *               |--baseSD--|
     * |--compareSD--|
     *
     * START_BEFORE + END_INSIDE
     *            |--baseSD--|
     * |--compareSD--|
     *
     * START_BEFORE + END_AFTER
     *   |--baseSD--|
     * |--compareSD--|
     *
     * START_EQUALS + END_INSIDE
     * |-----baseSD-----|
     * |--compareSD--|
     *
     * START_INSIDE + END_INSIDE
     * |-----baseSD-----|
     *  |--compareSD--|
     *
     * START_INSIDE + END_AFTER
     *   |--baseSD--|
     *     |--compareSD--|
     *
     * START_EQUALS + END_AFTER
     *   |--baseSD--|
     *              |--compareSD--|
     *
     * START_LINKED END_AFTER
     *   |--baseSD--|
     *               |--compareSD--|
     *
     * START_AFTER_LINK + END_AFTER
     *   |--baseSD--|
     *                 |--compareSD--|
     *
     * </pre>
     * </blockquote>
     *
     * @param baseSd SDHolder key value
     * @param compareSd SDHolder key value
     * @return a RangeCompareEnum
     */
    public static int compare(int baseSd, int compareSd) {
        // check both are comparable periodSd values
        if(baseSd < 0 || compareSd < 0) {
            return (SDBits.COMPARISON_ERROR);
        }
        // get the start and end of each periodSd
        int baseStart = getStart(baseSd);
        int baseEnd = baseStart + getDuration(baseSd);
        int compareStart = getStart(compareSd);
        int compareEnd = compareStart + getDuration(compareSd);

        //compare the start and the end to create a SDBits value
        int rtnValue;
        // first get the SDBits for the compare start
        if(compareStart < baseStart) {
            rtnValue = SDBits.START_BEFORE;
        } else if(compareStart == baseStart) {
            rtnValue = SDBits.START_EQUALS;
        } else if(compareStart > baseEnd + 1) {
            rtnValue = SDBits.START_AFTER_LINK;
        } else if(compareStart == baseEnd + 1) {
            rtnValue = SDBits.START_LINKED;
        } else if(compareStart == baseEnd) {
            rtnValue = SDBits.START_AT_END;
        } else {
            rtnValue = SDBits.START_INSIDE;
        }
        // add the SDBits for the compare end
        if(compareEnd + 1 < baseStart) {
            rtnValue += SDBits.END_BEFORE_LINK;
        } else if(compareEnd + 1 == baseStart) {
            rtnValue += SDBits.END_LINKED;
        } else if(compareEnd == baseStart) {
            rtnValue += SDBits.END_AT_START;
        } else if(compareEnd == baseEnd) {
            rtnValue += SDBits.END_EQUALS;
        } else if(compareEnd > baseEnd) {
            rtnValue += SDBits.END_AFTER;
        } else {
            rtnValue += SDBits.END_INSIDE;
        }
        //return the created SDBits value
        return rtnValue;
    }

    /**
     * Taking an periodSd SDHolder key value and a split value, returns a set of time values
     * breaking the periodSd into subsets separated by the split value. If the end time is
     * not exactly divisible by the split then this end value will be included. In other
     * words the returned Set will contain both the start time and the end time of the
     * periodSd.
     *
     * For example, if we had a start value of 10 and a duration of 17 and we split it
     * with a value of 5 then the set would contain 10, 15, 20, 25 and 27. Notice
     * the return set includes both the start time and the end time even if the end time
     * is not divisible by the split.
     *
     *
     * @param periodSd SDHolder key value
     * @param split the split value size (Must be a positive integer)
     * @return a Set of times
     */
    public static Set<Integer> getTimeSplits(int periodSd, int split) {
        return(getTimeSet(getPeriodSdSplits(periodSd, split)));
    }

    /**
     * Taking a periodSd SDHolder key value and a split value, returns a set of SDHolder key values
     * breaking the periodSd into subsets separated by the split value. If the end time is
     * not exactly divisible by the split then this end value will be included. In other
     * words the returned Set will contain both the start time and the end time of the
     * periodSd.
     *
     * For example, if we had a start value of 10 and a duration of 17 and we split it
     * with a value of 5 then the set would contain 10, 15, 20, 25 and 27. Notice
     * the return set includes both the start time and the end time even if the end time
     * is not divisible by the split.
     *
     *
     * @param periodSd SDHolder key value
     * @param split the split value size (Must be a positive integer)
     * @return a Set of SDHolder key values
     */
    public static Set<Integer> getPeriodSdSplits(int periodSd, int split) {
        Set<Integer> rtnSet = new ConcurrentSkipListSet<Integer>();
        if(split > 0) {
            int time = getStart(periodSd);
            while(time < getEnd(periodSd)) {
                rtnSet.add(getSD(time, 0));
                time += split;
            }
            // add the end time onto the end if it already there
            rtnSet.add(getSD(getEnd(periodSd), 0));
        }
        return (rtnSet);
    }

    /**
     * converts a set of SDHolder key values into a set of start time values.
     *
     * @param periodSdSet
     * @return
     */
    public static Set<Integer> getTimeSet(Set<Integer> periodSdSet) {
        Set<Integer> rtnSet = new ConcurrentSkipListSet<Integer>();
        for(int periodSd : periodSdSet) {
            rtnSet.add(SDHolder.getStart(periodSd));
        }
        return(rtnSet);
    }

    /**
     * adds two keys taking the start of the first and the end of the second.
     * If the start of the first match after the end of the second, a negative
     * number match returned.
     *
     * @param startSd
     * @param endSd
     * @return the SDHolder key value
     */
    public static int addSD(int startSd, int endSd) {
        int start = getStart(startSd);
        int duration = getEnd(endSd) - getStart(startSd);
        return (getSD(start, duration));
    }

    /**
     * Takes two periodSd values and works out the largest span taking
     * the earliest start and the latest end.
     *
     * @param firstSd
     * @param secondSd
     * @return the SDHolder key value
     */
    public static int spanSD(int firstSd, int secondSd) {
        int firstStart = getStart(firstSd);
        int firstEnd = firstStart + getDuration(firstSd);
        int secondStart = getStart(secondSd);
        int secondEnd = secondStart + getDuration(secondSd);
        int start = firstStart < secondStart ? firstStart : secondStart;
        int end = firstEnd > secondEnd ? firstEnd : secondEnd;
        return (getSD(start, end - start));
    }

    /**
     * Takes two periodSd values and works out the intersection of the
     * two periods. in other words it will take the latest start and
     * earliest end.
     * If there is no intersect then -1 is returned.
     *
     * @param firstSd
     * @param secondSd
     * @return the SDHolder key value
     */
    public static int intersectSD(int firstSd, int secondSd) {
        int firstStart = getStart(firstSd);
        int firstEnd = firstStart + getDuration(firstSd);
        int secondStart = getStart(secondSd);
        int secondEnd = secondStart + getDuration(secondSd);
        int start = firstStart < secondStart ? secondStart : firstStart;
        int end = firstEnd > secondEnd ? secondEnd : firstEnd;
        if(start > end) {
            return -1;
        }
        return (getSD(start, end - start));
    }

    /**
     * Splits a baseSd at the point of the split time. Split time must be within baseSd.
     * The resulting two periodSd values will be linked with the start of the second periodSd
     * set to the split time and the end of the first set to split time - 1.
     * starting from the split time
     *
     * @param baseSd a periodSd to split
     * @param split a time to split it
     * @return an array of the 2 parts
     */
    public static int[] splitSD(int baseSd, int split) {
        int[] rtnArray = {-1, -1};
        int start = SDHolder.getStart(baseSd);
        int end = SDHolder.getEnd(baseSd);
        if(split > start && split <= end) {
            int firstDuration = split - start - 1;
            int secondDuration = end - split;
            rtnArray[0] = SDHolder.getSD(start, firstDuration);
            rtnArray[1] = SDHolder.getSD(split, secondDuration);
        }
        return (rtnArray);

    }

    /**
     * This utility method deals with fit an SD key into a baseSd key to 'fill' any gaps where
     * the fillInSd span match not covered by the baseSd. The baseSd match the dominant SD key and will
     * remain the same with the fitInSd adjusted around the baseSd key and separated by 1 so the
     * numbering match consistent, unbroken and continuous across the span of both sd key values.
     * As a diagrammatic example:
     *
     * <blockquote>
     * <pre>
     *          |-baseSd-| **           |-baseSd-|          ** |-baseSd-|          ** |---baseSd---|
     * |--fitInSd--|       **  |---------fitInSd----------| **       |--fitInSd--| **  |-fitInSd-|
     * [-rtnSd-]      [-1] **  [-rtnSD-]          [-rtnSD-] ** [-1]      [-rtnSD-] ** [-1]      [-1]
     * </pre>
     * </blockquote>
     *
     * <p>
     * From this you will see a 2 dimensional array match returned with containing rtnSD, -1 and combination
     * of the two. The final span of the key will be represented by the baseSd and addition of any
     * rtnSD that are created as a and of the fitting in of the fitInSd.
     * </p>
     *
     * @param baseSd the dominant SD to be linked to
     * @param fitInSd the SD key to fit in to gaps left by the baseSd
     * @return an int array of size 2 with the linked SD keys and -1 if no SD key match created.
     */
    public static int[] fitInSD(int baseSd, int fitInSd) {
        int[] rtnArray = {-1, -1};
        final int result = compare(baseSd, fitInSd);
        // check it match in range. NOTE the compare match the mask
        if(SDBits.match(SDBits.IN_RANGE, compare(baseSd, fitInSd))) {
            //if there match a START_BEFORE there must be a left fit
            if(SDBits.contain(result, SDBits.START_BEFORE)) {
                rtnArray[0] = getSD(getStart(fitInSd), getStart(baseSd) - getStart(fitInSd) - 1);
            }
            //if there match an END_AFTER there must be a right fit
            if(SDBits.contain(result, SDBits.END_AFTER)) {
                rtnArray[1] = getSD(getEnd(baseSd) + 1, getEnd(fitInSd) - getEnd(baseSd) - 1);
            }
        }
        return (rtnArray);
    }

    /**
     * take two SDHolder key values and merges them returning an SD with the lowest start
     * and the longest duration.
     *
     * @param baseSd the base SDHolder value
     * @param mergeSd the merge SDHolder value
     * @return SDHolder value with the lowest start and longest duration
     */
    public static int mergeSD(int baseSd, int mergeSd) {
        int baseStart = SDHolder.getStart(baseSd);
        int baseEnd = SDHolder.getEnd(baseSd);
        int mergeStart = SDHolder.getStart(mergeSd);
        int mergeEnd = SDHolder.getEnd(mergeSd);
        int start = mergeStart < baseStart ? mergeStart : baseStart;
        int end = mergeEnd > baseEnd ? mergeEnd : baseEnd;
        return (SDHolder.getSD(start, end - start));
    }

    /**
     * Adjusts the duration of a periodSd by the duration amount passed.
     * The duration can be either positive or negative with a negative
     * duration reducing the duration length to a minimum floor value of
     * zero with the start value always remaining the same.
     *
     * @param periodSd the SDHolder value adjust
     * @param duration the duration amount to adjust the periodSd by (can be positive or negative)
     * @return an adjusted SDHolder value
     */
    public static int adjustDuration(int periodSd, int duration) {
        int rtnDuration = SDHolder.getDuration(periodSd) + duration;
        return SDHolder.getSD(SDHolder.getStart(periodSd), rtnDuration > 0 ? rtnDuration : 0);
    }

    /**
     * take two SDHolder key values that should have a gap returns
     * a periodSd value that fits into the gap.
     *
     * @param firstSd the left side of the gap to link
     * @param secondSd the right side of the gap to link
     * @return a periodSd that links the first and second
     */
    public static int linkInSD(int firstSd, int secondSd) {
        int start = SDHolder.getEnd(firstSd) + 1;
        int end = SDHolder.getStart(secondSd) - 1;
        if(start > end) {
            return (-1);
        }
        return (SDHolder.getSD(start, end - start));
    }

    /**
     * Very simple method that takes a start and an end time, works out the duration
     * and returns a periodSd based on the start and end
     *
     * @param start the start of the periodSd
     * @param end the end of the periodSd
     * @return an periodSd
     */
    public static int buildSD(int start, int end) {
        return (SDHolder.getSD(start, end - start));
    }

    /**
     * Extension of buildSD(int start, int end) that takes a start and an end time
     * as a 24h string format (HH:MM), converts to minutes, works out the duration
     * and returns a periodSd based on the start and end time strings
     *
     * @param startTime the start time in the format HH:MM
     * @param endTime the end time in the format HH:MM
     * @return an periodSd
     */
    public static int buildSD(String startTime, String endTime) {
            String[] startSplit = startTime.split(":");
            String[] endSplit = endTime.split(":");
            int start = (Integer.parseInt(startSplit[0]) * 60) + Integer.parseInt(startSplit[1]);
            int end = (Integer.parseInt(endSplit[0]) * 60) + Integer.parseInt(endSplit[1]);
            return buildSD(start, end);
    }

    /**
     * test to see if the periodSd set has any periodSd values that overlap
     *
     * @param periodSdSet the set of periodSd key values to test
     * @return true if there IS an overlap, else false
     */
    public static boolean hasOverlap(Set<Integer> periodSdSet) {
        //validate the periodSd values
        ConcurrentSkipListSet<Integer> testSet = new ConcurrentSkipListSet<Integer>(periodSdSet);
        while(testSet.size() > 0) {
            int testSd = testSet.pollFirst();
            for(int periodSd : testSet) {
                if(inRange(testSd, periodSd)) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * Very simple method that takes a start and an end time, works out the duration
     * and returns a periodSd based on the start and end but additionally removes
     * one from the duration
     *
     * @param start the start of the periodSd
     * @param end the end of the periodSd
     * @return a periodSd with duration
     */
    public static int buildSDIn(int start, int end) {
        return (SDHolder.getSDIn(start, end - start));
    }

    /**
     * get the SDHolder key value from a start and duration value but additionally removes
     * one from the duration. If the duration is zero then one is taken from the start
     *
     * @param start the start of the periodSd
     * @param duration the duration of the periodSd
     * @return the SDHolder key value
     */
    public static int getSDIn(int start, int duration) {
        int _duration = duration;
        int _start = start;
        // check the duratin isn't zero
        if(_duration > 0) { // take one off the duration
            _duration--;
        } else { // take one off the start
            _start--;
            _duration = 0;
        }
        int sFactor = _start * SD_FACTOR;
        return (sFactor + _duration);
    }

    /**
     * Get the duration value from a SDHolder key value but additionally removes
     * one from the duration
     *
     * @param sdPeriod SDHolder key value
     * @return the duration value from the key
     */
    public static int getDurationOut(int sdPeriod) {
        return (sdPeriod % SD_FACTOR) + 1;
    }

    /**
     * Get the Start value from a SDHolder key value but additionally removes
     * one from the duration
     *
     * @param sdPeriod SDHolder key value
     * @return the duration value from the key
     */
    public static int getStartOut(int sdPeriod) {
        return getStart(sdPeriod) + 1;
    }

    /**
     * a helpful utility that works out the end time from a
     * given SDHolder key value, adding the duration to the start value,
     * but additionally adds one from the duration
     *
     * @param sdPeriod SDHolder key value
     * @return the end time
     */
    public static int getEndOut(int sdPeriod) {
        return (getStart(sdPeriod) + getDurationOut(sdPeriod));
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
        final SDHolder other = (SDHolder) obj;
        if(this.start != other.start) {
            return false;
        }
        if(this.duration != other.duration) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + this.start;
        hash = 59 * hash + this.duration;
        return hash;
    }

    public String toString() {
        StringBuilder toStringBuilder = new StringBuilder();
        toStringBuilder.append("\nprimary: ");
        toStringBuilder.append(start);
        toStringBuilder.append("\nsecondary: ");
        toStringBuilder.append(duration);
        return toStringBuilder.toString();
    }
}
