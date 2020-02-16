package com.alphasense.backend.client.core.utils;

import com.alphasense.backend.client.core.entity.Param;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.jayway.jsonpath.JsonPath;
import org.apache.commons.io.IOUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Utils for reading data used in test
 */
public final class DataReader {

    private static final String MATH_CALCULATION_REGEXP =
            "\\$\\{(?<parameter>[^\\}]*)\\}\\s*(\\+|-)\\s*(?<increment>((\\d)+\\b|\\$\\{(?<parameter2>[^\\}]*)\\}))";

    private static final String SET_PARAMETER_REGEXP = "\\$\\{(?<key>[\\w]+) = (?<value>[^\\}]*)\\}";

    private DataReader() {
        // utils class
    }

    /**
     * Reads resource file as JSON. Converts params to map and substitutes parameters with values.
     *
     * @param pathToJson path to JSON file
     * @param params     parameters for substitution
     * @return parsed JSON as String
     * @see DataReader#readResource(String, Map)
     */
    public static String readJson(String pathToJson, Param... params) {
        if (params.length != 0) {
            Map<String, Object> paramsMap = Maps.newHashMap();
            Arrays.asList(params).forEach(p -> paramsMap.put(p.getName(), p.getValue()));
            return readResource(pathToJson, paramsMap);
        }
        return readResource(pathToJson);
    }


    /**
     * Reads file with data. Substitutes params patterns in file with their value from map.
     * Parameter - string which matches pattern: ${(.*)}.
     * E.g. ${token} will be replaced with params.get(token)
     *
     * @param pathToResource path to file in 'resources' folder
     * @param paramsMap      parameters map with params for substitution
     * @return test data as String
     */
    public static String readResource(String pathToResource, Map<String, Object> paramsMap) {
        String data = readResource(pathToResource);
        return substituteParamsInString(data, paramsMap);
    }

    /**
     * Substitutes params patterns in string with their value from map.
     * Parameter - string which matches pattern: ${(.*)}.
     * E.g. ${token} will be replaced with params.get(token)
     * If string contains math calculation with params ('${parameter} + digits' or '${parameter1} + ${parameter2}')
     * params will be replaced with values & corresponded math calculation will be done.
     * E.g. if parameter=100 & string contains '${parameter} + 100' it will be replaced with 200,
     * if parameter1=100 & parameter2=200 & string contains '${parameter1} + ${parameter2}' it will be replaced with 300.
     * Important: parameter should go first (100 +/- ${parameter} is not supported)
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
        if (containsMathCalculation(string)) {
            List<String> calculations = extractMathCalculation(string);
            for (String calculation : calculations) {
                String initialExpression = calculation;
                String expression = substituteParams(calculation, paramsMap);
                Integer increment = extractIncrement(expression);
                Integer source = extractSource(expression);
                if (increment != null && source != null) {
                    /*
                    This condition required for expressions starts from -, e.g. -100-200, or -100+200,
                    its just make the source value negative: if was 100, it becomes -100, in case when original expression contains -.
                    */
                    if (expression.startsWith("-")) {
                        source = -source;
                    }
                    /*
                    This condition required for decrement expressions like 200-100 or -200-100
                    */
                    if (initialExpression.contains("-")) {
                        string = string.replace(initialExpression, String.valueOf(source - increment));
                    }
                    /*
                    This condition required for increment expressions like 200+100 or -100+200
                    */
                    else if (initialExpression.contains("+")) {
                        string = string.replace(initialExpression, String.valueOf(source + increment));
                    } else {
                        throw new IllegalArgumentException("Provided operation is not supported. Expression: " + initialExpression);
                    }
                }
            }
        }
        return substituteParams(string, paramsMap);
    }

    /**
     * Substitutes params patterns in map value with their value from params map.
     * Parameter - string which matches pattern: ${(.*)}.
     * E.g. ${token} will be replaced with params.get(token)
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
     * Reads resource file as string. If source is JSON parses string as JSON string.
     *
     * @param pathToResource path to file in 'resources' folder
     * @return string with file content
     */
    public static String readResource(String pathToResource) {
        InputStream is = DataReader.class.getClassLoader().getResourceAsStream(pathToResource);
        if (is == null) {
            throw new IllegalArgumentException(String.format("Resource by '%s' path not found", pathToResource));
        }
        String data = new BufferedReader(new InputStreamReader(is)).lines()
                .parallel().collect(Collectors.joining("\n"));

        if (JSONUtils.isJSON(data)) {
            return JsonPath.parse(data).jsonString();
        }
        return data;
    }

    /**
     * Reading resource file as stream and convert it in to byte array depends on media type:
     * works with jpegs and mp4 video ONLY.
     *
     * @param pathToResource
     * @param docType
     * @return
     */
    public static byte[] readMediaResourceAsByteArray(String pathToResource, String docType) {
        InputStream is = DataReader.class.getClassLoader().getResourceAsStream(pathToResource);
        ByteArrayOutputStream byteStream;
        checkIfStreamNotNull(pathToResource);
        if (!docType.equals("video") & !docType.equals("document")) {
            throw new RuntimeException("Document type is wrong..Expected values are: video or document, but was: " + docType);
        }
        try {
            if (docType.equals("video")) {
                return IOUtils.toByteArray(is);
            } else {
                BufferedImage originalImage = ImageIO.read(is);
                byteStream = new ByteArrayOutputStream();
                ImageIO.write(originalImage, "jpg", byteStream);
                byteStream.toByteArray();
            }
        } catch (IOException e) {
            throw new RuntimeException("SomeThing went wrong during streaming file..", e);
        }
        return byteStream.toByteArray();
    }

    private static void checkIfStreamNotNull(String pathToResource) {
        InputStream is = DataReader.class.getClassLoader().getResourceAsStream(pathToResource);
        if (is == null) {
            throw new IllegalArgumentException(String.format("Resource by '%s' path not found", pathToResource));
        }
    }

    /**
     * Checks if resource exists.
     *
     * @param pathToResource path to file in 'resources' folder
     * @return string with file content
     */
    public static boolean resourceExists(String pathToResource) {
        return ClassLoader.getSystemClassLoader().getResource(pathToResource) != null;
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

    private static boolean containsMathCalculation(String input) {
        return RegexpUtils.isMatched(input, MATH_CALCULATION_REGEXP);
    }

    private static boolean containsSetParameterMatches(String input) {
        return RegexpUtils.isMatched(input, SET_PARAMETER_REGEXP);
    }

    private static List<String> extractMathCalculation(String input) {
        return RegexpUtils.extractGroupMatches(input, 0, MATH_CALCULATION_REGEXP);
    }

    private static List<String> extractSetParameterMatches(String input) {
        return RegexpUtils.extractGroupMatches(input, 0, SET_PARAMETER_REGEXP);
    }

    private static Integer extractIncrement(String input) {
        return extractTerm("increment", input);
    }

    private static Integer extractSource(String input) {
        return extractTerm("source", input);
    }

    private static Integer extractTerm(String termGroup, String input) {
        List<String> term = RegexpUtils.extractGroupMatches(
                input, termGroup, "\\b(?<source>(\\d)+)\\s*(\\+|-)\\s*(?<increment>(\\d)+)\\b");

        if (!term.isEmpty()) {
            if (term.size() != 1) {
                throw new IllegalArgumentException("Parameter with increment is wrong: " + input);
            }
            return Integer.parseInt(term.get(0));
        }

        return null;
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
