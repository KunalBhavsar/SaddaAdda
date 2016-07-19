package com.emiadda.wsdl.specialProducts;

import java.io.Serializable;

/**
 * Created by Kunal on 19/07/16.
 */
public class ProductExtraOptionValueModel implements Serializable {
    private static final long serialVersionUID = 1L;

    private String product_option_value_id;
    private String option_value_id;
    private String name;
    private String image;
    private String quantity;
    private String subtract;
    private String price;
    private String price_prefix;
    private String weight;
    private String weight_prefix;

    public String getProduct_option_value_id() {
        return product_option_value_id;
    }

    public void setProduct_option_value_id(String product_option_value_id) {
        this.product_option_value_id = product_option_value_id;
    }

    public String getOption_value_id() {
        return option_value_id;
    }

    public void setOption_value_id(String option_value_id) {
        this.option_value_id = option_value_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getSubtract() {
        return subtract;
    }

    public void setSubtract(String subtract) {
        this.subtract = subtract;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPrice_prefix() {
        return price_prefix;
    }

    public void setPrice_prefix(String price_prefix) {
        this.price_prefix = price_prefix;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getWeight_prefix() {
        return weight_prefix;
    }

    public void setWeight_prefix(String weight_prefix) {
        this.weight_prefix = weight_prefix;
    }
}
