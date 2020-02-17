package com.alphasense.backend.client.core.utils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

/**
 * Utils for reading data used in test
 */
public final class DataReader {

    private static final String SET_PARAMETER_REGEXP = "\\$\\{(?<key>[\\w]+) = (?<value>[^\\}]*)\\}";

    private DataReader() {
        // utils class
    }

    /**
     * Substitutes params patterns in string with their value from map.
     * Parameter - string which matches pattern: ${(.*)}.
     * E.g. ${id} will be replaced with params.get(id)
     *
     * @param string    string with params
     * @param paramsMap parameters map with params for substitution
     * @return test data as String
     * @throws IllegalStateException if parameter is not present in map
     */
    public static String substituteParamsInString(String string, Map<String, Object> paramsMap) {
        if (containsSetParameterMatches(string)){
            List<String> settingStrings = extractSetParameterMatches(string);
            for(String settingString : settingStrings){
                String key = extractTerm("key", settingString, SET_PARAMETER_REGEXP);
                String value = extractTerm("value", settingString, SET_PARAMETER_REGEXP);
                paramsMap.put(key, value);
                string = string.replace(settingString, value);
            }
        }
        return substituteParams(string, paramsMap);
    }

    /**
     * Substitutes params patterns in map value with their value from params map.
     * Parameter - string which matches pattern: ${(.*)}.
     * E.g. ${id} will be replaced with params.get(id)
     *
     * @param map       map which contains values with params placeholders
     * @param paramsMap parameters map with params for substitution
     * @return test data as String
     */
    public static Map<String, String> substituteParamsInMap(Map<String, String> map, Map<String, Object> paramsMap) {
        Map<String, String> returnMap = new HashMap<>();

        for (String s : map.keySet()) {
            returnMap.put(s, substituteParamsInString(map.get(s), paramsMap));
        }
        return returnMap;
    }

    /**
     * Substitutes parameter in string with value. Parameter - substring which matches the pattern: ${(.*)}.
     * E.g. ${token} will be replaced with its value
     * If param is JSON field and param value is not string it shouldn't contain double quotes.
     * E.g. if JSON contains "limit": "${limitValue}" and limitValue is int (e.g. 4)
     * it will be replaced with "limit": 4, not "limit": "4"
     * If input matches  ${(.*)}+(\d)+ - parameter will be incremented with specified values
     * Currently only int values are supported
     *
     * @param input      string with parameters
     * @param paramName  name of parameter that should be substituted
     * @param paramValue value for substitution
     * @return string with substituted parameters
     */
    private static String substituteParamPattern(String paramName, Object paramValue, String input) {
        String paramAsJsonField = String.format(": \"${%s}\"", paramName);

        if (paramValue instanceof Collection<?>) {
            try {
                return input.replace(
                        paramAsJsonField,
                        String.format(":%s", new ObjectMapper().setSerializationInclusion(Include.NON_NULL)
                                .writeValueAsString(paramValue)));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error during params replacing", e);
            }
        }

        if (input.contains(paramAsJsonField) && !(paramValue instanceof String)) {
            return input.replace(paramAsJsonField, String.format(":%s", paramValue));
        }

        return input.replace(String.format("${%s}", paramName), paramValue.toString());
    }

    private static List<String> extractParamsNames(String input) {
        return RegexpUtils.extractGroupMatches(input, "parameter", "\\$\\{(?<parameter>[^\\}]*)\\}");
    }

    private static boolean containsSetParameterMatches(String input) {
        return RegexpUtils.isMatched(input, SET_PARAMETER_REGEXP);
    }

    private static List<String> extractSetParameterMatches(String input) {
        return RegexpUtils.extractGroupMatches(input, 0, SET_PARAMETER_REGEXP);
    }

    private static String extractTerm(String termGroup, String input, String regexp) {
        List<String> terms = RegexpUtils.extractGroupMatches(input, termGroup, regexp);
        if (!terms.isEmpty()) {
            if (terms.size() != 1) {
                throw new IllegalArgumentException(
                        String.format("Found not the only one match %s termGroup in input: %s", termGroup, input));
            }
            return terms.get(0);
        }
        return null;
    }

    private static String substituteParams(String input, Map<String, Object> paramsMap) {
        List<String> paramsNames = extractParamsNames(input);

        for (String paramName : paramsNames) {
            if (!paramsMap.containsKey(paramName)) {
                throw new IllegalStateException(
                        String.format("Failed to substitute param in '%s' json. " +
                                        "Key '%s' not found in params map. Keys in map: %s", input
                                , paramName, paramsMap.keySet()));
            }

            input = substituteParamPattern(paramName, paramsMap.get(paramName), input);
        }
        return input;
    }
}
