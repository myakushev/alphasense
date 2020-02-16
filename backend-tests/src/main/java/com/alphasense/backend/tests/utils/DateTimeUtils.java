package com.alphasense.backend.tests.utils;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utils for working with date - time.
 */
public final class DateTimeUtils {

    public static final DateTimeFormatter DAY_MONTH_FORMATTER = DateTimeFormatter.ofPattern("dd MMM");
    public static final DateTimeFormatter YEAR_MONTH_DAY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter DATE_TIME_PARAM_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneId.of("UTC+00:00"));


    private DateTimeUtils() {
        // utils class
    }

    /**
     * Returns current zone date time.
     *
     * @return ZonedDateTime
     */
    public static ZonedDateTime getCurrentZoneDateTime() {
        return ZonedDateTime.now(ZoneId.of("UTC+00:00"));
    }

    /**
     * Returns current time.
     *
     * @return timestamp
     */
    public static Timestamp getCurrentTime() {
        return new Timestamp(System.currentTimeMillis());
    }
}
