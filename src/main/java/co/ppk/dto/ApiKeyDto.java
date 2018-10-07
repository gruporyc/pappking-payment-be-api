package co.ppk.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

/**
 * ApiKeyDto: Data transformation object for json transformation of api key object
 * @author jmunoz
 * @since 29/09/2018
 * @version 1.0.0
 */
public class ApiKeyDto {
    @NotNull
    private String clientId;
    @NotNull
    private int validity;

    /**
     * @return the client's universal identifier
     */
    @JsonProperty("client_id")
    public String getClientId() {
        return clientId;
    }

    /**
     * @param clientId the client's universal identifier
     */
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    /**
     * @return the client's api key validity
     */
    @JsonProperty("validity")
    public int getValidity() {
        return validity;
    }

    /**
     * @param validity the client's api key validity
     */
    public void setValidity(int validity) {
        this.validity = validity;
    }

    @Override
    public String toString() {
        return "ApiKeyDto{" +
                "clientId='" + clientId + '\'' +
                ", validity='" + validity + '\'' +
                '}';
    }
}
