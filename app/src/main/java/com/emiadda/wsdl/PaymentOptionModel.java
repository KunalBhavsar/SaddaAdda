package com.emiadda.wsdl;

import java.io.Serializable;

/**
 * Created by Kunal on 13/07/16.
 */
public class PaymentOptionModel implements Serializable {

    private static final long serialVersionUID = 1L;

    private String edetail_id;
    private String product_id;
    private String down_payment;
    private String no_of_emi;
    private String per_emi;
    private String product_mrp;
    private String down_payment_percent;
    private String free_emi;
    private String payable_emi;
    private String payable_amt;
    private String payable_mrp;
    private String display_payment_details;

    public String getEdetail_id() {
        return edetail_id;
    }

    public void setEdetail_id(String edetail_id) {
        this.edetail_id = edetail_id;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getDown_payment() {
        return down_payment;
    }

    public void setDown_payment(String down_payment) {
        this.down_payment = down_payment;
    }

    public String getNo_of_emi() {
        return no_of_emi;
    }

    public void setNo_of_emi(String no_of_emi) {
        this.no_of_emi = no_of_emi;
    }

    public String getPer_emi() {
        return per_emi;
    }

    public void setPer_emi(String per_emi) {
        this.per_emi = per_emi;
    }

    public String getProduct_mrp() {
        return product_mrp;
    }

    public void setProduct_mrp(String product_mrp) {
        this.product_mrp = product_mrp;
    }

    public String getDown_payment_percent() {
        return down_payment_percent;
    }

    public void setDown_payment_percent(String down_payment_percent) {
        this.down_payment_percent = down_payment_percent;
    }

    public String getFree_emi() {
        return free_emi;
    }

    public void setFree_emi(String free_emi) {
        this.free_emi = free_emi;
    }

    public String getPayable_emi() {
        return payable_emi;
    }

    public void setPayable_emi(String payable_emi) {
        this.payable_emi = payable_emi;
    }

    public String getPayable_amt() {
        return payable_amt;
    }

    public void setPayable_amt(String payable_amt) {
        this.payable_amt = payable_amt;
    }

    public String getPayable_mrp() {
        return payable_mrp;
    }

    public void setPayable_mrp(String payable_mrp) {
        this.payable_mrp = payable_mrp;
    }

    public String getDisplay_payment_details() {
        return display_payment_details;
    }

    public void setDisplay_payment_details(String display_payment_details) {
        this.display_payment_details = display_payment_details;
    }
}
