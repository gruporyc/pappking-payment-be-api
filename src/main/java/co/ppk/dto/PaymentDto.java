/******************************************************************
 *
 * This code is for the Pappking service project.
 *
 *
 * © 2018, Pappking Management All rights reserved.
 *
 *
 ******************************************************************/

package co.ppk.dto;


import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * PaymentDto: Data transformation object for json transformation of payment object
 * @author jmunoz
 * @since 10/08/2018
 * @version 1.0.0
 */
public class PaymentDto {
    private String customerId;
    private String serviceId;
    private float amount;

    /**
     * @return the payment customer id
     */
    @JsonProperty("customer_id")
    public String getCustomerId() {
        return customerId;
    }

    /**
     * @param customerId the payment customer id
     */
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    /**
     * @return the service id
     */
    @JsonProperty("service_id")
    public String getServiceId() {
        return serviceId;
    }

    /**
     * @param serviceId the service id
     */
    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    /**
     * @return the payment amount
     */
    @JsonProperty("amount")
    public float getAmount() {
        return amount;
    }

    /**
     * @param amount the payment amount
     */
    public void setAmount(float amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "PaymentDto{" +
                "customerId='" + customerId + '\'' +
                ", serviceId='" + serviceId + '\'' +
                ", amount=" + amount +
                '}';
    }
}
