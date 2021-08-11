package com.carlosgarguz.routeme.utils;

public class RouteCard {

    private String startingPointName;
    private String endingPointName;
    private String durationText;
    private long durationNumber;
    private int stopTime;

    public RouteCard() {
    }


    public RouteCard(String startingPointName, String endingPointName, String durationText, int durationNumber) {
        this.startingPointName = startingPointName;
        this.endingPointName = endingPointName;
        this.durationText = durationText;
        this.durationNumber = durationNumber;
    }

    public String getStartingPointName() {
        return startingPointName;
    }

    public void setStartingPointName(String startingPointName) {
        this.startingPointName = startingPointName;
    }

    public String getEndingPointName() {
        return endingPointName;
    }

    public void setEndingPointName(String endingPointName) {
        this.endingPointName = endingPointName;
    }

    public String getDurationText() {
        return durationText;
    }

    public void setDurationText(String durationText) {
        this.durationText = durationText;
    }

    public long getDurationNumber() {
        return durationNumber;
    }

    public void setDurationNumber(long durationNumber) {
        this.durationNumber = durationNumber;
    }

    public int getStopTime() {
        return stopTime;
    }

    public void setStopTime(int stopTime) {
        this.stopTime = stopTime;
    }
}
