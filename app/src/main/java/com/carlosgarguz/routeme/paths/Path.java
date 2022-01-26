package com.carlosgarguz.routeme.paths;

import android.location.Address;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class Path {

    private MyAddress startingPoint;
    private MyAddress endingPoint;
    private ArrayList<String> polylines;
    private ArrayList<LatLng> points;
    private RouteTime duration;
    private String mode;

    public Path(){
        this.startingPoint = new MyAddress();
        this.endingPoint = new MyAddress();
        this.polylines = new ArrayList<String>();
        this.points = new ArrayList<LatLng>();
        this.duration = new RouteTime();
    }

    public MyAddress getStartingPoint() {
        return startingPoint;
    }

    public MyAddress getEndingPoint() {
        return endingPoint;
    }

    public ArrayList<String> getPolylines() {
        return polylines;
    }

    public ArrayList<LatLng> getPoints() {
        return points;
    }

    public RouteTime getDuration() {
        return duration;
    }

    public String getMode() {
        return mode;
    }

    public void setStartingPoint(MyAddress startingPoint) {
        this.startingPoint = startingPoint;
    }

    public void setEndingPoint(MyAddress endingPoint) {
        this.endingPoint = endingPoint;
    }

    public void setPolylines(ArrayList polylines) {
        this.polylines = polylines;
    }

    public void setPoints(ArrayList<LatLng> points) {
        this.points = points;
    }

    public void setDuration(RouteTime duration) {
        this.duration = duration;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public static String getApiUrl(LatLng startingPoint, LatLng destination, String modo, boolean avoidTolls, String key){
        String url = "https://maps.googleapis.com/maps/api/directions/json?";
        String origin = "origin=" + startingPoint.latitude+ "," + startingPoint.longitude;
        String destinationText = "destination=" + destination.latitude + "," + destination.longitude;
        String mode = "mode=" + modo;
        String sKey = "key=" + key;
        if(avoidTolls) {
            return url + origin + "&" + destinationText + "&" + mode + "&" + "avoid=tolls&" + "language=es&" + sKey;
        }else{
            return url + origin + "&" + destinationText + "&" + mode + "&" + "language=es&" + sKey;
        }

    }

    public static String getMatrixApiUrl( ArrayList<LatLng> destinationsCoordinates, String modo, String language, boolean avoidTolls, String key ){
        String url = "https://maps.googleapis.com/maps/api/distancematrix/json?";

        String origins = "origins=";
        for(int i=0; i<destinationsCoordinates.size(); i++){
            origins = origins + destinationsCoordinates.get(i).latitude + "," + destinationsCoordinates.get(i).longitude;
            if(i!=destinationsCoordinates.size()-1)
                origins = origins + "|";
        }

        String destinations = "destinations=";
        for(int i=0; i<destinationsCoordinates.size(); i++){
            destinations = destinations + destinationsCoordinates.get(i).latitude + "," + destinationsCoordinates.get(i).longitude;
            if(i!=destinationsCoordinates.size()-1)
                destinations = destinations + "|";
        }

        String mode =  "mode=" + modo;
        String slanguage = "language=" + language;
        String sKey = "key=" + key;

        if(avoidTolls) {
            return url + origins + "&" + destinations + "&" + mode + "&" + slanguage + "&" + "avoid=tolls&" + sKey;
        }else{
            return url + origins + "&" + destinations + "&" + mode + "&" + slanguage + "&" + sKey;
        }
    }

    //Puedes intentar hacerlo recursivo
    public ArrayList<LatLng> decodePoly(String encoded) {

        ArrayList<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

    @Override
    public String toString() {
        String result =  "El path tiene:\n salida nombre: " + getStartingPoint().getTextAddress() + "\n " +
                "salida coordenadas: " + String.valueOf(getStartingPoint().getCoordinates().latitude) + ", "+ String.valueOf(getStartingPoint().getCoordinates().longitude) +
                "\n destino nombre: " + getEndingPoint().getTextAddress() +
                "\n destino coordenadas: " + String.valueOf(getEndingPoint().getCoordinates().latitude) + ", "+ String.valueOf(getEndingPoint().getCoordinates().longitude) +
                "\n tiempo de ruta: " +getDuration().getTextTime() + ", " + String.valueOf(getDuration().getTimeInSeconds()) + "segundos";
        for (int i=0; i<getPolylines().size(); i++){
            result = result + "\n polyline " + i + ": " + getPolylines().get(i);
        }
        return result;
    }
}


