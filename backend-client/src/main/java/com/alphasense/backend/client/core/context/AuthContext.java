package com.alphasense.backend.client.core.context;

public class AuthContext {

    private SessionInfo sessionInfo;
    private AuthInfo authInfo;

    public AuthContext() {
        authInfo = new AuthInfo();
    }

    public void setSessionInfo(SessionInfo sessionInfo) {
        this.sessionInfo = sessionInfo;
    }

    public SessionInfo getSessionInfo() {
        return sessionInfo;
    }

    public AuthInfo getAuthInfo() {
        return authInfo;
    }

    public void setAuthInfo(AuthInfo authInfo) {
        this.authInfo = authInfo;
    }

    public void clear() {
        authInfo = new AuthInfo();
        sessionInfo = null;
    }
}
