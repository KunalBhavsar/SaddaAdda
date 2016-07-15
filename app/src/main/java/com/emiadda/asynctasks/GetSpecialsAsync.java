package com.emiadda.asynctasks;

import android.os.AsyncTask;
import android.util.Log;

import com.emiadda.interafaces.ServerResponseInterface;
import com.emiadda.wsdl.categoriesAndProducts.VOKExtendedSoapSerializationEnvelope;
import com.emiadda.wsdl.categoriesAndProducts.VOKserverBinding;
import com.emiadda.wsdl.specialProducts.KESExtendedSoapSerializationEnvelope;
import com.emiadda.wsdl.specialProducts.KESserverBinding;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

/**
 * Created by Kunal on 16/07/16.
 */
public class GetSpecialsAsync extends AsyncTask<Void, Void, String> {

    private static final String TAG = GetProductsByCategory.class.getSimpleName();
    private static final String METHOD_NAME = "getProductsByCategory";
    private static final String NAMESPACE = "http://www.mydevsystems.com";

    private int requestCode;
    private int responseCode;
    private ServerResponseInterface serverResponseInterface;

    public GetSpecialsAsync(ServerResponseInterface serverResponseInterface, int requestCode) {
        this.requestCode =requestCode;
        this.serverResponseInterface = serverResponseInterface;
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            //Using easysoap
            KESserverBinding abmServerBinding = new KESserverBinding();

            //Using soap standard way
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            PropertyInfo propertyId = new PropertyInfo();
            propertyId.setValue(0);
            propertyId.setType(Integer.class);
            request.addProperty(propertyId);

            KESExtendedSoapSerializationEnvelope soapEnvelope = new KESExtendedSoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.encodingStyle = SoapEnvelope.ENC;
            soapEnvelope.dotNet = false;
            soapEnvelope.bodyOut = request;

            responseCode = ServerResponseInterface.RESPONSE_CODE_OK;
            return abmServerBinding.getSpecials();
        }
        catch (Exception e) {
            requestCode = ServerResponseInterface.RESPONSE_CODE_EXCEPTION;
            Log.e(TAG, e.getMessage(), e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        if(serverResponseInterface != null) {
            this.serverResponseInterface.responseReceived(s, requestCode, responseCode);
        }
    }
}
