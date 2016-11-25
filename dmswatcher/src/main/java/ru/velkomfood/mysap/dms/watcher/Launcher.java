package ru.velkomfood.mysap.dms.watcher;

import ru.velkomfood.mysap.dms.watcher.controller.ErpReader;
import java.util.Date;

public class Launcher {

  public static void main(String[] args) throws Exception {

      long start = new Date().getTime();

      System.out.println("DMS Watcher is started!");

      ErpReader erpReader = ErpReader.getInstance();

      if (erpReader.isAlive()) {
          erpReader.initJcoDestionation();
          erpReader.readSapData();
          System.out.printf("Number of documents is %d\r\n", erpReader.getDocuments().size());
          erpReader.readDmsDetails();
          erpReader.clearDmsList();
          System.out.printf("Number of details is %d\r\n", erpReader.getDetails().size());
          erpReader.sendEmail();
      }

      long finish = new Date().getTime();

      long result = Math.round(Math.floor(((double) (finish - start)) / 1000));
      String answer = "";

      if (result >= 0 && result <= 60) {
          answer = "Time of execution is " + result + " seconds";
      }
      else {
          result = Math.round(result / 60);
          answer = "Time of execution is " + result + " minutes";
      }

      System.out.println(answer);
      System.out.println("DMS Watcher is finished!");

  } // main

}
