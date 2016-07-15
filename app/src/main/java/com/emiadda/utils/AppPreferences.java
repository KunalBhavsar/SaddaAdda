package com.emiadda.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.emiadda.wsdl.CustomerModel;
import com.emiadda.wsdl.ProductModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Kunal on 06/07/16.
 */
public class AppPreferences {
    private static final String TAG = AppPreferences.class.getSimpleName();
    private static AppPreferences ourInstance;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;

    public static final String USER_LOGGED_IN = "user_logged_in";
    public static final String APP_OWNER_DATA = "app_owner_data";
    public static final String CART_LIST = "cart_list";

    private AppPreferences(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static void init(Context context) {
        ourInstance = new AppPreferences(context);
    }

    public static AppPreferences getInstance() {
        return ourInstance;
    }

    public void clearAppPref() {
        editor.clear().apply();
    }

    public void remove(String key) {
        editor.remove(key).apply();
    }

    public boolean isUserLoggedIn() {
        return sharedPreferences.getBoolean(USER_LOGGED_IN, false);
    }

    public void setUserLoggegIn(boolean loggedIn) {
        editor.putBoolean(USER_LOGGED_IN, loggedIn);
        editor.apply();
    }

    public CustomerModel getAppOwnerData() {
        String customerData = sharedPreferences.getString(APP_OWNER_DATA, "");
        if(customerData.isEmpty()) {
            return null;
        }
        return new Gson().fromJson(customerData, new TypeToken<CustomerModel>(){}.getType());
    }

    public void setAppOwnerData(CustomerModel customerModel) {
        editor.putString(APP_OWNER_DATA, new Gson().toJson(customerModel));
        editor.apply();
    }

    public List<ProductModel> getCartList() {
        String cartString = sharedPreferences.getString(CART_LIST, "");
        if(!cartString.isEmpty()) {
            Type listType = new TypeToken<ArrayList<ProductModel>>(){}.getType();
            return new Gson().fromJson(cartString, listType);
        }
        return new ArrayList<>();
    }

    public void addProductToCartList(ProductModel item) {
        List<ProductModel> productList = getCartList();
        if (productList.contains(item)) {
            productList.remove(productList.indexOf(item));
            productList.add(item);
        }
        else {
            productList.add(item);
        }
        editor.putString(CART_LIST, new Gson().toJson(productList));
        editor.apply();
    }

    public void removeProductFromCartList(ProductModel item) {
        List<ProductModel> productList = getCartList();
        if (productList.contains(item)) {
            productList.remove(productList.indexOf(item));
        }
        editor.putString(CART_LIST, new Gson().toJson(productList));
        editor.apply();
    }
}
