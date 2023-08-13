package com.app.kutaykerem.productdiscovery.Models;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class GetDateDetailTurkish {

    public static String calculateElapsedTime(Date startTime, Date endTime) {
        long durationInMillis = endTime.getTime() - startTime.getTime();
        long hours = TimeUnit.MILLISECONDS.toHours(durationInMillis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(durationInMillis) % 60;
        long days = TimeUnit.MILLISECONDS.toDays(durationInMillis);
        long months = days / 30;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startTime);
        calendar.setTime(endTime);

        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

        String elapsedTime = "";


        if (days == 0) {
            if (hours == 0 && minutes == 0) {
                elapsedTime = "Şimdi";
            } else {
                calendar.setTime(startTime);
                int startHour = calendar.get(Calendar.HOUR_OF_DAY);
                int startMinute = calendar.get(Calendar.MINUTE);

                if (currentHour < startHour || (currentHour == startHour && currentMinute < startMinute)) {
                    elapsedTime = "Dün " + timeFormat.format(startTime); // Saat 00.00 dan Önceki süre
                } else {
                    elapsedTime = "" + timeFormat.format(startTime); // Sonraki süre
                }
            }
        } else {
            if (days >= 1 && days < 2) {
                elapsedTime = days + " gün önce " ;
            } else if (days >= 2 && days < 7) {
                elapsedTime = (days) + " gün önce ";
            } else if (days >= 7 && days < 28) {
                int weeks = (int) (days / 7);
                elapsedTime = weeks + " hafta önce ";
            }else if(days == 28){
                int months2 = (int) (days / 28);
                elapsedTime = months2 + " ay önce ";
            }else if(days == 29){
                int months2 = (int) (days / 29);
                elapsedTime = months2 + " ay önce ";
            }
            else if(days >= 30){
                int months2 = (int) (days / 30);
                elapsedTime = months2 + " ay önce ";
            }

            else if (days >= 365) {
                int years = (int) (days / 365);
                elapsedTime = years + " yıl önce ";
            }



        }

        return elapsedTime;
    }

}
