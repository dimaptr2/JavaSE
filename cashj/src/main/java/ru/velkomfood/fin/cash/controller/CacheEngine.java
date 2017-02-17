package ru.velkomfood.fin.cash.controller;

import ru.velkomfood.fin.cash.model.*;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by dpetrov on 13.02.2017.
 */
public class CacheEngine {

    // Data collections
    private RangeDates dates;
    private Map<Long, Material> materials;
    private List<CashJournal> journalList;
    private List<DeliveryHead> heads;
    private List<DeliveryItem> items;
    private List<Receipt> receipts;

    private static CacheEngine instance;

    private CacheEngine() {
        dates = new RangeDates();
        materials = new ConcurrentHashMap<>();
        journalList = new ArrayList<>();
        heads = new ArrayList<>();
        items = new ArrayList<>();
        receipts = new ArrayList<>();
    }

    public static CacheEngine getInstance() {
        if (instance == null) {
            instance = new CacheEngine();
        }
        return instance;
    }

    // accessors
    public RangeDates getDates() {
        return dates;
    }

    public Map<Long, Material> getMaterials() {
        return materials;
    }

    public List<CashJournal> getJournalList() {
        return journalList;
    }

    public List<DeliveryHead> getHeads() {
        return heads;
    }

    public List<DeliveryItem> getItems() {
        return items;
    }

    public List<Receipt> getReceipts() {
        return receipts;
    }

    // Build a print form of receipt
    public void buildReceipt() {

        if (!receipts.isEmpty()) {
            receipts.clear();
        }
        // Read cash journal
        // Z_RFC_MATERIAL_UNIT_CONV = convert a unit of quantity from any UOM to KILOGRAM
        // Process receipts
        Iterator<CashJournal> itcj = journalList.iterator();

        while (itcj.hasNext()) {
            CashJournal cj = itcj.next();
            Receipt receipt = new Receipt();
            receipt.setId(cj.getId());
            receipt.setCajoNumber(cj.getCajoNumber());
            receipt.setCompanyCode(cj.getCompanyCode());
            receipt.setYear(cj.getYear());
            receipt.setPostingNumber(cj.getPostingNumber());
            receipt.setAmountReceipt(cj.getAmountReceipt());
            receipt.setPartnerName(cj.getPartnerName());
            receipt.setDocumentDate(cj.getDocumentDate());
            receipt.setPostingDate(cj.getPostingDate());
            receipt.setDocumentNumber(cj.getDocumentNumber());
            receipt.setPositionText(cj.getPositionText());
            receipt.setDeliveryId(cj.getDeliveryId());
            for (DeliveryItem di: items) {
                if (di.getDeliveryId().equals(receipt.getDeliveryId())) {
                    ReceiptItem ri = new ReceiptItem();
                    ri.setDeliveryId(di.getDeliveryId());
                    ri.setPosId(di.getPosId());
                    ri.setMaterialId(di.getMaterialId());
                    ri.setMaterialName(di.getMaterialName());
                    ri.setUom(di.getUom());
                    ri.setQuantity(di.getQuantity());
                    ri.setQuantityKG(di.getQuantityKG());
                    receipt.addItem(ri);
                }
            }
            receipts.add(receipt);
        }

        // take out the trash
        journalList.clear();
        heads.clear();
        items.clear();

    } // build a print form

    // Calculate sums
    public void calculateReceiptSums(List<Integer> positions) {

        // Delete all items that haven't contained in the collection "positions".
        if (positions != null && !positions.isEmpty()) {
            Iterator<Receipt> it = receipts.iterator();
            while (it.hasNext()) {
                Receipt receipt = it.next();
                for (int index : positions) {
                    for (ReceiptItem ri: receipt.getItems()) {
                        if (index != ri.getPosId()) {
                            receipt.getItems().remove(ri);
                        }
                    }
                }
            }
        }

        Iterator<Receipt> it = receipts.iterator();
        // Calculate total quantities for the deliveries
        Map<Long, BigDecimal> totalQuantities = calculateTotalQuantityByDelivery(receipts);
        // For the rounding to two decimal places
        DecimalFormat df = new DecimalFormat("#0.00");

        // Calculate amounts in the positions
        // For the local format of numbers need commas,
        // therefore all commas will be replaced on points.
        for (Receipt r: receipts) {
            Double sumTotal = r.getAmountReceipt().doubleValue();
            for (ReceiptItem item: r.getItems()) {
                if (totalQuantities.containsKey(item.getDeliveryId())) {
                    Double totalQuantity = totalQuantities.get(item.getDeliveryId()).doubleValue();
                    // Amount in Pos = sumTotal * (itemQuantity / totalQuantity)
                    Double result = 0.000;
                    if (totalQuantity != 0.000) {
                        result = sumTotal * (item.getQuantity().doubleValue() / totalQuantity);
                    }
                    String strResult = df.format(result);
                    strResult = strResult.replace(",", ".");
                    item.setAmount(new BigDecimal(strResult));
                }
            }
        }

    } // sum calculation

    // Calculate a total quantity by every delivery
    private Map<Long, BigDecimal> calculateTotalQuantityByDelivery(List<Receipt> rs) {

        Map<Long, BigDecimal> totals = new ConcurrentHashMap<>();
        Iterator<Receipt> iterator = rs.iterator();

        while (iterator.hasNext()) {
            Receipt r = iterator.next();
            Double q = 0.000;
            for (ReceiptItem item: r.getItems()) {
                if (r.getDeliveryId().equals(item.getDeliveryId())) {
                    q += item.getQuantityKG().doubleValue();
                }
            }
            totals.put(r.getDeliveryId(), BigDecimal.valueOf(q));
        }

        return totals;
    }

    // Clear all collections
    public void refreshData() {
        materials.clear();
        journalList.clear();
        heads.clear();
        items.clear();
//        receipts.clear();
    }

}
