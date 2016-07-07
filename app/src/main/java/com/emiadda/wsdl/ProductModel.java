package com.emiadda.wsdl;

import java.io.Serializable;

/**
 * Created by Shraddha on 6/7/16.
 */
public class ProductModel implements Serializable {

    private static final long serialVersionUID = 1L;

    private String product_id;
    private String name;
    private String description;
    private String meta_title;
    private String meta_description;
    private String meta_keyword;
    private String tag;
    private String model;
    private String sku;
    private String upc;
    private String ean;
    private String jan;
    private String isbn;
    private String mpn;
    private String location;
    private String quantity;
    private String stock_status;
    private String image;
    private String manufacturer_id;
    private String manufacturer;
    private String price;
    private String special;
    private String reward;
    private String points;
    private String tax_class_id;
    private String date_available;
    private String weight;
    private String weight_class_id;
    private String length;
    private String width;
    private String height;
    private String length_class_id;
    private String subtract;
    private String rating;
    private String reviews;
    private String minimum;
    private String sort_order;
    private String status;
    private String date_added;
    private String date_modified;
    private String viewed;
    private String down_payment;
    private String emi_last_price;
    private String otp_last_price;
    private String otp_discount_rate;
    private String emi_discount_rate;
    private String show_payment_option;

    private boolean loadingImage;

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMeta_title() {
        return meta_title;
    }

    public void setMeta_title(String meta_title) {
        this.meta_title = meta_title;
    }

    public String getMeta_description() {
        return meta_description;
    }

    public void setMeta_description(String meta_description) {
        this.meta_description = meta_description;
    }

    public String getMeta_keyword() {
        return meta_keyword;
    }

    public void setMeta_keyword(String meta_keyword) {
        this.meta_keyword = meta_keyword;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getUpc() {
        return upc;
    }

    public void setUpc(String upc) {
        this.upc = upc;
    }

    public String getEan() {
        return ean;
    }

    public void setEan(String ean) {
        this.ean = ean;
    }

    public String getJan() {
        return jan;
    }

    public void setJan(String jan) {
        this.jan = jan;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getMpn() {
        return mpn;
    }

    public void setMpn(String mpn) {
        this.mpn = mpn;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getStock_status() {
        return stock_status;
    }

    public void setStock_status(String stock_status) {
        this.stock_status = stock_status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getManufacturer_id() {
        return manufacturer_id;
    }

    public void setManufacturer_id(String manufacturer_id) {
        this.manufacturer_id = manufacturer_id;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSpecial() {
        return special;
    }

    public void setSpecial(String special) {
        this.special = special;
    }

    public String getReward() {
        return reward;
    }

    public void setReward(String reward) {
        this.reward = reward;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getTax_class_id() {
        return tax_class_id;
    }

    public void setTax_class_id(String tax_class_id) {
        this.tax_class_id = tax_class_id;
    }

    public String getDate_available() {
        return date_available;
    }

    public void setDate_available(String date_available) {
        this.date_available = date_available;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getWeight_class_id() {
        return weight_class_id;
    }

    public void setWeight_class_id(String weight_class_id) {
        this.weight_class_id = weight_class_id;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getLength_class_id() {
        return length_class_id;
    }

    public void setLength_class_id(String length_class_id) {
        this.length_class_id = length_class_id;
    }

    public String getSubtract() {
        return subtract;
    }

    public void setSubtract(String subtract) {
        this.subtract = subtract;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getReviews() {
        return reviews;
    }

    public void setReviews(String reviews) {
        this.reviews = reviews;
    }

    public String getMinimum() {
        return minimum;
    }

    public void setMinimum(String minimum) {
        this.minimum = minimum;
    }

    public String getSort_order() {
        return sort_order;
    }

    public void setSort_order(String sort_order) {
        this.sort_order = sort_order;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate_added() {
        return date_added;
    }

    public void setDate_added(String date_added) {
        this.date_added = date_added;
    }

    public String getDate_modified() {
        return date_modified;
    }

    public void setDate_modified(String date_modified) {
        this.date_modified = date_modified;
    }

    public String getViewed() {
        return viewed;
    }

    public void setViewed(String viewed) {
        this.viewed = viewed;
    }

    public String getDown_payment() {
        return down_payment;
    }

    public void setDown_payment(String down_payment) {
        this.down_payment = down_payment;
    }

    public String getEmi_last_price() {
        return emi_last_price;
    }

    public void setEmi_last_price(String emi_last_price) {
        this.emi_last_price = emi_last_price;
    }

    public String getOtp_last_price() {
        return otp_last_price;
    }

    public void setOtp_last_price(String otp_last_price) {
        this.otp_last_price = otp_last_price;
    }

    public String getOtp_discount_rate() {
        return otp_discount_rate;
    }

    public void setOtp_discount_rate(String otp_discount_rate) {
        this.otp_discount_rate = otp_discount_rate;
    }

    public String getEmi_discount_rate() {
        return emi_discount_rate;
    }

    public void setEmi_discount_rate(String emi_discount_rate) {
        this.emi_discount_rate = emi_discount_rate;
    }

    public String getShow_payment_option() {
        return show_payment_option;
    }

    public void setShow_payment_option(String show_payment_option) {
        this.show_payment_option = show_payment_option;
    }

    public boolean isLoadingImage() {
        return loadingImage;
    }

    public void setLoadingImage(boolean loadingImage) {
        this.loadingImage = loadingImage;
    }
}
