package com.emiadda.asynctasks;

import android.os.AsyncTask;
import android.util.Log;

import com.emiadda.interafaces.ServerResponseSubscriber;
import com.emiadda.wsdl.customerLogin.WBNCustomerloginBinding;
import com.emiadda.wsdl.customerLogin.WBNExtendedSoapSerializationEnvelope;
import com.emiadda.wsdl.customerLogin.WBNparams;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

/**
 * Created by Kunal on 04/07/16.
 */
public class LoginAsync extends AsyncTask<String, Void, String> {
    private static final String TAG = LoginAsync.class.getSimpleName();
    private static final String METHOD_NAME = "CustomerLogin";
    private static final String NAMESPACE = "http://www.mydevsystems.com";

    private ServerResponseSubscriber serverResponseInterface;
    private int requestCode;
    private int responseCode;

    public LoginAsync(ServerResponseSubscriber serverResponseInterface, int requestCode) {
        this.serverResponseInterface = serverResponseInterface;
        this.requestCode = requestCode;
    }

    @Override
    protected String doInBackground(String... params) {

        try {
            //Using easysoap
            WBNCustomerloginBinding vfwCustomerloginBinding = new WBNCustomerloginBinding();

            //Using soap standard way
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            PropertyInfo emailProperty = new PropertyInfo();
            emailProperty.setName("email");
//            emailProperty.setValue("hardik.prajapati@geeconsystems.com");
            emailProperty.setValue(params[0]);
            emailProperty.setType(String.class);
            request.addProperty(emailProperty);

            PropertyInfo passwordProperty = new PropertyInfo();
            passwordProperty.setName("password");
//            passwordProperty.setValue("123456");
            passwordProperty.setValue(params[1]);
            passwordProperty.setType(String.class);
            request.addProperty(passwordProperty);

            WBNExtendedSoapSerializationEnvelope soapEnvelope = new WBNExtendedSoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.encodingStyle = SoapEnvelope.ENC;
            soapEnvelope.dotNet = false;
            soapEnvelope.bodyOut = request;

            responseCode = ServerResponseSubscriber.RESPONSE_CODE_OK;
            return vfwCustomerloginBinding.CustomerLogin(new WBNparams(request, soapEnvelope));
        }
        catch (Exception ex) {
            responseCode = ServerResponseSubscriber.RESPONSE_CODE_EXCEPTION;
            Log.e(TAG, "Error: " + ex.getMessage(), ex);
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        if(serverResponseInterface != null) {
            serverResponseInterface.responseReceived(s, requestCode, responseCode, 0, null);
        }
    }
}
