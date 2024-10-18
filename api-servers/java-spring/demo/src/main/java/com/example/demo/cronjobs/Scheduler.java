package com.example.demo.cronjobs;


import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class Scheduler {

    @Scheduled(cron="0 * 9 * * ?")
    public void cronJob() throws Exception {}

    @Scheduled(cron="0 * 9 * * ?") // * Scheduler triggers cron at specific time (every minute at 9-hour)
    public void job1() throws Exception {
        SimpleDateFormat format = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss"
        );
        Date now = new Date();
        System.out.println(
                "Java cron job expression: "
                + format.format(now)
        );
    }

    @Scheduled(fixedRate=1000) // * triggers on app-startup, on every fixedRate millisecond(s) (1000ms)
    public void fixedFateSch() {
        SimpleDateFormat format = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss"
        );
        Date now = new Date();
        System.out.println(
                "Fixed Rate Scheduler: "
                + format.format(now)
        );
    }

    @Scheduled(fixedDelay=1000, initialDelay=3000) // * triggers after initialDelay on app-startup; then re-triggers after every fixedDelay (in ms)
    // (triggered for every 1s, after 1st 3s after app-startup)
    public void fixedDelaySch() {
        SimpleDateFormat format = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss"
        );
        Date now = new Date();
        System.out.println(
                "Fixed Delay Scheduler: "
                + format.format(now)
        );
    }

}
