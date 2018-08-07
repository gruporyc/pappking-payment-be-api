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
 * PaymentResponseDto: Data transformation object for json transformation of payment response object
 * @author jmunoz
 * @since 05/08/2018
 * @version 1.0.0
 */
public class LoadResponseDto {
    private String orderId;
    private String transactionId;
    private String state;
    private String pendingReason;
    private String responseCode;
    private String responsePaymentCode;
    private String errorMessage;
    private String trazabilityCode;
    private String responseMessage;

    /**
     * @return the payment response order id
     */
    @JsonProperty("order_id")
    public String getOrderId() {
        return orderId;
    }

    /**
     * @param orderId the payment response order id
     */
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    /**
     * @return the payment response transaction id
     */
    @JsonProperty("transaction_id")
    public String getTransactionId() {
        return transactionId;
    }

    /**
     * @param transactionId the payment response transaction id
     */
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    /**
     * @return the payment response state
     */
    @JsonProperty("state")
    public String getState() {
        return state;
    }

    /**
     * @param state the payment response state
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * @return the payment response pending reason
     */
    @JsonProperty("pending_reason")
    public String getPendingReason() {
        return pendingReason;
    }

    /**
     * @param pendingReason the payment response pending reason
     */
    public void setPendingReason(String pendingReason) {
        this.pendingReason = pendingReason;
    }

    /**
     * @return the payment response response code
     */
    @JsonProperty("response_code")
    public String getResponseCode() {
        return responseCode;
    }

    /**
     * @param responseCode the payment response code
     */
    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    /**
     * @return the payment response payment code
     */
    @JsonProperty("response_payment_code")
    public String getResponsePaymentCode() {
        return responsePaymentCode;
    }

    /**
     * @param responsePaymentCode the payment response payment code
     */
    public void setResponsePaymentCode(String responsePaymentCode) {
        this.responsePaymentCode = responsePaymentCode;
    }

    /**
     * @return the payment response error message
     */
    @JsonProperty("error_message")
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * @param errorMessage the payment response error message
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * @return the payment response trazability code
     */
    @JsonProperty("trazability_code")
    public String getTrazabilityCode() {
        return trazabilityCode;
    }

    /**
     * @param trazabilityCode the payment response trazability code
     */
    public void setTrazabilityCode(String trazabilityCode) {
        this.trazabilityCode = trazabilityCode;
    }

    /**
     * @return the payment response response message
     */
    @JsonProperty("response_message")
    public String getResponseMessage() {
        return responseMessage;
    }

    /**
     * @param responseMessage the payment response message
     */
    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    @Override
    public String toString() {
        return "PaymentResponseDto{" +
                "orderId='" + orderId + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", state='" + state + '\'' +
                ", pendingReason='" + pendingReason + '\'' +
                ", responseCode='" + responseCode + '\'' +
                ", responsePaymentCode='" + responsePaymentCode + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", trazabilityCode='" + trazabilityCode + '\'' +
                ", responseMessage='" + responseMessage + '\'' +
                '}';
    }
}
