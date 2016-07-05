package com.emiadda.core;

/**
 * Created by Kunal on 05/07/16.
 */
public class EACategory extends EABase {
    private String categoryName;
    private String categoryImage;

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryImage() {
        return categoryImage;
    }

    public void setCategoryImage(String categoryImage) {
        this.categoryImage = categoryImage;
    }
}
