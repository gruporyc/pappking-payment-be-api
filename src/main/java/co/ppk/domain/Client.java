package co.ppk.domain;

public class Client {
    private final String id;
    private final String name;
    private final String gatewayAccountId;
    private final String gatewayMerchantId;
    private final String gatewayApiKey;
    private final String gatewayApiLogin;
    private final String status;
    private final String createdAt;
    private final String updatedAt;

    public Client(
            String id,
            String name,
            String gatewayAccountId,
            String gatewayMerchantId,
            String gatewayApiKey,
            String gatewayApiLogin,
            String status,
            String createdAt,
            String updatedAt) {
        this.id = id;
        this.name = name;
        this.gatewayAccountId = gatewayAccountId;
        this.gatewayMerchantId = gatewayMerchantId;
        this.gatewayApiKey = gatewayApiKey;
        this.gatewayApiLogin = gatewayApiLogin;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getGatewayAccountId() {
        return gatewayAccountId;
    }

    public String getGatewayMerchantId() {
        return gatewayMerchantId;
    }

    public String getGatewayApiKey() {
        return gatewayApiKey;
    }

    public String getGatewayApiLogin() {
        return gatewayApiLogin;
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
        private String name;
        private String gatewayAccountId;
        private String gatewayMerchantId;
        private String gatewayApiKey;
        private String gatewayApiLogin;
        private String status;
        private String createdAt;
        private String updatedAt;

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setGatewayAccountId(String gatewayAccountId) {
            this.gatewayAccountId = gatewayAccountId;
            return this;
        }

        public Builder setGatewayMerchantId(String gatewayMerchantId) {
            this.gatewayMerchantId = gatewayMerchantId;
            return this;
        }

        public Builder setGatewayApiKey(String gatewayApiKey) {
            this.gatewayApiKey = gatewayApiKey;
            return this;
        }

        public Builder setGatewayApiLogin(String gatewayApiLogin) {
            this.gatewayApiLogin = gatewayApiLogin;
            return this;
        }

        public Builder setStatus(String status) {
            this.status = status;
            return this;
        }

        public Builder setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Client build() {
            return new Client(id, name, gatewayAccountId, gatewayMerchantId, gatewayApiKey, gatewayApiLogin, status, createdAt, updatedAt);
        }
    }
}

