package com.emiadda.wsdl;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Kunal on 14/08/16.
 */
public class DistrictModel implements Serializable {
    List<RegionModel> districtData;
    List<VolunteerModel> volunteerData;

    public List<RegionModel> getDistrictData() {
        return districtData;
    }

    public void setDistrictData(List<RegionModel> districtData) {
        this.districtData = districtData;
    }

    public List<VolunteerModel> getVolunteerData() {
        return volunteerData;
    }

    public void setVolunteerData(List<VolunteerModel> volunteerData) {
        this.volunteerData = volunteerData;
    }
}
