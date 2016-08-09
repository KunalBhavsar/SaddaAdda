package com.emiadda.wsdl;

import java.io.Serializable;

/**
 * Created by Kunal on 08/08/16.
 */

public class PlaceOrderResponse implements Serializable {

    private static final long serialVersionUID = 0L;

    int order_id;
    int order_payment_id;

    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public int getOrder_payment_id() {
        return order_payment_id;
    }

    public void setOrder_payment_id(int order_payment_id) {
        this.order_payment_id = order_payment_id;
    }
}
