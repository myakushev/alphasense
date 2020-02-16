package com.alphasense.backend.tests.assertion;

import com.alphasense.backend.client.core.rest.http.HttpResponseDecorator;
import com.alphasense.backend.client.core.utils.JSONUtils;
import com.alphasense.backend.client.core.utils.RegexpUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.json.JSONException;
import org.junit.Assert;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.alphasense.backend.client.core.utils.RegexpUtils.REGEXP_PREFIX;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

/**
 * Contains complex assertions.
 */
public class Assertions {

    private static final Logger logger = LoggerFactory.getLogger(Assertions.class);

    private static final String DIFF_FIELDS_FORMAT = "Field is different from expected. Expected: %s\n Actual: %s\n";

    private static final String SPACES = "( )+";

    private Assertions() {
        // utils class
    }

    /**
     * Asserts that JSON fields are equal. The field can be a plain string or a JSON.
     *
     * @param expected expected field value
     * @param actual   actual field value
     */
    public static void assertJSONFieldEquals(String expected, String actual) {
        if (JSONUtils.isJSON(expected)) {
            assertJSONEqualsStrictOrder(expected, actual);
        } else {
            assertJSONTextFieldEquals(expected, actual);
        }
    }

    /**
     * Asserts that JSONs are equal. JSONCompareMode - STRICT
     *
     * @param expectedJson expected JSON
     * @param actualJson   actual JSON
     */
    public static void assertJSONEqualsStrictOrder(String expectedJson, String actualJson) {
        assertJSONEquals(expectedJson, actualJson, JSONCompareMode.STRICT);
    }

    /**
     * Asserts that JSONs are equal. JSONCompareMode - LENIENT (non-strict array ordering and extensibility is allowed)
     *
     * @param expectedJson expected JSON
     * @param actualJson   actual JSON
     */
    public static void assertJSONEqualsNonStrictOrder(String expectedJson, String actualJson) {
        assertJSONEquals(expectedJson, actualJson, JSONCompareMode.LENIENT);
    }

    /**
     * Asserts that JSONs are equal. JSONCompareMode - NON_EXTENSIBLE (non-strict array ordering and extensibility is not allowed)
     *
     * @param expectedJson expected JSON
     * @param actualJson   actual JSON
     */
    public static void assertJSONEqualsNonExtensibleOrder(String expectedJson, String actualJson) {
        assertJSONEquals(expectedJson, actualJson, JSONCompareMode.NON_EXTENSIBLE);
    }

    /**
     * Asserts that JSONs are equal. JSONCompareMode - STRICT
     *
     * @param expectedJson expected JSON
     * @param actualJson   actual JSON
     * @param compareMode  JSON compare mode
     */
    public static void assertJSONEquals(String expectedJson, String actualJson, JSONCompareMode compareMode) {
        if (actualJson.equals("") || expectedJson.equals("")) {
            Assert.assertEquals("Jsons are not equal", expectedJson, actualJson);
            return;
        }
        JSONCustomComparator comparator = new JSONCustomComparator(compareMode);
        JSONAssert.assertEquals(String.format(
                DIFF_FIELDS_FORMAT, expectedJson, JSONUtils.beautifyIfJSON(actualJson)),
                expectedJson, actualJson, comparator);
    }

    /**
     * Asserts that JSON text fields are equal.
     * Text field may contain regexp, in this case asserts that
     * actual field value matches the pattern.
     *
     * @param expected expected field value (can be present by regexp)
     * @param actual   actual field value
     */
    public static void assertJSONTextFieldEquals(String expected, String actual) {
        if (RegexpUtils.isRegexpString(expected)) {
            String regexp = RegexpUtils.extractRegexpFromExpectation(expected);
            assertTrue(String.format(DIFF_FIELDS_FORMAT, regexp, actual), RegexpUtils.isMatched(actual, regexp));
        } else {
            Assert.assertEquals(String.format(DIFF_FIELDS_FORMAT, expected, actual), expected, actual);
        }
    }

    /**
     * Check expected json equals to actual
     *
     * @param expectedValue
     * @param actualValue
     * @return
     */
    public static boolean checkJsonsEquals(String expectedValue, String actualValue) {
        boolean isPresent = false;
        try {
            assertJSONFieldEquals(expectedValue, actualValue);
            isPresent = true;
        } catch (JSONException | AssertionError e) {
            logger.debug("Expected JSON is not present in actual JSONs");
        }
        return isPresent;
    }

    /**
     * Asserts that JSON is present in list of JSON strings. If not - AssertionError is thrown
     *
     * @param jsons        list of JSONs
     * @param expectedJson expected JSON
     */
    public static void assertJSONIsPresentInList(List<String> jsons, String expectedJson) {
        boolean isPresent = false;

        for (String json : jsons) {
            try {
                Assertions.assertJSONEqualsStrictOrder(expectedJson, json);
                isPresent = true;
            } catch (JSONException | AssertionError e) {
                logger.info("Expected JSON is not present in actual JSONs");
            }
        }

        if (!isPresent) {
            throw new AssertionError(
                    String.format("Expected JSON:\n%s\n is not present in actual JSONs:\n%s",
                            expectedJson, String.join("\n", jsons.stream()
                                    .map(JSONUtils::beautifyIfJSON)
                                    .collect(Collectors.toList()))
                    ));
        }
    }

