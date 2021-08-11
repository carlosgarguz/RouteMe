package com.carlosgarguz.routeme.paths;

import java.util.ArrayList;

public class Route{

    private int time;
    private ArrayList<Integer> order;

    public Route(int time, ArrayList<Integer> order) {
        this.time = time;
        this.order = order;
    }

    public Route(int time) {
        this.time = time;
        this.order = new ArrayList<Integer>();
    }

    public Route() {
        this.time = 0;
        this.order = new ArrayList<Integer>();
    }

    public int getTime() {
        return this.time;
    }

    public ArrayList<Integer> getOrder() {
        return this.order;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public void setOrder(ArrayList<Integer> order) {
        this.order = order;
    }

}