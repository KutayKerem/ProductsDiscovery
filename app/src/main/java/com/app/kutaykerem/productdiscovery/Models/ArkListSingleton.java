package com.app.kutaykerem.productdiscovery.Models;

import java.util.ArrayList;

public class ArkListSingleton {
    private static ArkListSingleton instance = null;
    private ArrayList<String> arkList = null;

    private ArkListSingleton() {
        arkList = new ArrayList<>();
    }

    public static ArkListSingleton getInstance() {
        if (instance == null) {
            instance = new ArkListSingleton();
        }
        return instance;
    }

    public ArrayList<String> getArkList() {
        return arkList;
    }

    public void setArkList(ArrayList<String> arkList) {
        this.arkList = arkList;
    }
}

