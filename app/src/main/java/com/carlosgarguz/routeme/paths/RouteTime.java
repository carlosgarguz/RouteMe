package com.carlosgarguz.routeme.paths;

public class RouteTime{

    //public static int id = 1000;

    private String textTime;
    private long timeInSeconds;
    private int startPointID;
    private int endPointID;
    private String startPointName;
    private String endPointName;


    public RouteTime(String textTime, long timeInSeconds, int startPointID, int endPointID) {
        this.textTime= textTime;
        this.timeInSeconds = timeInSeconds;
        this.startPointID = startPointID;
        this.endPointID = endPointID;
    }

    public RouteTime() {
        this.textTime ="";
        this.timeInSeconds=0;
        this.startPointID = -1;
        this.endPointID = -1;
        this.startPointName = "";
        this.endPointName = "";
    }

    public RouteTime(int startPointID, int endPointID) {
        this.textTime ="";
        this.timeInSeconds=0;
        this.startPointID = startPointID;
        this.endPointID = endPointID;
    }

    public int getStartPointID() {
        return startPointID;
    }

    public void setStartPointID(int startPointID) {
        this.startPointID = startPointID;
    }

    public int getEndPointID() {
        return endPointID;
    }

    public void setEndPointID(int endPointID) {
        this.endPointID = endPointID;
    }

    public String getTextTime() {
        return textTime;
    }

    public long getTimeInSeconds() {
        return timeInSeconds;
    }

    public void setTextTime(String textTime) {
        this.textTime = textTime;
    }

    public void setTimeInSeconds(long timeInSeconds) {
        this.timeInSeconds = timeInSeconds;
    }

    public String getStartPointName() {
        return startPointName;
    }

    public void setStartPointName(String startPointName) {
        this.startPointName = startPointName;
    }

    public String getEndPointName() {
        return endPointName;
    }

    public void setEndPointName(String endPointName) {
        this.endPointName = endPointName;
    }
}