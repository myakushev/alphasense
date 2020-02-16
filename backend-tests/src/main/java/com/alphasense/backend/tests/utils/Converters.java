package com.alphasense.backend.tests.utils;

import javax.xml.bind.DatatypeConverter;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import static java.time.temporal.ChronoUnit.MILLIS;

public final class Converters {

    private Converters() {
        // utils class
    }

    public static String hexStringToBase64(String hexString) {
        return DatatypeConverter.printBase64Binary(
                DatatypeConverter.parseHexBinary(hexString));
    }

    public static String byteArrayToBase64(byte[] byteArray) {
        return DatatypeConverter.printBase64Binary(byteArray);
    }

    public static Map<String, String> mapValuesToString(Map<String, Object> map) {
        Map<String, String> newMap = new HashMap<>();
        map.forEach((k, v) -> {
            if (v instanceof Timestamp) {
                newMap.put(k, ((Timestamp) v).toInstant().truncatedTo(MILLIS).toString());
            } else {
                newMap.put(k, v == null ? "null" : v.toString());
            }
        });
        return newMap;
    }
}
