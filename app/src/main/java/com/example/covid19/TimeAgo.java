package com.example.covid19;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TimeAgo {

    public String getTimeAgo(long duration) {
        Date now = new Date();

        long seconds = TimeUnit.MILLISECONDS.toSeconds(now.getTime()-duration);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(now.getTime()-duration);
        long hours = TimeUnit.MILLISECONDS.toHours(now.getTime()-duration);
        long days = TimeUnit.MILLISECONDS.toDays(now.getTime()-duration);

        if (seconds < 60){
            return "şu an";
        }
        else if (minutes >= 1 && minutes < 60){
            return minutes + " dakika önce";
        } else if (hours >= 1 && hours < 24){
            return hours + " saat önce";
        } else {
            return days + " gün önce";
        }
    }
}