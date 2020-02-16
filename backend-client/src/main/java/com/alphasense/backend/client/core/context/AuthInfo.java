package com.alphasense.backend.client.core.context;

import javax.xml.bind.DatatypeConverter;
import java.math.BigInteger;

public class AuthInfo {
    private BigInteger clientPrivateKey;
    private byte[] clientOpenKey;
    private byte[] serverOpenKey;
    private byte[] salt;
    private boolean isNewSalt;
    private String mguid;
    private BigInteger verifier;
    private byte[] sessionKey;
    private String csrf;
    private String serverCheckSum;
    private String clientCheckSum;
    private byte[] clientSessionKeyCheckSum;
    private byte[] serverTouchOpenKey;
    private byte[] touchToken;

    public BigInteger getClientPrivateKey() {
        return clientPrivateKey;
    }

    public void setClientPrivateKey(BigInteger clientPrivateKey) {
        this.clientPrivateKey = clientPrivateKey;
    }

    public byte[] getClientOpenKey() {
        return clientOpenKey;
    }

    public String getClientOpenKeyAsString() {
        return DatatypeConverter.printHexBinary(clientOpenKey);
    }

    public void setClientOpenKey(byte[] clientOpenKey) {
        this.clientOpenKey = clientOpenKey;
    }

    public byte[] getServerOpenKey() {
        return serverOpenKey;
    }

    public void setServerOpenKey(byte[] serverOpenKey) {
        this.serverOpenKey = serverOpenKey;
    }

    public byte[] getSalt() {
        return salt;
    }

    public String getSaltAsString() {
        return DatatypeConverter.printHexBinary(salt);
    }

    public void setSalt(byte[] salt) {
        this.salt = salt;
    }

    public void setIsNewSalt(boolean isNewSalt) {
        this.isNewSalt = isNewSalt;
    }

    public boolean isNewSalt() {
        return isNewSalt;
    }

    public String getMguid() {
        return mguid;
    }

    public byte[] getMguidAsByteArray() {
        return DatatypeConverter.parseHexBinary(mguid);
    }

    public void setMguid(String mguid) {
        this.mguid = mguid;
    }

    public BigInteger getVerifier() {
        return verifier;
    }

    public void setVerifier(BigInteger verifier) {
        this.verifier = verifier;
    }

    public byte[] getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(byte[] sessionKey) {
        this.sessionKey = sessionKey;
    }

    public String getCsrf() {
        return csrf;
    }

    public void setCsrf(String csrf) {
        this.csrf = csrf;
    }

    public String getServerCheckSum() {
        return serverCheckSum;
    }

    public void setServerCheckSum(String serverCheckSum) {
        this.serverCheckSum = serverCheckSum;
    }

    public String getClientCheckSum() {
        return clientCheckSum;
    }

    public void setClientCheckSum(String clientCheckSum) {
        this.clientCheckSum = clientCheckSum;
    }

    public byte[] getClientSessionKeyCheckSum() {
        return clientSessionKeyCheckSum;
    }

    public String getClientSessionKeyCheckSumAsString() {
        return DatatypeConverter.printHexBinary(clientSessionKeyCheckSum);
    }

    public void setClientSessionKeyCheckSum(byte[] clientSessionKeyCheckSum) {
        this.clientSessionKeyCheckSum = clientSessionKeyCheckSum;
    }

    public byte[] getServerTouchOpenKey() {
        return serverTouchOpenKey;
    }

    public void setServerTouchOpenKey(byte[] serverTouchOpenKey) {
        this.serverTouchOpenKey = serverTouchOpenKey;
    }

    public byte[] getTouchToken() {
        return touchToken;
    }

    public void setTouchToken(byte[] touchToken) {
        this.touchToken = touchToken;
    }
}
