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

import co.ppk.enums.Currency;
import co.ppk.enums.PaymentMethod;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;


/**
 * PaymentRegistryDto: Data transformation object for json transformation of payment registry object
 * @author jmunoz
 * @since 05/08/2018
 * @version 1.0.0
 */
public class LoadRegistryDto {
    @NotNull
    private float amount;
    @NotNull
    private Currency currency;
    @NotNull
    private PersonDto buyer;
    @NotNull
    private PersonDto payer;
    @NotNull
    private PaymentMethod method;
    private CreditCardDto creditCard;

    /**
     * @return the payment request amount
     */
    @JsonProperty("amount")
    public float getAmount() {
        return amount;
    }

    /**
     * @param amount the payment request amount
     */
    public void setAmount(float amount) {
        this.amount = amount;
    }

    /**
     * @return the payment request currency
     */
    @JsonProperty("currency")
    public Currency getCurrency() {
        return currency;
    }

    /**
     * @param currency the payment request currency
     */
    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    /**
     * @return the payment request buyer
     */
    @JsonProperty("buyer")
    public PersonDto getBuyer() {
        return buyer;
    }

    /**
     * @param buyer the payment request buyer
     */
    public void setBuyer(PersonDto buyer) {
        this.buyer = buyer;
    }

    /**
     * @return the payment request payer
     */
    @JsonProperty("payer")
    public PersonDto getPayer() {
        return payer;
    }

    /**
     * @param payer the payment request payer
     */
    public void setPayer(PersonDto payer) {
        this.payer = payer;
    }

    /**
     * @return the payment request method
     */
    @JsonProperty("method")
    public PaymentMethod getMethod() {
        return method;
    }

    /**
     * @param method the payment request method
     */
    public void setMethod(PaymentMethod method) {
        this.method = method;
    }

    /**
     * @return the payment request credit card
     */
    @JsonProperty("credit_card")
    public CreditCardDto getCreditCard() {
        return creditCard;
    }

    /**
     * @param creditCard the payment request credit card
     */
    public void setCreditCard(CreditCardDto creditCard) {
        this.creditCard = creditCard;
    }

    @Override
    public String toString() {
        return "PaymentRequestDto{" +
                "amount=" + amount +
                ", currency=" + currency +
                ", buyer=" + buyer +
                ", payer=" + payer +
                ", method=" + method +
                ", creditCard=" + creditCard +
                '}';
    }
}
