package co.ppk.domain;

public class Service {
    private final String id;
    private final String serviceId;
    private final float amount;
    private final String status;
    private final String createdAt;
    private final String updatedAt;

    public Service(String id, String serviceId, float amount, String status, String createdAt, String updatedAt) {
        this.id = id;
        this.serviceId = serviceId;
        this.amount = amount;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }

    public String getServiceId() {
        return serviceId;
    }

    public float getAmount() {
        return amount;
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
        private String serviceId;
        private float amount;
        private String status;
        private String createdAt;
        private String updatedAt;

        public Service.Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Service.Builder setServiceId(String serviceId) {
            this.serviceId = serviceId;
            return this;
        }

        public Service.Builder setAmount(float amount) {
            this.amount = amount;
            return this;
        }

        public Service.Builder setStatus(String status) {
            this.status = status;
            return this;
        }

        public Service.Builder setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Service.Builder setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Service build() {
            return new Service(id, serviceId, amount, status, createdAt, updatedAt);
        }
    }
}
