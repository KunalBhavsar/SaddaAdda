package com.emiadda.wsdl;

import java.io.Serializable;

/**
 * Created by Kunal on 14/08/16.
 */
public class RegionModel implements Serializable {
    private static final long serialVersionUID = 0L;

    private String region_id;
    private String region_name;
    private String region_level;
    private String region_parent;
    private String city_category;
    private String iso_code_2;
    private String iso_code_3;
    private String address_format;
    private String postcode_required;
    private String status;

    public String getRegion_id() {
        return region_id;
    }

    public void setRegion_id(String region_id) {
        this.region_id = region_id;
    }

    public String getRegion_name() {
        return region_name;
    }

    public void setRegion_name(String region_name) {
        this.region_name = region_name;
    }

    public String getRegion_level() {
        return region_level;
    }

    public void setRegion_level(String region_level) {
        this.region_level = region_level;
    }

    public String getRegion_parent() {
        return region_parent;
    }

    public void setRegion_parent(String region_parent) {
        this.region_parent = region_parent;
    }

    public String getCity_category() {
        return city_category;
    }

    public void setCity_category(String city_category) {
        this.city_category = city_category;
    }

    public String getIso_code_2() {
        return iso_code_2;
    }

    public void setIso_code_2(String iso_code_2) {
        this.iso_code_2 = iso_code_2;
    }

    public String getIso_code_3() {
        return iso_code_3;
    }

    public void setIso_code_3(String iso_code_3) {
        this.iso_code_3 = iso_code_3;
    }

    public String getAddress_format() {
        return address_format;
    }

    public void setAddress_format(String address_format) {
        this.address_format = address_format;
    }

    public String getPostcode_required() {
        return postcode_required;
    }

    public void setPostcode_required(String postcode_required) {
        this.postcode_required = postcode_required;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
