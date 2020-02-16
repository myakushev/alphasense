package com.alphasense.backend.tests.config;

public class TimeoutsSettings {

    private int checkTimeout;
    private int checkPeriod;
    private int errorCaseCheckTimeout;
    private int implicitlyWaitTimeout;


    public int getCheckTimeout() {
        return checkTimeout;
    }

    public void setCheckTimeout(int checkTimeout) {
        this.checkTimeout = checkTimeout;
    }

    public int getCheckPeriod() {
        return checkPeriod;
    }

    public void setCheckPeriod(int checkPeriod) {
        this.checkPeriod = checkPeriod;
    }

    public int getErrorCaseCheckTimeout() {
        return errorCaseCheckTimeout;
    }

    public void setErrorCaseCheckTimeout(int errorCaseCheckTimeout) {
        this.errorCaseCheckTimeout = errorCaseCheckTimeout;
    }

    public int getImplicitlyWaitTimeout() {
        return implicitlyWaitTimeout;
    }

    public void setImplicitlyWaitTimeout(int implicitlyWaitTimeout) {
        this.implicitlyWaitTimeout = implicitlyWaitTimeout;
    }
}