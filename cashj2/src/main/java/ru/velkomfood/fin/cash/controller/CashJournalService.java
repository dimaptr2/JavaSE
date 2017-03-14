package ru.velkomfood.fin.cash.controller;

import ru.velkomfood.fin.cash.model.HeadReceiptOrder;
import ru.velkomfood.fin.cash.model.ItemReceiptOrder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by dpetrov on 13.03.2017.
 */
public class CashJournalService {

    private DbEngine dbEngine;
    private ErpRequestor erpRequestor;

    private Map<String, HeadReceiptOrder> heads;
    private List<ItemReceiptOrder> items;

    public CashJournalService() {
        this.dbEngine = DbEngine.getInstance();
        this.erpRequestor = ErpRequestor.getInstance();
        heads = new ConcurrentHashMap<>();
        items = new ArrayList<>();
    }

}
