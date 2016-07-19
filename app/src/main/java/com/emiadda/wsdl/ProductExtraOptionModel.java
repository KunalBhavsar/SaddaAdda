package com.emiadda.wsdl;

import com.emiadda.wsdl.specialProducts.ProductExtraOptionValueModel;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Kunal on 19/07/16.
 */
public class ProductExtraOptionModel implements Serializable {

    private static final long serialVersionUID = 1L;

    private String product_option_id;
    private String option_id;
    private String name;
    private String type;
    private String required;
    private String value;
    private List<ProductExtraOptionValueModel> product_option_value;

    public String getProduct_option_id() {
        return product_option_id;
    }

    public void setProduct_option_id(String product_option_id) {
        this.product_option_id = product_option_id;
    }

    public String getOption_id() {
        return option_id;
    }

    public void setOption_id(String option_id) {
        this.option_id = option_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRequired() {
        return required;
    }

    public void setRequired(String required) {
        this.required = required;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<ProductExtraOptionValueModel> getProduct_option_value() {
        return product_option_value;
    }

    public void setProduct_option_value(List<ProductExtraOptionValueModel> product_option_value) {
        this.product_option_value = product_option_value;
    }
}
