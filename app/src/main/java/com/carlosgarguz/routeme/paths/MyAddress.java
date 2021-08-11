package com.carlosgarguz.routeme.paths;

import com.google.android.gms.maps.model.LatLng;

public class MyAddress {

    private String textAddress;
    private LatLng coordinates;

    public MyAddress(String textAddress, LatLng coordinates) {
        this.textAddress = textAddress;
        this.coordinates = coordinates;
    }

    public MyAddress() {
        textAddress = "";
        coordinates = new LatLng(0.0, 0.0);
    }

    public String getTextAddress() {
        return textAddress;
    }

    public LatLng getCoordinates() {
        return coordinates;
    }

    public void setTextAddress(String textAddress) {
        this.textAddress = textAddress;
    }

    public void setCoordinates(LatLng coordinates) {
        this.coordinates = coordinates;
    }
}