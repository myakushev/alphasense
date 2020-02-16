package com.alphasense.backend.client.core.context;

public class SessionInfo {

    private byte[] key;
    private byte[] token;

    public SessionInfo(byte[] key, byte[] token) {
        this.key = key;
        this.token = token;
    }

    public byte[] getKey() {
        return key;
    }

    public byte[] getToken() {
        return token;
    }
}
