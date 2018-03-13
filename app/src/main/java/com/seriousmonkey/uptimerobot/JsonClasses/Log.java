package com.seriousmonkey.uptimerobot.JsonClasses;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Daniel Sevitti on 2018-03-07.
 */

public class Log {
    public JsonInt type;
    public JsonInt datetime;
    public JsonInt duration;
    public Reason reason;

    public Log(JsonInt type, JsonInt datetime, JsonInt duration, Reason reason) {
        this.type = type;
        this.datetime = datetime;
        this.duration = duration;
        this.reason = reason;
    }

    public String getType() {
        switch(type.getValue()) {
            case 1:
                return "Down";
            case 2:
                return "Up";
            case 98:
                return "Started";
            case 99:
                return "Paused";
            default:
                return "Error";
        }
    }

    public Date getDateTime() {
        return new Date(datetime.getValue() * 1000L);
    }

    public String getFormattedDateTime(Boolean Long) {
        SimpleDateFormat sdf;

        if(Long){
            sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        }
        else {
            sdf = new SimpleDateFormat("MMM d, yyyy 'at' h:mm a");
        }

        sdf.setTimeZone(TimeZone.getTimeZone("GMT-5"));

        return sdf.format(getDateTime());
    }

    public String getFormattedDuration(Boolean Long) {
        int hours = duration.getValue() / 3600;
        int minutes = (duration.getValue() % 3600) / 60;
        int seconds = duration.getValue() % 60;

        if(Long) {
            return String.format("%d hrs, %d mins, %d seconds", hours, minutes, seconds);
        }
        else{
            return String.format("%d hrs, %d mins", hours, minutes);
        }

        //return DateUtils.formatElapsedTime((long) duration.getValue());
    }

    public class Reason {
        public String code;
        public String detail;

        public Reason(String code, String detail) {
            this.code = code;
            this.detail = detail;
        }
    }

}