package ru.velkomfood.mm.mrp.uploader.data.model.td;

import ru.velkomfood.mm.mrp.uploader.data.InfoRecord;

import java.util.Objects;

public class StockKey implements InfoRecord {

    private long materialId;
    private String plantId;
    private String storeId;
    private int fiscalYear;
    private int period;

    public StockKey() {
    }

    public long getMaterialId() {
        return materialId;
    }

    public void setMaterialId(long materialId) {
        this.materialId = materialId;
    }

    public String getPlantId() {
        return plantId;
    }

    public void setPlantId(String plantId) {
        this.plantId = plantId;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public int getFiscalYear() {
        return fiscalYear;
    }

    public void setFiscalYear(int fiscalYear) {
        this.fiscalYear = fiscalYear;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StockKey stockKey = (StockKey) o;
        return materialId == stockKey.materialId &&
                fiscalYear == stockKey.fiscalYear &&
                period == stockKey.period &&
                plantId.equals(stockKey.plantId) &&
                storeId.equals(stockKey.storeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(materialId, plantId, storeId, fiscalYear, period);
    }

    @Override
    public String toString() {
        return "StockKey{" +
                "materialId=" + materialId +
                ", plantId='" + plantId + '\'' +
                ", storeId='" + storeId + '\'' +
                ", fiscalYear=" + fiscalYear +
                ", period=" + period +
                '}';
    }

}
