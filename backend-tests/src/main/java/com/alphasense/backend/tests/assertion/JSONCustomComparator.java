package com.alphasense.backend.tests.assertion;

import com.alphasense.backend.client.core.utils.RegexpUtils;
import com.alphasense.backend.tests.utils.PlaceholderProcessor;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.JSONCompareResult;
import org.skyscreamer.jsonassert.comparator.DefaultComparator;

import java.time.Instant;

/**
 * Custom comparator used in {@link JSONAssert}.
 * Can compare values with regexp in expected string.
 */
public class JSONCustomComparator extends DefaultComparator {

    private static final int CHECK_RANGE_SECONDS = 2;

    public JSONCustomComparator(JSONCompareMode mode) {
        super(mode);
    }

    /**
     * Compares two {@link Object}s on the provided path represented by {@code prefix} and
     * updates the result of the comparison in the {@code result} {@link JSONCompareResult} object.
     * <p>
     * If Object is String and starts with "regexp: " - regexp extracts and compares with actual result
     * <p>
     * If Object stars with "<~" and looks like <~Date> then it transforms Date to yyyy-MM-dd'T'HH:mm:ss.SSS'Z' pattern
     * and compares with actual result
     * <p>
     * If Object stars with "<~" and looks like <~now+1s, yyyy-MM-dd'T'HH:mm:ss.SSS'Z'> then @replaceHoldersInValues
     * transform it to Date and compares with actual result
     *
     * @param prefix        the path in the json where the comparison happens
     * @param expectedValue Expected object
     * @param actualValue   Object to compare
     * @param result        result of the comparison
     */
    @Override
    public void compareValues(String prefix, Object expectedValue, Object actualValue, JSONCompareResult result) {
        if ((expectedValue instanceof String) && RegexpUtils.isRegexpString(expectedValue.toString())) {
            String regexp = RegexpUtils.extractRegexpFromExpectation(expectedValue.toString());
            if (!actualValue.toString().matches(regexp)) {
                result.fail(prefix, expectedValue, actualValue);
            }

            return;
        }
        if ((expectedValue instanceof String) && expectedValue.toString().startsWith("<~")) {
            String updatedExpectedValue = PlaceholderProcessor.replaceHoldersInValues
                    (expectedValue.toString());
            updatedExpectedValue = updatedExpectedValue.replaceAll("[<>~]", "");
            Instant expectedDate = Instant.parse(updatedExpectedValue);
            Instant actualDate = Instant.parse(actualValue.toString());
            // Following check is added for AB-2066 - need to check that time has MILLIS, not MICROS
            if (updatedExpectedValue.matches(RegexpUtils.REGEXPS.get("anyDateTime")) &&
                    !actualValue.toString().matches(RegexpUtils.REGEXPS.get("anyDateTime"))) {
                result.fail(prefix, expectedValue, actualValue);
            }
            if (expectedDate.isAfter(actualDate.minusSeconds(CHECK_RANGE_SECONDS)) &&
                    expectedDate.isBefore(actualDate.plusSeconds(CHECK_RANGE_SECONDS))) {
                return;
            }
            result.fail(prefix, expectedDate, actualDate);
            return;
        }

        super.compareValues(prefix, expectedValue, actualValue, result);
    }
}
