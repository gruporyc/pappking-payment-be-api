package co.ppk.domain;

public class Balance {
    private final String id;
    private final String customerId;
    private final float balance;
    private final String status;
    private final String createdAt;
    private final String updatedAt;

    public Balance(String id, String customerId, float balance, String status, String createdAt, String updatedAt) {
        this.id = id;
        this.customerId = customerId;
        this.balance = balance;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public float getBalance() {
        return balance;
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
        private String customerId;
        private float balance;
        private String status;
        private String createdAt;
        private String updatedAt;

        public Balance.Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Balance.Builder setCustomerId(String customerId) {
            this.customerId = customerId;
            return this;
        }

        public Balance.Builder setBalance(float balance) {
            this.balance = balance;
            return this;
        }

        public Balance.Builder setStatus(String status) {
            this.status = status;
            return this;
        }

        public Balance.Builder setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Balance.Builder setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Balance build() {
            return new Balance(id, customerId, balance, status, createdAt, updatedAt);
        }
    }

}
