package com.seriousmonkey.uptimerobot.JsonClasses;

import com.google.gson.Gson;
import com.seriousmonkey.uptimerobot.MainActivity;
import com.seriousmonkey.uptimerobot.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Daniel Sevitti on 2018-03-07.
 */

public class Monitor {
    public JsonInt id;
    public String friendly_name;
    public String url;
    public JsonInt type;
    public JsonInt sub_type;
    public JsonInt keyword_type;
    public String keyword_value;
    public String http_username;
    public String http_password;
    public JsonInt port;
    public JsonInt interval;
    public JsonInt status;
    public Ssl ssl;
    public JsonInt create_datetime;
    public Log[] logs;

    public Monitor(JsonInt id, String friendly_name, String url, JsonInt type, JsonInt sub_type, JsonInt keyword_type, String keyword_value, String http_username, String http_password, JsonInt port, JsonInt interval, JsonInt status, Ssl ssl, JsonInt create_datetime, Log[] logs) {
        this.id  = id;
        this.friendly_name = friendly_name;
        this.url = url;
        this.type = type;
        this.sub_type = sub_type;
        this.keyword_type = keyword_type;
        this.keyword_value = keyword_value;
        this.http_username = http_username;
        this.http_password = http_password;
        this.port = port;
        this.interval = interval;
        this.status = status;
        this.ssl = ssl;
        this.create_datetime = create_datetime;
        this.logs = logs;
    }

    public String getStatus() {
        switch(status.getValue()) {
            case 0:
                return "Paused";
            case 1:
                return "Not Checked Yet";
            case 2:
                return "OK";
            case 8:
                return "Seems Down";
            case 9:
                return "Down";
            default:
                return "Error";
        }
    }

    public String getStatusColor() {
        switch(status.getValue()) {
            case 0:
                return "#b4b924";
            case 1:
                return "#adb924";
            case 2:
                return "#ff99cc00";
            case 8:
                return "#b99124";
            case 9:
                return "#b92455";
            default:
                return "#b92455";
        }
    }

    public Date getCreateDateTime() {
        return new Date(create_datetime.getValue() * 1000L);
    }

    public double getDownTimePercentage(TimeDuration TIME_DURATION) {
        int totalDownTime = 0;

        for(Log log : logs) {
            if( log.type.getValue() == 1) { //if log is down time
                switch (TIME_DURATION) {
                    case DAY: {
                        if(log.datetime.getValue() > (getCurrentDateTime() - secondsInDay)) {
                            totalDownTime += log.duration.getValue();

                            System.out.print("Day Parameters: {totalDownTime=" + totalDownTime + ", log.datetime=" + log.datetime.getValue() + ", getCurrentDateTime=" + getCurrentDateTime() + ", secondsInDay=" + secondsInDay + "} \n");
                        }
                        break;
                    }
                    case WEEK: {
                        if (log.datetime.getValue() > (getCurrentDateTime() - secondsInWeek)) {
                            totalDownTime += log.duration.getValue();

                            System.out.print("Week Parameters: {totalDownTime=" + totalDownTime + ", log.datetime=" + log.datetime.getValue() + ", getCurrentDateTime=" + getCurrentDateTime() + ", secondsInDay=" + secondsInDay + "} \n");
                        }
                        break;
                    }
                    case MONTH: {
                        if (log.datetime.getValue() > (getCurrentDateTime() - secondsInMonth)) {
                            totalDownTime += log.duration.getValue();

                            System.out.print("Month Parameters: {totalDownTime=" + totalDownTime + ", log.datetime=" + log.datetime.getValue() + ", getCurrentDateTime=" + getCurrentDateTime() + ", secondsInDay=" + secondsInDay + "} \n");
                        }
                        break;
                    }
                    case YEAR: {
                        if (log.datetime.getValue() > (getCurrentDateTime() - secondsInYear)) {
                            totalDownTime += log.duration.getValue();

                            System.out.print("Year Parameters: {totalDownTime=" + totalDownTime + ", log.datetime=" + log.datetime.getValue() + ", getCurrentDateTime=" + getCurrentDateTime() + ", secondsInDay=" + secondsInDay + "} \n");
                        }
                        break;
                    }
                    default:
                        break;
                }
            }
        }

        if (totalDownTime > 0) {
            switch (TIME_DURATION) {
                case DAY:
                    return ((double) totalDownTime / (double) secondsInDay) * 100.0;
                case WEEK:
                    return ((double) totalDownTime / (double) secondsInWeek) * 100.0;
                case MONTH:
                    return ((double) totalDownTime / (double) secondsInMonth) * 100.0;
                case YEAR:
                    return ((double) totalDownTime / (double) secondsInYear) * 100.0;
                default:
                    return 0;
            }
        }
        else
        {
            return 0;
        }
    }

    public enum TimeDuration {
        DAY, WEEK, MONTH, YEAR
    }

    public static int secondsInDay = 86400;
    public static int secondsInWeek = 604800;
    public static int secondsInMonth = 2592000;
    public static int secondsInYear = 31536000;

    public static int getCurrentDateTime() {
        return (int) (new Date().getTime() / 1000L);
    }

    public static Date getCurrentDate() {
        return new Date();
    }

    public static String getFormattedDate(Date date, String timeZone) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy 'at' h:mm a");

        sdf.setTimeZone(TimeZone.getTimeZone(timeZone));

        return sdf.format(date);
    }

    public static Monitor getMonitorFromJson(String json) {
        Gson g = new Gson();

        Monitor monitor = g.fromJson(json, Monitor.class);

        return monitor;
        //System.out.println(g.toJson(monitor)); // {"name":"John"}
    }

    public class Ssl {
        public String brand;
        public String product;
        public JsonInt expires;

        public Ssl(String brand, String product, JsonInt expires) {
            this.brand = brand;
            this.product = product;
            this.expires = expires;
        }
    }
}