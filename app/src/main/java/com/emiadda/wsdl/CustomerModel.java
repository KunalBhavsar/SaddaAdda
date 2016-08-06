package com.emiadda.wsdl;

import java.io.Serializable;

/**
 * Created by Kunal on 05/07/16.
 */
public class CustomerModel implements Serializable {

    private static final long serialVersionUID = 1L;

    String customer_id;
    String firstname;
    String lastname;
    String customer_group_id;
    String email;
    String telephone;
    String fax;
    String newsletter;
    String address_id;
    String registration_code;
    String customer_type;
    String credit_limit;
    AddressModel address;

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getNewsletter() {
        return newsletter;
    }

    public void setNewsletter(String newsletter) {
        this.newsletter = newsletter;
    }

    public AddressModel getAddress() {
        return address;
    }

    public void setAddress(AddressModel address) {
        this.address = address;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getCustomer_group_id() {
        return customer_group_id;
    }

    public void setCustomer_group_id(String customer_group_id) {
        this.customer_group_id = customer_group_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getAddress_id() {
        return address_id;
    }

    public void setAddress_id(String address_id) {
        this.address_id = address_id;
    }

    public String getRegistration_code() {
        return registration_code;
    }

    public void setRegistration_code(String registration_code) {
        this.registration_code = registration_code;
    }

    public String getCustomer_type() {
        return customer_type;
    }

    public void setCustomer_type(String customer_type) {
        this.customer_type = customer_type;
    }

    public String getCredit_limit() {
        return credit_limit;
    }

    public void setCredit_limit(String credit_limit) {
        this.credit_limit = credit_limit;
    }
}
