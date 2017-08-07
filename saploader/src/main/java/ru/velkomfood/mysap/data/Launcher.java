package ru.velkomfood.mysap.data;


import ru.velkomfood.mysap.data.controller.DbWriter;
import ru.velkomfood.mysap.data.controller.SapReader;

import java.time.LocalDate;

public class Launcher {

    public static void main(String[] args) {

        LocalDate moment = LocalDate.now();
        boolean masterData;

        if (args.length == 1) {

            if (!args[0].equals("1")) {
                System.out.println("Enter only 1");
                System.exit(-1);
            }
            masterData = true;

        } else {
            masterData = false;
        }


        SapReader sapReader = new SapReader(masterData);
        sapReader.setCurrentDate(moment);
        sapReader.run();
        DbWriter dbWriter = new DbWriter(sapReader);
        dbWriter.start();


    } // MAIN


} // LAUNCHER
