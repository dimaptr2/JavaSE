package ru.velkomfood.mm.mrp.uploader.data.model.td;

import ru.velkomfood.mm.mrp.uploader.data.InfoRecord;

import java.math.BigDecimal;
import java.util.Objects;

public class Requirement implements InfoRecord {

    private long materialId;
    private String plantId;
    private String purchaseGroupId;
    private int fiscalYear;
    private int period;
    private String uomId;
    private BigDecimal quantity;

    public Requirement() {
    }

    public Requirement(long materialId, String plantId,
                       String purchaseGroupId, int fiscalYear, int period,
                       String uomId, BigDecimal quantity) {
        this.materialId = materialId;
        this.plantId = plantId;
        this.purchaseGroupId = purchaseGroupId;
        this.fiscalYear = fiscalYear;
        this.period = period;
        this.uomId = uomId;
        this.quantity = quantity;
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

    public String getPurchaseGroupId() {
        return purchaseGroupId;
    }

    public void setPurchaseGroupId(String purchaseGroupId) {
        this.purchaseGroupId = purchaseGroupId;
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

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Requirement that = (Requirement) o;
        return materialId == that.materialId &&
                fiscalYear == that.fiscalYear &&
                period == that.period &&
                plantId.equals(that.plantId) &&
                purchaseGroupId.equals(that.purchaseGroupId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(materialId, plantId, purchaseGroupId, fiscalYear, period);
    }

    @Override
    public String toString() {
        return "Requirement{" +
                "materialId=" + materialId +
                ", plantId='" + plantId + '\'' +
                ", purchaseGroupId='" + purchaseGroupId + '\'' +
                ", fiscalYear=" + fiscalYear +
                ", period=" + period +
                ", uomId='" + uomId + '\'' +
                ", quantity=" + quantity +
                '}';
    }

}
