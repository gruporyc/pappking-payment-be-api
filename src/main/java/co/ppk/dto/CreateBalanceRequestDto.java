package co.ppk.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateBalanceRequestDto {
    private String customerId;
    private double Balance;
    private String Status;

    @JsonProperty("customer_id")
    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    @JsonProperty("balance")
    public double getBalance() {
        return Balance;
    }

    public void setBalance(double balance) {
        Balance = balance;
    }

    @JsonProperty("status")
    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    @Override
    public String toString() {
        return "CreateBalanceRequestDto{" +
                "customerId='" + customerId + '\'' +
                ", Balance='" + Balance + '\'' +
                ", Status='" + Status + '\'' +
                '}';
    }
}
