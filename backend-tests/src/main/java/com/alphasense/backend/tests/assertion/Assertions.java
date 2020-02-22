package com.alphasense.backend.tests.assertion;

import com.alphasense.backend.client.core.rest.http.HttpResponseDecorator;
import com.alphasense.backend.client.core.utils.JSONUtils;
import org.junit.Assert;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.Matchers.is;

/**
 * Contains complex assertions.
 */
public class Assertions {

    private static final Logger logger = LoggerFactory.getLogger(Assertions.class);

    private static final String DIFF_FIELDS_FORMAT = "Field is different from expected. Expected: %s\n Actual: %s\n";

    private Assertions() {
        // utils class
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
     * Asserts that response status code is equal to expected.
     *
     * @param response       response
     * @param expectedStatus expected response status code
     */
    public static void assertResponseCodeIs(HttpResponseDecorator response, int expectedStatus) {
        Assert.assertThat("Response status code is different from expected", response.getStatusCode(),
                is(expectedStatus));
    }

}
