package ru.velkomfood.dms.info;

import ru.velkomfood.dms.info.controller.DataLoader;
import ru.velkomfood.dms.info.controller.Mailer;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class Launcher {

    public static void main(String[] args) {

        final long INTERVAL = 3600000; // run every hour

        Timer timer = new Timer("Starter");
        TimerTask runner = new DataLoader("uploader");
        timer.scheduleAtFixedRate(runner, 1000, INTERVAL);

        TimerTask mailer = new Mailer();
        Calendar date1 = Calendar.getInstance();
        date1.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
        date1.set(Calendar.HOUR, 10);
        date1.set(Calendar.MINUTE, 55);
        date1.set(Calendar.SECOND, 0);
        timer.schedule(mailer, date1.getTime(), TimeUnit.DAYS.toMillis(7));

    }

}