    /**
     * Asserts that two string tokens are equal. Expected string can be regexp (should start with 'regexp' prefix).
     * This method should be used for single word comparison
     * (if there are several regexps in string, only the first one will be processed)
     *
     * @param expected expected string
     * @param actual   actual string
     */
    public static void assertStringTokensEquals(String expected, String actual) {
        assertTrue(String.format(DIFF_FIELDS_FORMAT, expected, actual), getCondition(expected, actual));
    }

    public static void assertStringTokensEquals(String expected, String actual, int index) {
        assertTrue(String.format(
                "Field is different from expected. Expected: %s\n Actual: %s\n Index - %d",
                expected, actual, index), getCondition(expected, actual));
    }

    private static boolean getCondition(String expected, String actual) {
        boolean condition;
        if (expected.contains(REGEXP_PREFIX)) {
            expected = RegexpUtils.extractRegexpFromExpectation(expected);
            condition = RegexpUtils.isMatched(actual, expected);
        } else {
            condition = expected.equals(actual);
        }
        return condition;
    }

    /**
     * Asserts that list of maps are equal.
     *
     * @param expectedList expected list of maps
     * @param actualList   actual list of maps
     */
    public static <K, V> void assertEquals(List<Map<K, V>> expectedList, List<Map<K, V>> actualList) {
        Assert.assertEquals(
                String.format("Sizes are not equal. Expected: %s\n, Actual: %s", expectedList, actualList),
                expectedList.size(),
                actualList.size());
        checkMapDifference(expectedList, actualList);
    }

    /**
     * Asserts that list of maps contains list of maps in the same order
     *
     * @param expectedList expected list of maps
     * @param actualList   actual list of maps
     */
    public static <K, V> void assertContains(List<Map<K, V>> expectedList, List<Map<K, V>> actualList) {
        List<Map<K, V>> actualListWithSameData = new ArrayList<>();
        boolean found = false;
        for (Map<K, V> kvMap : expectedList) {
            for (Map<K, V> map : actualList) {
                MapDifference mapDifference = Maps.difference(kvMap, map,
                        new MapCustomEquivalence());
                //If Maps are equals without asserting jsons, add actual equal map to the new list of maps for further compare
                if (mapDifference.areEqual()) {
                    found = true;
                    actualListWithSameData.add(map);
                }
            }
            assertTrue(
                    String.format("Compared values are not equal. Expected %s,\n actual %s", expectedList, actualList),
                    found);
        }

        //Filtering Out duplicated values from new collected map by replacing from List to Set. LinkedList used to save order.
        LinkedList<Map<K, V>> actualListWithOutDuplicates = Lists.newLinkedList(Sets.newLinkedHashSet(actualListWithSameData));
        //Comparing size of expected list of map with list of maps actual without duplicated values
        Assert.assertEquals(
                String.format("Result Sizes are not equal. Expected: %s\n, Actual: %s", expectedList, actualListWithOutDuplicates),
                expectedList.size(),
                actualListWithOutDuplicates.size());
        //Checking one more time all the maps values
        checkMapDifference(expectedList, actualListWithOutDuplicates);
    }

    private static <K, V> void checkMapDifference(List<Map<K, V>> expectedList, List<Map<K, V>> actualList) {
        for (int i = 0; i < expectedList.size(); i++) {
            MapDifference mapDifference = Maps.difference(expectedList.get(i), actualList.get(i),
                    new MapCustomEquivalence());
            assertTrue("Different values:" + mapDifference.entriesDiffering(),
                    mapDifference.entriesDiffering().size() == 0);
            assertTrue("Not found in actual result: " + mapDifference.entriesOnlyOnLeft(),
                    mapDifference.entriesOnlyOnLeft().size() == 0);
            assertTrue("Extra values in actual result: " + mapDifference.entriesOnlyOnRight(),
                    mapDifference.entriesOnlyOnRight().size() == 0);
        }
    }

    /**
     * Asserts that response status code is equal to expected.
     *
     * @param response       response
     * @param expectedStatus expected response status code
     */
    public static void assertResponseCodeIs(HttpResponseDecorator response, int expectedStatus) {
        Assert.assertThat("Response status code is different from expected", response.getStatusCode(),
                is(expectedStatus));
    }

    /**
     * Asserts that two texts are equal. Text is split into tokens & compared. Single token can be present by regexp
     *
     * @param expected expected text
     * @param actual   actual text
     */
    public static void assertTextEquals(String expected, String actual) {
        String[] expectedWords = expected.split(SPACES);
        String[] actualWords = actual.split(SPACES);

        if (expectedWords.length != actualWords.length) {
            fail(String.format(
                    "Number of words in expected text & actual are different. Expected - %d words, Actual - %d words. " +
                            "\nExpected: %s\nActual: %s",
                    expectedWords.length,
                    actualWords.length,
                    String.join(" ", expectedWords),
                    String.join(" ", actualWords)));
        }

        IntStream.range(0, expectedWords.length - 1)
                .forEach(i -> assertStringTokensEquals(expectedWords[i], actualWords[i], i));
        // this logic is added for comparing last word in pdf - file reader for expected file
        // cropps the last '\n' sign which is present in pdf file
        int lastIndex = expectedWords.length - 1;
        assertStringTokensEquals(expectedWords[lastIndex].replace("\n", ""), actualWords[lastIndex].replace("\n", ""), lastIndex);
    }

}
