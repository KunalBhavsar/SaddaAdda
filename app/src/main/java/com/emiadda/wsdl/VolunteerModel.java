package com.emiadda.wsdl;

import java.io.Serializable;

/**
 * Created by Kunal on 14/08/16.
 */
public class VolunteerModel implements Serializable {
    private static final long serialVersionUID = 0L;

    String volid;
    String vol;

    public String getVolid() {
        return volid;
    }

    public void setVolid(String volid) {
        this.volid = volid;
    }

    public String getVol() {
        return vol;
    }

    public void setVol(String vol) {
        this.vol = vol;
    }
}
