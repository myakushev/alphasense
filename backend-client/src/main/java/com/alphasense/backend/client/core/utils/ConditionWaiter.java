package com.alphasense.backend.client.core.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public final class ConditionWaiter {

    private static final Logger logger = LoggerFactory.getLogger(ConditionWaiter.class);

    private static String errorMessage = "";

    private ConditionWaiter() {
        // utils class
    }

    /**
     * Waits for successful(with no exceptions) end of operation.
     * If after specified timeout operation doesn't end successfully (still throw exceptions),
     * operation is executed once again and exception is thrown for further processing
     *
     * @param operation   operation for which waiting mechanism should be used
     * @param timeout     wait timeout, milliseconds
     * @param checkPeriod how often should operation check condition, milliseconds
     */
    public static void waitForSuccess(Operation operation, int timeout, int checkPeriod) {
        errorMessage = "";
        boolean result;
        if (result = waitFor(() -> {
            try {
                operation.perform();
                return true;
            } catch (Throwable th) {
                logNewException(th.getMessage());

                return false;
            }
        }, timeout, checkPeriod)) {
            // success check, do nothing
        }
        if (!result) {
            operation.perform();
        }

    }

    /**
     * Block current thread till condition.checkCondition() returns false or timeout is not reached.
     *
     * @param condition   wait condition. If condition.checkCondition() returns true, thread will be released
     * @param timeout     wait timeout, milliseconds
     * @param checkPeriod how often should function check condition, milliseconds
     * @return true if condition.checkCondition() was true
     */
    public static boolean waitFor(Func condition, int timeout, int checkPeriod) {
        boolean result = false;
        for (int waitingFor = 0;
             waitingFor < timeout && !(result = condition.checkCondition());
             waitingFor += checkPeriod) {
            try {
                Thread.sleep(checkPeriod);
            } catch (InterruptedException ex) {
                return false;
            }
        }
        return result;
    }

    private static void logNewException(String message) {
        message = Objects.toString(message, "");
        if (!errorMessage.equals(message)) {
            logger.info("Operation is failed. Exception: {}", message);
            errorMessage = message;
        }
    }

    public interface Func {
        boolean checkCondition();
    }

    /**
     * Simple interface for operation run. Operation can throw RuntimeException
     */
    public interface Operation {

        /**
         * Performs given operation.
         */
        void perform();
    }
}
