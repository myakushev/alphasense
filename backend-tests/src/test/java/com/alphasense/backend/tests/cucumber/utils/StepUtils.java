package com.alphasense.backend.tests.cucumber.utils;

import com.alphasense.backend.tests.cucumber.beans.TestContext;
import com.alphasense.backend.tests.utils.FileUtils;
import com.alphasense.backend.tests.utils.PlaceholderProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public final class StepUtils {

    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);

    public StepUtils() {

    }

    /**
     * Saves values of templateDataList element from feature's steps into context
     *
     * @param testContext      context of the test
     * @param templateDataList DataList from feature's steps
     */
    public static void putParamsIntoContext(TestContext testContext, List<Map<String, String>> templateDataList) {
        Map<String, String> row = templateDataList.get(0);
        for (String key : row.keySet()) {
            testContext.addParam(key, PlaceholderProcessor.replaceHoldersInValues(row.get(key),
                    testContext.getParams()));
        }
    }
}
