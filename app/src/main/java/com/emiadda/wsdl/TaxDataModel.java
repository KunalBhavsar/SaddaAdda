package com.emiadda.wsdl;

import java.io.Serializable;

/**
 * Created by Kunal on 05/08/16.
 */

public class TaxDataModel implements Serializable {

    private static final long serialVersionUID = 1L;

    double tax_amt;

    public double getTax_amt() {
        return tax_amt;
    }

    public void setTax_amt(double tax_amt) {
        this.tax_amt = tax_amt;
    }
}
