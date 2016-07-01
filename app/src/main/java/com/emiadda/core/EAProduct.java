package com.emiadda.core;

import java.util.List;

/**
 * Created by Kunal Bhavsar on 16/3/16.
 */
public class EAProduct extends EABase {
    private List<String> imageUrls;
    private String productName;
    private List<String> features;
    private double mrp;
    private double minEmiValue;
    private String description;

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public List<String> getFeatures() {
        return features;
    }

    public void setFeatures(List<String> features) {
        this.features = features;
    }

    public double getMrp() {
        return mrp;
    }

    public void setMrp(double mrp) {
        this.mrp = mrp;
    }

    public double getMinEmiValue() {
        return minEmiValue;
    }

    public void setMinEmiValue(double minEmiValue) {
        this.minEmiValue = minEmiValue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
