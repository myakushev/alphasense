package com.alphasense.backend.client.core.utils;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utils for working with regular expressions.
 */
public final class RegexpUtils {

    // This prefix is used in test data files for comparing actual values with expected.
    // If string contains this prefix than actual string will be matched with expected by regexp.
    public static final String REGEXP_PREFIX = "regexp:";

    // Map of possible regexps to use in test data
    public static final Map<String, String> REGEXPS = ImmutableMap.<String, String>builder()
            .put("anyValue", "(.*)")
            .put("anyDigits", "(\\\\d+)")
            // regexp for matching instant date time (in MILLIS), e.g. '2014-12-12T23:43:01.123Z' or '2014-12-12T23:43:01Z'
            .put("anyDateTime", "(\\\\d{4}-\\\\d{2}-\\\\d{2}T\\\\d{2}:\\\\d{2}:\\\\d{2}?(\\\\.\\\\d{1,3})?Z)")
            // regexp for matching date time with zone, e.g. '2014-12-12'
            .put("anyDate", "(\\\\d{4}-\\\\d{2}-\\\\d{2})")
            .build();

    private RegexpUtils() {
        // utils class
    }

    /**
     * Extracts all group matches from string.
     *
     * @param input  string for extracting group matches
     * @param group  name of group for extracting
     * @param regexp regular expression with named groups
     * @return list of matches
     */
    public static List<String> extractGroupMatches(String input, Object group, String regexp) {
        List<String> matches = Lists.newArrayList();
        Pattern p = Pattern.compile(regexp);
        Matcher m = p.matcher(input);
        while (m.find()) {
            if (group instanceof Integer) {
                matches.add(m.group((int) group));
            } else if (group instanceof String) {
                matches.add(m.group(group.toString()));
            } else {
                throw new IllegalArgumentException("Invalid group type provided. Allowed: int or string, provided - " + group);
            }
        }
        return matches;
    }

    /**
     * Checks whether string contains special prefix for regexp or not.
     * This method is used for processing test data files with expected results
     *
     * @param input string for checking
     * @return true if string contains prefix, false - otherwise
     */
    public static boolean isRegexpString(String input) {
        return input.contains(RegexpUtils.REGEXP_PREFIX);
    }

    /**
     * Checks whether string matches regexp or not.
     *
     * @param input  string for checking
     * @param regexp regular expression
     * @return true if string contains prefix, false - otherwise
     */
    public static boolean isMatched(String input, String regexp) {
        return Pattern.compile(regexp).matcher(input).find();
    }

    /**
     * Extracts regexp value from string.
     * Regexp are used in test expectations (e.g. response body)
     *
     * @param expectedValue string with regexp
     * @return extracted regular expression or input with no changes if regexp not found in string
     * @see RegexpUtils#isRegexpString(String)
     */
    public static String extractRegexpFromExpectation(String expectedValue) {
        return expectedValue.replace(REGEXP_PREFIX, "").replace("\\\\", "\\").trim();
    }
}
