package ru.velkomfood.mysap.data;


import com.sap.conn.jco.JCoException;
import ru.velkomfood.mysap.data.controller.DbWriterMaster;
import ru.velkomfood.mysap.data.controller.SapReader;
import ru.velkomfood.mysap.data.controller.TaskHandler;
import java.time.LocalDate;
import java.util.Timer;

public class Launcher {

    public static void main(String[] args) {

        final long INTERVAL = 10000; // Every 10 seconds

        LocalDate moment = LocalDate.now();


        DbWriterMaster dbWriterMaster = DbWriterMaster.getInstance();
        SapReader sapReader = SapReader.getInstance(dbWriterMaster);
        sapReader.setCurrentDate(moment);
        try {
            sapReader.initSAPDestination();
        } catch (JCoException sapex) {
            sapex.printStackTrace();
        }
        // Start the timer and schedule tasks
        Timer timer1 = new Timer();
        TaskHandler handler = TaskHandler.getInstance();
        handler.setSapReader(sapReader);

//        DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd hh:mm");
//        String txtDate = moment + " " + "07:05";
        timer1.scheduleAtFixedRate(handler, 1000, INTERVAL);

    } // MAIN


} // LAUNCHER
