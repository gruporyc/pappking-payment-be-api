package co.ppk.service;

public class MeatadataBO {
    private final String sessionId;
    private final String remoteIp;
    private final String cookie;
    private final String userAgent;

    public MeatadataBO(String sessionId, String remoteIp, String cookie, String userAgent) {
        this.sessionId = sessionId;
        this.remoteIp = remoteIp;
        this.cookie = cookie;
        this.userAgent = userAgent;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getRemoteIp() {
        return remoteIp;
    }

    public String getCookie() {
        return cookie;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public static class Builder {
        private String sessionId;
        private String remoteIp;
        private String cookie;
        private String userAgent;


        public MeatadataBO.Builder setSessionId(String sessionId) {
            this.sessionId = sessionId;
            return this;
        }

        public MeatadataBO.Builder setRemoteIp(String remoteIp) {
            this.remoteIp = remoteIp;
            return this;
        }

        public MeatadataBO.Builder setCookie(String cookie) {
            this.cookie = cookie;
            return this;
        }

        public MeatadataBO.Builder setUserAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        public MeatadataBO build() {
            return new MeatadataBO(sessionId, remoteIp, cookie, userAgent);
        }
    }
}
