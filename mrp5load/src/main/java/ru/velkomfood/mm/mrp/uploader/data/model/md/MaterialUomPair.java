package ru.velkomfood.mm.mrp.uploader.data.model.md;

import ru.velkomfood.mm.mrp.uploader.data.InfoRecord;

import java.util.Objects;

public class MaterialUomPair implements InfoRecord {

    private long id;
    private String uom;

    public MaterialUomPair() {
    }

    public MaterialUomPair(long id, String uom) {
        this.id = id;
        this.uom = uom;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MaterialUomPair that = (MaterialUomPair) o;
        return id == that.id &&
                uom.equals(that.uom);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, uom);
    }

    @Override
    public String toString() {
        return "MaterialUomPair{" +
                "id=" + id +
                ", uom='" + uom + '\'' +
                '}';
    }

}
