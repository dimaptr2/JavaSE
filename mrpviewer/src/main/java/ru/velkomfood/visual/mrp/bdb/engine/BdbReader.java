package ru.velkomfood.visual.mrp.bdb.engine;

import com.sleepycat.je.DatabaseException;
import ru.velkomfood.visual.mrp.bdb.entities.MrpDataEntity;
import ru.velkomfood.visual.mrp.bdb.entities.MrpPrimaryKey;
import ru.velkomfood.visual.mrp.model.entities.MaterialEntity;
import ru.velkomfood.visual.mrp.model.entities.Months;
import ru.velkomfood.visual.mrp.model.entities.PurchaseGroup;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by dpetrov on 10.08.16.
 */

public class BdbReader {

    private static BdbReader instance;

    private List<MaterialEntity> materials;
    private Map<String, PurchaseGroup> purchaseGroups;
    private int year;
    private List<Months> periods;
    private Map<String, MrpPrimaryKey> keys;
    private Map<String, MrpDataEntity> dataWarehouse;

    private BdbReader() {

        keys = new ConcurrentHashMap<>();
        dataWarehouse = new ConcurrentHashMap<>();

    }

    public static BdbReader getInstance() {

        if (instance == null) instance = new BdbReader();

        return instance;
    }

    public void setMaterials(List<MaterialEntity> materials) {
        this.materials = materials;
    }

    public void setPurchaseGroups(Map<String, PurchaseGroup> purchaseGroups) {
        this.purchaseGroups = purchaseGroups;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setPeriods(List<Months> periods) {
        this.periods = periods;
    }

    public void fetchMrpPrimaryKeys(Map<String, String> params) throws DatabaseException {

        if (!keys.isEmpty()) keys.clear();


    }

    public Map<String, MrpDataEntity> fetchMrpData() throws DatabaseException {

        if (!dataWarehouse.isEmpty()) dataWarehouse.clear();

        return dataWarehouse;
    }

}
