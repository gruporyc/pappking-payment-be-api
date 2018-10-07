package co.ppk.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

/**
 * ClientDto: Data transformation object for json transformation of Client object
 * @author jmunoz
 * @since 29/09/2018
 * @version 1.0.0
 */
public class ClientDto {
    @NotNull
    private String name;
    @NotNull
    private String gatewayAccoutId;
    @NotNull
    private String gatewayMerchantId;
    @NotNull
    private String gatewayApiKey;
    @NotNull
    private String gatewayApiLogin;

    /**
     * @return the client's commercial name
     */
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    /**
     * @param name the client's commercial name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the client's gateway account identifier
     */
    @JsonProperty("gateway_accout_id")
    public String getGatewayAccoutId() {
        return gatewayAccoutId;
    }

    /**
     * @param gatewayAccoutId the client's gateway account identifier
     */
    public void setGatewayAccoutId(String gatewayAccoutId) {
        this.gatewayAccoutId = gatewayAccoutId;
    }

    /**
     * @return the client's gateway merchant identifier
     */
    @JsonProperty("gateway_merchant_id")
    public String getGatewayMerchantId() {
        return gatewayMerchantId;
    }

    /**
     * @param gatewayMerchantId the client's gateway merchant identifier
     */
    public void setGatewayMerchantId(String gatewayMerchantId) {
        this.gatewayMerchantId = gatewayMerchantId;
    }

    /**
     * @return the client's gateway api key
     */
    @JsonProperty("gateway_api_key")
    public String getGatewayApiKey() {
        return gatewayApiKey;
    }

    /**
     * @param gatewayApiKey the client's gateway api key
     */
    public void setGatewayApiKey(String gatewayApiKey) {
        this.gatewayApiKey = gatewayApiKey;
    }

    /**
     * @return the client's gateway api login
     */
    @JsonProperty("gateway_api_login")
    public String getGatewayApiLogin() {
        return gatewayApiLogin;
    }

    /**
     * @param gatewayApiLogin the client's gateway api login
     */
    public void setGatewayApiLogin(String gatewayApiLogin) {
        this.gatewayApiLogin = gatewayApiLogin;
    }

    @Override
    public String toString() {
        return "ClientDto{" +
                "name='" + name + '\'' +
                ", gatewayAccoutId='" + gatewayAccoutId + '\'' +
                ", gatewayMerchantId='" + gatewayMerchantId + '\'' +
                ", gatewayApiKey='" + gatewayApiKey + '\'' +
                ", gatewayApiLogin='" + gatewayApiLogin + '\'' +
                '}';
    }
}
