package ru.velkomfood.fin.tests;

import org.junit.Test;
import ru.velkomfood.fin.data.PrintFormat;
import ru.velkomfood.fin.data.Receipt;
import ru.velkomfood.fin.data.ReceiptItem;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by dpetrov on 30.03.2017.
 */
public class TestPrintForm {

    private final String FPATH = "C:/Users/DPETROV/velkomfood.ru/Java/SE/receipt/info";

    @Test
    public void testPrintFormat() {

        PrintFormat pf = new PrintFormat();
        List<Receipt> receiptList = prepareTestList();

        Iterator<Receipt> iter = receiptList.iterator();
        while (iter.hasNext()) {
            Receipt r = iter.next();
            for (ReceiptItem ri: r.getItems()) {
                double value1 = 3000.00;
                double value2 = 169750.04;
                Receipt result = pf.buildReceipt(ri.getReceiptId(), BigDecimal.valueOf(value1),
                         BigDecimal.valueOf(value2), r);
            }
        }

    }

    private List<Receipt> prepareTestList() {

        List<Receipt> rcs = new ArrayList<>();

        List<String> heads = readHeads();
        List<String> items = readItems();
        DecimalFormat df = new DecimalFormat("0.0000");


        for (String h: heads) {
            long id = Long.parseLong(h);
            Receipt receipt = new Receipt();
            receipt.setId(id);
            for (String it: items) {
                String[] temp = it.split("-");
                long key = Long.parseLong(temp[0]);
                if (id == key) {
                    ReceiptItem ri = new ReceiptItem();
                    ri.setReceiptId(id);
                    ri.setPosition(Long.parseLong(temp[1]));
                    ri.setMaterial(temp[2]);
                    ri.setMaterialName(temp[3]);
                    BigDecimal quan = null;
                    BigDecimal amount = null;
                    quan = new BigDecimal(temp[4]);
                    amount = new BigDecimal(temp[5]);
                    ri.setQuantity(quan);
                    ri.setAmount(amount);
                    receipt.getItems().add(ri);
                }
            } //for
            rcs.add(receipt);
        } // for

        return rcs;
    }

    // Delivery heads
    private List<String> readHeads() {

        List<String> receipts = new ArrayList<>();
        String filePath = FPATH + "/receipts.txt";

        try (Stream<String> stream = Files.lines(Paths.get(filePath))) {
            stream.forEach(line -> {
                receipts.add(line);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return receipts;
    }

    // Delivery items
    private List<String> readItems() {

        List<String> items = new ArrayList<>();
        String filePath = FPATH + "/items.txt";

        try (Stream<String> stream = Files.lines(Paths.get(filePath))) {
            stream.forEach(line -> {
                items.add(line);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return items;
    }

}
