package ru.velkomfood.mm.mrp.uploader.data.model.td;

import ru.velkomfood.mm.mrp.uploader.data.InfoRecord;

import java.util.Objects;

public class RequirementKey implements InfoRecord {

    private long materialId;
    private String plantId;
    private String purchaseGroupId;
    private int fiscalYear;
    private int period;

    public RequirementKey() {
    }

    public RequirementKey(long materialId, String plantId,
                          String purchaseGroupId, int fiscalYear, int period) {
        this.materialId = materialId;
        this.plantId = plantId;
        this.purchaseGroupId = purchaseGroupId;
        this.fiscalYear = fiscalYear;
        this.period = period;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequirementKey that = (RequirementKey) o;
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
        return "RequirementKey{" +
                "materialId=" + materialId +
                ", plantId='" + plantId + '\'' +
                ", purchaseGroupId='" + purchaseGroupId + '\'' +
                ", fiscalYear=" + fiscalYear +
                ", period=" + period +
                '}';
    }

}
