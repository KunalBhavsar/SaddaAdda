package com.emiadda.server;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import java.util.Hashtable;

/**
 * Created by Kunal on 13/08/16.
 */
public class UpdateCustomerParams implements KvmSerializable {

    public String customer_id;
    public String firstname;
    public String lastname;
    public String email;
    public String telephone;
    public String gender;
    public String input_gender;
    public String dob;
    public String franchise;
    public String volunteer;
    public String volunteer_code;
    public String vol;
    public String customer_group_id;
    public String address_1;
    public String address_2;
    public String zone_do_id;
    public String payment_address_2;
    public String city_id;
    public String postcode;
    public String password;
    public String confirm;
    public String newsletter;
    public String service;
    public String agree;

    public UpdateCustomerParams() {
    }

    public UpdateCustomerParams(SoapObject soapObject) {
        if (soapObject == null)
            return;
        if (soapObject.hasProperty("customer_id")) {
            Object obj = soapObject.getProperty("customer_id");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class)) {
                SoapPrimitive j = (SoapPrimitive) obj;
                customer_id = j.toString();
            } else if (obj != null && obj instanceof String) {
                customer_id = (String) obj;
            }
        }
        if (soapObject.hasProperty("firstname")) {
            Object obj = soapObject.getProperty("firstname");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class)) {
                SoapPrimitive j = (SoapPrimitive) obj;
                firstname = j.toString();
            } else if (obj != null && obj instanceof String) {
                firstname = (String) obj;
            }
        }
        if (soapObject.hasProperty("lastname")) {
            Object obj = soapObject.getProperty("lastname");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class)) {
                SoapPrimitive j = (SoapPrimitive) obj;
                lastname = j.toString();
            } else if (obj != null && obj instanceof String) {
                lastname = (String) obj;
            }
        }
        if (soapObject.hasProperty("email")) {
            Object obj = soapObject.getProperty("email");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class)) {
                SoapPrimitive j = (SoapPrimitive) obj;
                email = j.toString();
            } else if (obj != null && obj instanceof String) {
                email = (String) obj;
            }
        }
        if (soapObject.hasProperty("gender")) {
            Object obj = soapObject.getProperty("gender");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class)) {
                SoapPrimitive j = (SoapPrimitive) obj;
                gender = j.toString();
            } else if (obj != null && obj instanceof String) {
                gender = (String) obj;
            }
        }
        if (soapObject.hasProperty("input_gender")) {
            Object obj = soapObject.getProperty("input_gender");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class)) {
                SoapPrimitive j = (SoapPrimitive) obj;
                input_gender = j.toString();
            } else if (obj != null && obj instanceof String) {
                input_gender = (String) obj;
            }
        }
        if (soapObject.hasProperty("dob")) {
            Object obj = soapObject.getProperty("dob");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class)) {
                SoapPrimitive j = (SoapPrimitive) obj;
                dob = j.toString();
            } else if (obj != null && obj instanceof String) {
                dob = (String) obj;
            }
        }
        if (soapObject.hasProperty("franchise")) {
            Object obj = soapObject.getProperty("franchise");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class)) {
                SoapPrimitive j = (SoapPrimitive) obj;
                franchise = j.toString();
            } else if (obj != null && obj instanceof String) {
                franchise = (String) obj;
            }
        }
        if (soapObject.hasProperty("volunteer")) {
            Object obj = soapObject.getProperty("volunteer");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class)) {
                SoapPrimitive j = (SoapPrimitive) obj;
                volunteer = j.toString();
            } else if (obj != null && obj instanceof String) {
                volunteer = (String) obj;
            }
        }
        if (soapObject.hasProperty("volunteer_code")) {
            Object obj = soapObject.getProperty("volunteer_code");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class)) {
                SoapPrimitive j = (SoapPrimitive) obj;
                volunteer_code = j.toString();
            } else if (obj != null && obj instanceof String) {
                volunteer_code = (String) obj;
            }
        }
        if (soapObject.hasProperty("vol")) {
            Object obj = soapObject.getProperty("vol");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class)) {
                SoapPrimitive j = (SoapPrimitive) obj;
                vol = j.toString();
            } else if (obj != null && obj instanceof String) {
                vol = (String) obj;
            }
        }
        if (soapObject.hasProperty("customer_group_id")) {
            Object obj = soapObject.getProperty("customer_group_id");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class)) {
                SoapPrimitive j = (SoapPrimitive) obj;
                customer_group_id = j.toString();
            } else if (obj != null && obj instanceof String) {
                customer_group_id = (String) obj;
            }
        }
        if (soapObject.hasProperty("address_1")) {
            Object obj = soapObject.getProperty("address_1");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class)) {
                SoapPrimitive j = (SoapPrimitive) obj;
                address_1 = j.toString();
            } else if (obj != null && obj instanceof String) {
                address_1 = (String) obj;
            }
        }
        if (soapObject.hasProperty("address_2")) {
            Object obj = soapObject.getProperty("address_2");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class)) {
                SoapPrimitive j = (SoapPrimitive) obj;
                address_2 = j.toString();
            } else if (obj != null && obj instanceof String) {
                address_2 = (String) obj;
            }
        }
        if (soapObject.hasProperty("zone_do_id")) {
            Object obj = soapObject.getProperty("zone_do_id");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class)) {
                SoapPrimitive j = (SoapPrimitive) obj;
                zone_do_id = j.toString();
            } else if (obj != null && obj instanceof String) {
                zone_do_id = (String) obj;
            }
        }
        if (soapObject.hasProperty("payment_address_2")) {
            Object obj = soapObject.getProperty("payment_address_2");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class)) {
                SoapPrimitive j = (SoapPrimitive) obj;
                payment_address_2 = j.toString();
            } else if (obj != null && obj instanceof String) {
                payment_address_2 = (String) obj;
            }
        }
        if (soapObject.hasProperty("city_id")) {
            Object obj = soapObject.getProperty("city_id");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class)) {
                SoapPrimitive j = (SoapPrimitive) obj;
                city_id = j.toString();
            } else if (obj != null && obj instanceof String) {
                city_id = (String) obj;
            }
        }
        if (soapObject.hasProperty("postcode")) {
            Object obj = soapObject.getProperty("postcode");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class)) {
                SoapPrimitive j = (SoapPrimitive) obj;
                postcode = j.toString();
            } else if (obj != null && obj instanceof String) {
                postcode = (String) obj;
            }
        }
        if (soapObject.hasProperty("password")) {
            Object obj = soapObject.getProperty("password");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class)) {
                SoapPrimitive j = (SoapPrimitive) obj;
                password = j.toString();
            } else if (obj != null && obj instanceof String) {
                password = (String) obj;
            }
        }
        if (soapObject.hasProperty("confirm")) {
            Object obj = soapObject.getProperty("confirm");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class)) {
                SoapPrimitive j = (SoapPrimitive) obj;
                confirm = j.toString();
            } else if (obj != null && obj instanceof String) {
                confirm = (String) obj;
            }
        }
        if (soapObject.hasProperty("newsletter")) {
            Object obj = soapObject.getProperty("newsletter");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class)) {
                SoapPrimitive j = (SoapPrimitive) obj;
                newsletter = j.toString();
            } else if (obj != null && obj instanceof String) {
                newsletter = (String) obj;
            }
        }
        if (soapObject.hasProperty("service")) {
            Object obj = soapObject.getProperty("service");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class)) {
                SoapPrimitive j = (SoapPrimitive) obj;
                service = j.toString();
            } else if (obj != null && obj instanceof String) {
                service = (String) obj;
            }
        }
        if (soapObject.hasProperty("agree")) {
            Object obj = soapObject.getProperty("agree");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class)) {
                SoapPrimitive j = (SoapPrimitive) obj;
                agree = j.toString();
            } else if (obj != null && obj instanceof String) {
                agree = (String) obj;
            }
        }
    }

    @Override
    public Object getProperty(int arg0) {
        switch (arg0) {
            case 0:
                return customer_id;
            case 1:
                return firstname;
            case 2:
                return lastname;
            case 3:
                return email;
            case 4:
                return telephone;
            case 5:
                return gender;
            case 6:
                return input_gender;
            case 7:
                return dob;
            case 8:
                return franchise;
            case 9:
                return volunteer;
            case 10:
                return volunteer_code;
            case 11:
                return vol;
            case 12:
                return customer_group_id;
            case 13:
                return address_1;
            case 14:
                return address_2;
            case 15:
                return zone_do_id;
            case 16:
                return payment_address_2;
            case 17:
                return city_id;
            case 18:
                return postcode;
            case 19:
                return password;
            case 20:
                return confirm;
            case 21:
                return newsletter;
            case 22:
                return service;
            case 23:
                return agree;
        }
        return null;
    }

    @Override
    public int getPropertyCount() {
        return 24;
    }

    @Override
    public void getPropertyInfo(int index, @SuppressWarnings("rawtypes") Hashtable arg1, PropertyInfo info) {
        switch (index) {
            case 0:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "customer_id";
                break;
            case 1:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "firstname";
                break;
            case 2:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "lastname";
                break;
            case 3:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "email";
                break;
            case 4:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "telephone";
                break;
            case 5:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "gender";
                break;
            case 6:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "input_gender";
                break;
            case 7:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "dob";
                break;
            case 8:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "franchise";

                break;
            case 9:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "volunteer";
                break;
            case 10:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "volunteer_code";
                break;
            case 11:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "vol";
                break;
            case 12:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "customer_group_id";
                break;
            case 13:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "address_1";
                break;
            case 14:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "address_2";
                break;
            case 15:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "zone_do_id";
                break;
            case 16:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "payment_address_2";
                break;
            case 17:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "city_id";
                break;
            case 18:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "postcode";
                break;
            case 19:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "password";
                break;
            case 20:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "confirm";
                break;
            case 21:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "newsletter";
                break;
            case 22:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "service";
                break;
            case 23:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "agree";
                break;
        }
    }

    @Override
    public void setProperty(int arg0, Object arg1) {
    }
}
