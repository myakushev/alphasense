package com.alphasense.backend.tests.assertion;

import com.alphasense.backend.client.core.utils.JSONUtils;
import com.alphasense.backend.client.core.utils.RegexpUtils;
import com.alphasense.backend.tests.utils.PlaceholderProcessor;
import com.google.common.base.Equivalence;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;


public class MapCustomEquivalence extends Equivalence {

    private static final int CHECK_RANGE_SECONDS = 2;

    @Override
    protected boolean doEquivalent(Object expectedValue, Object actualValue) {
        if ((expectedValue instanceof String)
                && RegexpUtils.isRegexpString(expectedValue.toString())
                && !JSONUtils.isJSON(expectedValue.toString())) {
            String regexp = RegexpUtils.extractRegexpFromExpectation(expectedValue.toString());
            return actualValue.toString().matches(regexp);
        } else if ((expectedValue instanceof String) && expectedValue.toString().startsWith("<~")) {
            String updatedExpectedValue = PlaceholderProcessor.replaceHoldersInValues
                    (expectedValue.toString());
            updatedExpectedValue = updatedExpectedValue.replaceAll("[<>~]", "");
            ZonedDateTime expectedDate = ZonedDateTime.parse(updatedExpectedValue);
            if (expectedDate.isAfter(tryParse(actualValue.toString()).minusSeconds(CHECK_RANGE_SECONDS)) &&
                    expectedDate.isBefore(tryParse(actualValue.toString()).plusSeconds(CHECK_RANGE_SECONDS))) {
                return true;
            }
            return false;
        } else if (JSONUtils.isJSON(expectedValue.toString())) {
            return Assertions.checkJsonsEquals(expectedValue.toString(), actualValue.toString());
        } else {
            return expectedValue.equals(actualValue);
        }
    }

    private ZonedDateTime tryParse(String dateString) {
        List<String> formatStrings = Arrays.asList(
                "yyyy-MM-dd HH:mm:ss.SSS",
                "yyyy-MM-dd HH:mm:ss.SS",
                "yyyy-MM-dd HH:mm:ss.S",
                "yyyy-MM-dd HH:mm:ss",
                "yyyy-MM-dd HH:mm:s");
        {
            for (String formatString : formatStrings) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatString).withZone(ZoneId.of("UTC"));
                    return ZonedDateTime.parse(dateString, formatter);
                } catch (DateTimeParseException e) {
                }
            }
            return ZonedDateTime.parse(dateString);
        }
    }

    @Override
    protected int doHash(Object o) {
        return 0;
    }
}