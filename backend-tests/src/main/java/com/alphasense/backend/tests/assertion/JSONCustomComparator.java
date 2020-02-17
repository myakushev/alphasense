package com.alphasense.backend.tests.assertion;

import com.alphasense.backend.client.core.utils.RegexpUtils;
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

    public JSONCustomComparator(JSONCompareMode mode) {
        super(mode);
    }

    /**
     * Compares two {@link Object}s on the provided path represented by {@code prefix} and
     * updates the result of the comparison in the {@code result} {@link JSONCompareResult} object.
     * <p>
     * If Object is String and starts with "regexp: " - regexp extracts and compares with actual result
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
        super.compareValues(prefix, expectedValue, actualValue, result);
    }
}
