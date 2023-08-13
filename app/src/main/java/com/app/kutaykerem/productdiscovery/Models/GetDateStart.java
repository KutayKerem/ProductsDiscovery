package com.app.kutaykerem.productdiscovery.Models;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class GetDateStart {
    public static String calculateElapsedTime(Date startTime) {
        Date endTime = new Date(); // Geçerli zamanı bitiş zamanı olarak kullanıyoruz
        long durationInMillis = endTime.getTime() - startTime.getTime();

        long hours = TimeUnit.MILLISECONDS.toHours(durationInMillis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(durationInMillis) % 60;
        long days = TimeUnit.MILLISECONDS.toDays(durationInMillis);
        long months = days / 30; // Varsayılan olarak 30 gün kabul ediyoruz.



        String elapsedTime = "";
        if (months > 0) {
            elapsedTime += months + " ay ";
        }
        if (days > 0) {
            elapsedTime += days % 30 + " gün ";
        }
        if (hours > 0) {
            elapsedTime += hours + " saat ";
        }
        if (minutes > 0) {
            elapsedTime += minutes + " dakika ";
        }

        return elapsedTime.trim();
    }

}
