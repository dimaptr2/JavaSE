package ru.velkomfood.dms.info.controller;

import java.util.TimerTask;

public class Mailer extends TimerTask {

    @Override
    public void run() {
        System.out.append("Send e-mails\n");
    }

}
