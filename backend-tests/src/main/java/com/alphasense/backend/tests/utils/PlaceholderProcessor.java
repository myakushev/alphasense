package com.alphasense.backend.tests.utils;

import com.alphasense.backend.client.core.utils.DataReader;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaceholderProcessor {

    private static final String ATTR_PATTERN_TODAY_MILLIS_CONST = "ms";

    private static final String ATTR_PATTERN_TIME_SEC = "s";
    private static final String ATTR_PATTERN_TIME_MIN = "m";
    private static final String ATTR_PATTERN_TIME_HOUR = "h";
    private static final String ATTR_PATTERN_TIME_DAY = "d";
    private static final String ATTR_PATTERN_TIME_MONTH = "M";
    private static final String ATTR_PATTERN_TIME_YEAR = "y";

    private static final String ATTR_PATTERN_TIME_TODAY = "today";
    private static final String ATTR_PATTERN_TIME_TOMORROW = "tomorrow";
    private static final String ATTR_PATTERN_TIME_NOW = "now";
    private static final String ATTR_PATTERN_TIME_CURRENT_TIME_SECONDS = "cts";

    private static final Pattern patternDateTime = Pattern.compile
            ("<(~)?(?<patternTime>today|now|cts|tomorrow|" +
                    "(?<patternTimeParam>\\d{4}-\\d{2}-\\d{2}(\\s*|T)\\d{2}:\\d{2}:\\d{2}\\.*\\d{0,6}Z*))" +
                    "((?<offset>[+-]\\d+)(?<unit>s|m|h|d|y|M))?,\\s*(?<pattern>.+?)>");
    private static final Logger logger = LoggerFactory.getLogger(PlaceholderProcessor.class);
    private static Instant resultDate;

    /**
     * Replace placeholders in value. Placeholders:
     *
     * <b>&lt;today/now/currentTimeInSeconds</b>+-offset, java_date_format<b>&gt;</b>
     *
     * @param value  string value need to be transformed
     * @param params testContext params map
     * @return String contains date transformed to adjusted format
     */
    public static String replaceHoldersInValues(String value, Map<String, Object> params) {
        return replaceHoldersInValues(DataReader.substituteParamsInString(value, params));
    }

    public static String replaceHoldersInValues(String value) {
        if (value == null) {
            return null;
        }
        value = processTimePlaceholder(value);
        return value;
    }

    public static Map<String, Object> replaceHoldersInMap(Map<String, Object> valueMap, Map<String, Object> params) {
        Map<String, Object> returnMap = new LinkedHashMap<>();
        for (String s : valueMap.keySet()) {
            returnMap.put(s, replaceHoldersInValues(valueMap.get(s).toString(), params));
        }
        return returnMap;
    }

    /**
     * Transform resultDate to format or to milliseconds
     */
    private static String processTimePlaceholder(String value) {
        String initialValue = value;
        String[] holderAndFormat;
        while ((holderAndFormat = parseCalendarDateTime(value)) != null) {
            String dateString;
            if (ATTR_PATTERN_TODAY_MILLIS_CONST.equals(holderAndFormat[1])) {
                dateString = Long.toString(resultDate.toEpochMilli());
            } else {
                dateString = formatToUtc(holderAndFormat[1], resultDate);
            }
            value = value.replace(holderAndFormat[0], dateString);
            logger.info(String.format("Value %s replaced with %s", initialValue, value));
        }
        return value;
    }

    public static String formatToUtc(String formatPattern, Instant data) {
        return DateTimeFormatter.ofPattern(formatPattern).withZone(ZoneId.of("UTC")).format(data);
    }

    /**
     * Get java date format pattern from initial string
     *
     * @param string initial value
     * @return java date time format pattern
     */
    public static String getDateTimePattern(String string) {
        String[] result = parseCalendarDateTime(string);
        assert result != null;
        return result[1];
    }

    /**
     * Parses pattern to get offset and calculate date +-offset
     *
     * @param string initial value
     * @return Array where 1st element is full placeholder inside &lt; &gt; and 2nd element is pattern
     */
    private static String[] parseCalendarDateTime(String string) {
        Matcher matcher = patternDateTime.matcher(string);

        boolean found = matcher.find();
        if (!found) {
            return null;
        }

        String[] result = new String[2];
        result[0] = matcher.group();
        result[1] = matcher.group("pattern");

        Instant utc;
        String patternTimeParam = matcher.group("patternTimeParam");
        if (patternTimeParam != null) {
            LocalDateTime date = tryParse(patternTimeParam);
            utc = date.atZone(ZoneId.of("UTC")).toInstant();
        } else {
            switch (matcher.group("patternTime")) {
                case ATTR_PATTERN_TIME_TOMORROW: {
                    utc = Instant.now().plus(1, ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS);
                    break;
                }
                case ATTR_PATTERN_TIME_TODAY: {
                    utc = Instant.now().truncatedTo(ChronoUnit.DAYS);
                    break;
                }
                case ATTR_PATTERN_TIME_CURRENT_TIME_SECONDS: {
                    utc = Instant.now().truncatedTo(ChronoUnit.SECONDS);
                    break;
                }
                case ATTR_PATTERN_TIME_NOW:
                default:
                    utc = Instant.now();
            }
        }

        Instant date;
        if (matcher.group("offset") != null) {
            int offset = NumberUtils.toInt(matcher.group("offset"));
            String timeUnit = matcher.group("unit");
            switch (timeUnit == null ? ATTR_PATTERN_TIME_DAY : timeUnit) {
                case ATTR_PATTERN_TIME_SEC:
                    date = utc.plusSeconds(offset);
                    break;
                case ATTR_PATTERN_TIME_MIN:
                    date = utc.plus(offset, ChronoUnit.MINUTES);
                    break;
                case ATTR_PATTERN_TIME_HOUR:
                    date = utc.plus(offset, ChronoUnit.HOURS);
                    break;
                case ATTR_PATTERN_TIME_YEAR:
                    LocalDate localDate = utc.atZone(ZoneId.of("UTC")).toLocalDate();
                    date = localDate.plus(offset, ChronoUnit.YEARS).atStartOfDay().toInstant(ZoneOffset.UTC);
                    break;
                case ATTR_PATTERN_TIME_MONTH:
                    OffsetDateTime offsetDateTime = utc.atZone(ZoneId.of("UTC")).toOffsetDateTime();
                    date = offsetDateTime.plus(offset, ChronoUnit.MONTHS).toInstant();
                    break;
                case ATTR_PATTERN_TIME_DAY:
                default:
                    date = utc.plus(offset, ChronoUnit.DAYS);
            }
        } else {
            date = utc.plus(0, ChronoUnit.DAYS);
        }
        if (date != null) {
            resultDate = date;
        }
        return result;
    }

    private static LocalDateTime tryParse(String dateString) {
        List<String> formatStrings = Arrays.asList(
                "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'",
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                "yyyy-MM-dd'T'HH:mm:ss'Z'",
                "yyyy-MM-dd HH:mm:ss.SSS",
                "yyyy-MM-dd HH:mm:ss.SS",
                "yyyy-MM-dd HH:mm:ss.S",
                "yyyy-MM-dd HH:mm:ss",
                "yyyy-MM-dd HH:mm:s");
        {
            for (String formatString : formatStrings) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatString).withZone(ZoneId.of("UTC"));
                    return LocalDateTime.parse(dateString, formatter);
                } catch (DateTimeParseException e) {
                }
            }
            return LocalDateTime.parse(dateString);
        }
    }
}
