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
import co.ppk.enums.DocumentType;
import co.ppk.enums.PaymentMethod;
import co.ppk.enums.PersonType;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;


/**
 * PaymentRequestDto: Data transformation object for json transformation of payment request object
 * @author jmunoz
 * @since 05/08/2018
 * @version 1.0.0
 */
public class LoadRequestDto {
    @NotNull
    private double amount;
    @NotNull
    private Currency currency;
    @NotNull
    private PersonDto buyer;
    @NotNull
    private String description;
    @NotNull
    private PaymentMethod method;
    private CreditCardDto creditCard;
    private String financialInstituteCode;
    private PersonType personType;
    private String dni;
    private DocumentType documentType;

    /**
     * @return the payment request amount
     */
    @JsonProperty("amount")
    public double getAmount() {
        return amount;
    }

    /**
     * @param amount the payment request amount
     */
    public void setAmount(double amount) {
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
     * @return the payment request description
     */
    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    /**
     * @param description the payment request description
     */
    public void setDescription(String description) {
        this.description = description;
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

    /**
     * @return the payment request financial institute code
     */
    @JsonProperty("financial_institute_code")
    public String getFinancialInstituteCode() {
        return financialInstituteCode;
    }

    /**
     * @param financialInstituteCode the payment request financial institute code
     */
    public void setFinancialInstituteCode(String financialInstituteCode) {
        this.financialInstituteCode = financialInstituteCode;
    }

    /**
     * @return the payment request person type
     */
    @JsonProperty("person_type")
    public PersonType getPersonType() {
        return personType;
    }

    /**
     * @param personType the payment request person type
     */
    public void setPersonType(PersonType personType) {
        this.personType = personType;
    }

    /**
     * @return the payment request dni
     */
    @JsonProperty("dni")
    public String getDni() {
        return dni;
    }

    /**
     * @param dni the payment request dni
     */
    public void setDni(String dni) {
        this.dni = dni;
    }

    /**
     * @return the payment request document type
     */
    @JsonProperty("document_type")
    public DocumentType getDocumentType() {
        return documentType;
    }

    /**
     * @param documentType the payment request document type
     */
    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }

    @Override
    public String toString() {
        return "LoadRequestDto{" +
                "amount=" + amount +
                ", currency=" + currency +
                ", buyer=" + buyer +
                ", method=" + method +
                ", description=" + description +
                ", creditCard=" + creditCard +
                ", financialInstituteCode='" + financialInstituteCode + '\'' +
                ", personType=" + personType +
                ", dni='" + dni + '\'' +
                ", documentType=" + documentType +
                '}';
    }
}
