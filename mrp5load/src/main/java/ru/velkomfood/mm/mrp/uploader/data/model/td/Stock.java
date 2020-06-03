package ru.velkomfood.mm.mrp.uploader.data.model.td;

import ru.velkomfood.mm.mrp.uploader.data.InfoRecord;

import java.math.BigDecimal;
import java.util.Objects;

public class Stock implements InfoRecord {

    private long materialId;
    private String plantId;
    private String storeId;
    private int fiscalYear;
    private int period;
    private String uomId;
    private BigDecimal free;
    private BigDecimal block;
    private BigDecimal quality;

    public Stock() {
    }

    public Stock(long materialId, String plantId,
                 String storeId, int fiscalYear, int period,
                 String uomId, BigDecimal free, BigDecimal block, BigDecimal quality) {
        this.materialId = materialId;
        this.plantId = plantId;
        this.storeId = storeId;
        this.fiscalYear = fiscalYear;
        this.period = period;
        this.uomId = uomId;
        this.free = free;
        this.block = block;
        this.quality = quality;
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

    public String getUomId() {
        return uomId;
    }

    public void setUomId(String uomId) {
        this.uomId = uomId;
    }

    public BigDecimal getFree() {
        return free;
    }

    public void setFree(BigDecimal free) {
        this.free = free;
    }

    public BigDecimal getBlock() {
        return block;
    }

    public void setBlock(BigDecimal block) {
        this.block = block;
    }

    public BigDecimal getQuality() {
        return quality;
    }

    public void setQuality(BigDecimal quality) {
        this.quality = quality;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stock stock = (Stock) o;
        return materialId == stock.materialId &&
                fiscalYear == stock.fiscalYear &&
                period == stock.period &&
                plantId.equals(stock.plantId) &&
                storeId.equals(stock.storeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(materialId, plantId, storeId, fiscalYear, period);
    }

    @Override
    public String toString() {
        return "Stock{" +
                "materialId=" + materialId +
                ", plantId='" + plantId + '\'' +
                ", storeId='" + storeId + '\'' +
                ", fiscalYear=" + fiscalYear +
                ", period=" + period +
                ", uomId='" + uomId + '\'' +
                ", free=" + free +
                ", block=" + block +
                ", quality=" + quality +
                '}';
    }

}
