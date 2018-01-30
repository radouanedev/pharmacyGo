package com.happypeople.pharmacygo;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Radouane on 10/01/2018.
 */

public class Pharmacy {

    private String id;
    private LatLng location;
    private String place_id;

    public Pharmacy(String id, LatLng location, String place_id) {
        this.id = id;
        this.location = location;
        this.place_id = place_id;
    }

    public String getId() {
        return id;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }
}
