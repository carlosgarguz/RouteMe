package com.carlosgarguz.routeme.utils;

import java.util.ArrayList;

public class RouteCardDb {

    private int id;
    private String name;
    private ArrayList<String> listDestinations;



    public RouteCardDb() {
    }

    public RouteCardDb(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getListDestinations() {
        return listDestinations;
    }

    public void setListDestinations(ArrayList<String> listDestinations) {
        this.listDestinations = listDestinations;
    }
}
