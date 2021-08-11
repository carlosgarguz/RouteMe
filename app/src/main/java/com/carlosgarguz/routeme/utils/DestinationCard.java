package com.carlosgarguz.routeme.utils;

public class DestinationCard {


    //private String destinationNumber;
    private String destinationName;
    private String stopTime;
    private int numberStopTime;
    private boolean lastPoint;
    private double latitude;
    private double longitude;
    private int id;


    public DestinationCard() {
    }





    /*public String getdestinationNumber() {
        return destinationNumber;
    }*/

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public boolean isLastPoint() {
        return lastPoint;
    }

    public void setLastPoint(boolean lastPoint) {
        this.lastPoint = lastPoint;
    }

    public String getStopTime() {
        return stopTime;
    }

    public void setStopTime(String stopTime) {
        this.stopTime = stopTime;
    }

    public String getDestinationName() {
        return destinationName;
    }

    /*public void setdestinationNumber(String destinationNumber) {
        this.destinationNumber = destinationNumber;
    }*/

    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumberStopTime() {
        return numberStopTime;
    }

    public void setNumberStopTime(int numberStopTime) {
        this.numberStopTime = numberStopTime;
    }
}
