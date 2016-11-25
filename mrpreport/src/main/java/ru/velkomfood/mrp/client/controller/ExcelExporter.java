package ru.velkomfood.mrp.client.controller;

import javafx.collections.ObservableList;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import ru.velkomfood.mrp.client.view.OutputView;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Iterator;

/**
 * Created by dpetrov on 19.08.2016.
 */
public class ExcelExporter {

    private static ExcelExporter instance;

    private ExcelExporter() { }

    public static ExcelExporter getInstance() {
        if (instance == null) instance = new ExcelExporter();
        return instance;
    }

    // Read an observable list and export his data into excel file,
    // that can take a file name from the variable "fileName".
    public void exportDataToExcelFile(String fileName, String tabName,
                                      ObservableList<OutputView> resultList)
            throws IOException, ClassNotFoundException {

        int height = resultList.size();

        String[][] data = new String[height][20];

        Iterator<OutputView> it = resultList.iterator();
        int i = 0, j = 0;
        while (it.hasNext()) {
            OutputView view = it.next();
            data[i][j] = view.getMaterial();
            j++;
            data[i][j] = view.getMaterialDescription();
            j++;
            data[i][j] = String.valueOf(view.getWerks());
            j++;
            data[i][j] = String.valueOf(view.getYear());
            j++;
            data[i][j] = view.getPurchaseGroup();
            j++;
            data[i][j] = view.getNameOfPurchaseGroup();
            j++;
            data[i][j] = view.getBaseUnit();
            j++;
            data[i][j] = view.getStocks();
            j++;
            data[i][j] = view.getReq01();
            j++;
            data[i][j] = view.getReq02();
            j++;
            data[i][j] = view.getReq03();
            j++;
            data[i][j] = view.getReq04();
            j++;
            data[i][j] = view.getReq05();
            j++;
            data[i][j] = view.getReq06();
            j++;
            data[i][j] = view.getReq07();
            j++;
            data[i][j] = view.getReq08();
            j++;
            data[i][j] = view.getReq09();
            j++;
            data[i][j] = view.getReq10();
            j++;
            data[i][j] = view.getReq11();
            j++;
            data[i][j] = view.getReq12();
            j = 0;
            i++;
        }

        Workbook wb = new HSSFWorkbook();
        FileOutputStream fileOut = new FileOutputStream(fileName);
        Sheet sheet = wb.createSheet(tabName);

        try {

            Row[] rows = new Row[data.length];
            Cell[][] cells = new Cell[rows.length][];

            for (int k = 0; k < rows.length; k++) {
                rows[k] = sheet.createRow(k);
                cells[k] = new Cell[data[k].length];
                for (int l = 0; l < cells[k].length; l++) {
                    cells[k][l] = rows[k].createCell(l);
                    cells[k][l].setCellValue(data[k][l]);
                }
            }

            wb.write(fileOut);

        } finally {

            fileOut.close();

        }

    }

    // Read an excel file and import his data into JavaFX window.
//    public ObservableList<OutputView> importDataFromExcelFile(String fileName)
//        throws FileNotFoundException {
//
//        ObservableList<OutputView> resultList = FXCollections.observableArrayList();
//
//        return resultList;
//    }

}
