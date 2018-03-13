package com.seriousmonkey.uptimerobot.JsonClasses;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by Daniel Sevitti on 2018-03-07.
 */

public class Monitors {
    public String stat;
    public Pagination pagination;
    public Monitor[] monitors;

    public Monitors(String stat, Pagination pagination, Monitor[] monitors) {
        this.stat = stat;
        this.pagination = pagination;
        this.monitors = monitors;
    }

    public int getUpMonitorCount() {
        return getMonitorStatusCount(2);
    }

    public int getDownMonitorCount() {
        return getMonitorStatusCount(9);
    }

    public int getPausedMonitorCount() {
        return getMonitorStatusCount(0);
    }

    public int getMonitorStatusCount(int status) {
        int monitorCount = 0;

        if(monitors != null && monitors.length > 0) {
            for(Monitor monitor : monitors) {
                if(monitor.status.getValue() == status) {
                    monitorCount += 1;
                }
            }
        }

        return monitorCount;
    }

    public static Monitors getMonitorsFromJson(String json) {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(JsonInt.class, new JsonIntTypeAdapter());

        Gson g = builder.create();

        Monitors monitors = g.fromJson(json, Monitors.class);

        return monitors;
        //System.out.println(g.toJson(monitors)); // {"name":"John"}
    }
}
