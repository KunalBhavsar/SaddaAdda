package com.emiadda.wsdl;

import java.io.Serializable;

/**
 * Created by Kunal on 07/07/16.
 */
public class ProductImageModel implements Serializable {

    private static final long serialVersionUID = 1L;

    String product_id;
    String image;

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
