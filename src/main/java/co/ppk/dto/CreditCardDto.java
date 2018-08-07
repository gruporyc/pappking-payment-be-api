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

import co.ppk.enums.Country;
import co.ppk.enums.CreditCardType;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

/**
 * CreditCardDto: Data transformation object for json transformation of Credit card object
 * @author jmunoz
 * @since 05/08/2018
 * @version 1.0.0
 */

public class CreditCardDto {
    @NotNull
    private String number;
    @NotNull
    private String expiration;
    @NotNull
    private int securityCode;
    @NotNull
    private CreditCardType type;
    @NotNull
    private int installments;
    @NotNull
    private Country country;

    /**
     * @return the credit card number
     */
    @JsonProperty("number")
    public String getNumber() {
        return number;
    }

    /**
     * @param number the credit card number
     */
    public void setNumber(String number) {
        this.number = number;
    }

    /**
     * @return the credit card expiration date
     */
    @JsonProperty("expiration")
    public String getExpiration() {
        return expiration;
    }

    /**
     * @param expiration the credit card expiration date
     */
    public void setExpiration(String expiration) {
        this.expiration = expiration;
    }

    /**
     * @return the credit card security code
     */
    @JsonProperty("security_code")
    public int getSecurityCode() {
        return securityCode;
    }

    /**
     * @param securityCode the credit card security code
     */
    public void setSecurityCode(int securityCode) {
        this.securityCode = securityCode;
    }

    /**
     * @return the credit card type (visa, mastercard, amex, ... )
     */
    @JsonProperty("type")
    public CreditCardType getType() {
        return type;
    }

    /**
     * @param type the credit card method (visa, mastercard, amex, ... )
     */
    public void setType(CreditCardType type) {
        this.type = type;
    }

    /**
     * @return the credit card installments
     */
    @JsonProperty("installments")
    public int getInstallments() {
        return installments;
    }

    /**
     * @param installments the credit card installments
     */
    public void setInstallments(int installments) {
        this.installments = installments;
    }

    /**
     * @return the credit card country
     */
    @JsonProperty("country")
    public Country getCountry() {
        return country;
    }

    /**
     * @param country the credit card country
     */
    public void setCountry(Country country) {
        this.country = country;
    }

    @Override
    public String toString() {
        return "CreditCardDto{" +
                "number='" + number + '\'' +
                ", expiration='" + expiration + '\'' +
                ", securityCode=" + securityCode +
                ", method=" + type +
                ", installments=" + installments +
                ", country=" + country +
                '}';
    }
}
