package com.emiadda.core;

import com.emiadda.server.VectorProductsParams;
import com.emiadda.server.VectorTotalParams;
import com.emiadda.server.OrderParams;

/**
 * Created by Kunal on 06/08/16.
 */

public class EAPlaceOrderRequeset extends EAServerRequest {

    private OrderParams orderparams;
    private VectorProductsParams vectorproductsparams;
    private VectorTotalParams vectortotalparams;


    public EAPlaceOrderRequeset(int requestCode, int extraRequestCode, int priority, OrderParams orderparams, VectorProductsParams vectorproductsparams, VectorTotalParams vectortotalparams) {
        super(requestCode, extraRequestCode, priority);
        this.orderparams = orderparams;
        this.vectorproductsparams = vectorproductsparams;
        this.vectortotalparams = vectortotalparams;
    }

    public OrderParams getOrderparams() {
        return orderparams;
    }

    public void setOrderparams(OrderParams orderparams) {
        this.orderparams = orderparams;
    }

    public VectorProductsParams getVectorproductsparams() {
        return vectorproductsparams;
    }

    public void setVectorproductsparams(VectorProductsParams vectorproductsparams) {
        this.vectorproductsparams = vectorproductsparams;
    }

    public VectorTotalParams getVectortotalparams() {
        return vectortotalparams;
    }

    public void setVectortotalparams(VectorTotalParams vectortotalparams) {
        this.vectortotalparams = vectortotalparams;
    }
}
