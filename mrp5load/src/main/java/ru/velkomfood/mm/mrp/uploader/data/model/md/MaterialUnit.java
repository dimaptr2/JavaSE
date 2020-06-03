package ru.velkomfood.mm.mrp.uploader.data.model.md;

import ru.velkomfood.mm.mrp.uploader.data.InfoRecord;

import java.math.BigDecimal;
import java.util.Objects;

public class MaterialUnit implements InfoRecord {

    private long id;
    private String plant;
    private String uom;
    private BigDecimal rate;

    public MaterialUnit() {
    }

    public MaterialUnit(long id, String plant, String uom, BigDecimal rate) {
        this.id = id;
        this.plant = plant;
        this.uom = uom;
        this.rate = rate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPlant() {
        return plant;
    }

    public void setPlant(String plant) {
        this.plant = plant;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MaterialUnit that = (MaterialUnit) o;
        return id == that.id &&
                plant.equals(that.plant);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, plant);
    }

    @Override
    public String toString() {
        return "MaterialUnit{" +
                "id=" + id +
                ", plant='" + plant + '\'' +
                ", uom='" + uom + '\'' +
                ", rate=" + rate +
                '}';
    }

}
