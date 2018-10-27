package co.ppk.domain;

public class ApiKey {
    private final String id;
    private final String token;
    private final String clientId;
    private final String expirationDate;
    private final String status;
    private final String createdAt;
    private final String updatedAt;

    public ApiKey(String id, String token, String clientId, String expirationDate, String status, String createdAt, String updatedAt) {
        this.id = id;
        this.token = token;
        this.clientId = clientId;
        this.expirationDate = expirationDate;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public String getClientId() {
        return clientId;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public String getStatus() {
        return status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public static class Builder {
        private String id;
        private String token;
        private String clientId;
        private String expirationDate;
        private String status;
        private String createdAt;
        private String updatedAt;

        public ApiKey.Builder setId(String id) {
            this.id = id;
            return this;
        }

        public ApiKey.Builder setToken(String token) {
            this.token = token;
            return this;
        }

        public ApiKey.Builder setClientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public ApiKey.Builder setExpirationDate(String expirationDate) {
            this.expirationDate = expirationDate;
            return this;
        }

        public ApiKey.Builder setStatus(String status) {
            this.status = status;
            return this;
        }

        public ApiKey.Builder setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public ApiKey.Builder setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public ApiKey build() {
            return new ApiKey(id, token, clientId, expirationDate, status, createdAt, updatedAt);
        }
    }
}
