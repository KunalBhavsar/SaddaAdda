package com.emiadda.Async;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

/**
 * Created by Shraddha on 3/7/16.
 */
public class LoginAsync extends AsyncTask<Void, Void, Void> {

    private static final String SOAP_ACTION = "urn:server/CustomerLogin";
    private static final String METHOD_NAME = "CustomerLogin";
    private static final String NAMESPACE = "urn:server";
    private static final String URL = "http://www.mydevsystems.com/dev/emiaddanew/jnusoap/emiadda/customerLogin.php";

    private String email;
    private String password;

    public LoginAsync(String email, String password) {
        this.email = email;
        this.password = password;
    }

    @Override
    protected Void doInBackground(Void... params) {

        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        request.addProperty("email", email);
        request.addProperty("password", password);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.implicitTypes = true;
        envelope.setAddAdornments(false);
        envelope.setOutputSoapObject(request);
        //envelope.setOutputSoapObject(request);

        HttpTransportSE ht = new HttpTransportSE(URL);
        ht.debug = true;
        try {
            ht.call(SOAP_ACTION, envelope);
            Log.e("LoginAsync req ", ht.requestDump);
            SoapPrimitive response = (SoapPrimitive)envelope.getResponse();
            Log.e("LoginAsync resp" , response.toString());

        } catch (Exception e) {
            Log.e("LoginAsync error" , e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

}

