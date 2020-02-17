package com.alphasense.backend.client.core.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static org.junit.Assert.*;

/**
 * Utils for processing JSON.
 */
public final class JSONUtils {

    private JSONUtils() {
        // utils class
    }

    /**
     * Formats JSON with pretty printer (JSON will be displayed not in one line).
     *
     * @param input string for formatting
     * @return formatted JSON or input without changes if input is not a JSON
     */
    public static String beautifyIfJSON(String input) {
        if (isJSON(input)) {
            ObjectMapper mapper = new ObjectMapper();
            Object obj;
            try {
                obj = mapper.readValue(input, Object.class);
                return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
            } catch (IOException e) {
                throw new RuntimeException("Failed to beatify json", e);
            }
        }

        return input;
    }

    /**
     * Checks if string is JSON.
     *
     * @param input string to check
     * @return true if input is JSON, false - otherwise
     */
    public static boolean isJSON(String input) {
        try {
            new JSONObject(input);
        } catch (JSONException ex) {
            try {
                new JSONArray(input);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }
}
