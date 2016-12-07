package ru.velkomfood.mysap.synchronizer;

import com.sap.conn.jco.JCoException;
import ru.velkomfood.mysap.synchronizer.controller.MySqlWriter;
import ru.velkomfood.mysap.synchronizer.controller.SapReader;

import java.sql.SQLException;
import java.util.Date;

public class Launcher {

  public static void main(String[] args) throws JCoException, SQLException, InterruptedException {



    if (args.length == 1) {

      int parameter = Integer.parseInt(args[0]);

      // 1 - upload master data
      // 2 - upload BOMs
      // 3 - upload transactional data

      System.out.println("Start synchronization process ...");
      long start = new Date().getTime();
      SapReader srd1 = new SapReader("sap-01");
      srd1.setFlag("AM"); // all materials
      MySqlWriter myWriter = new MySqlWriter();
      // Set links to the threads
      srd1.setSqlWriter(myWriter);
      // Start threads ...
      srd1.start();

      switch (parameter) {
        case 1:
          srd1.run();
          long finish = new Date().getTime();
          long interval = ( finish - start ) / 1000;
          String tunits = "";
          if (interval >= 0 && interval <= 60) tunits = "seconds";
          else {
            tunits = "minutes";
            interval = interval / 60;
          }
          System.out.printf("Main thread: time of execution is %d " + tunits + "\r\n", interval);
          break;

        case 2:
          break;
        case 3:
          break;
      }

    } else {
      System.out.println("Usage the program with parameters: 1 | 2 | 3");
      System.out.println("1 - upload master data");
      System.out.println("2 - upload BOMs");
      System.out.println("3 - upload transaction data");
    }


  } // main method

}
