package ru.velkomfood.mm.mrp.uploader.data.model.md;

import ru.velkomfood.mm.mrp.uploader.data.InfoRecord;

import java.util.Objects;

public class MaterialUnitKey implements InfoRecord {

    private long id;
    private String plant;

    public MaterialUnitKey() {
    }

    public MaterialUnitKey(long id, String plant) {
        this.id = id;
        this.plant = plant;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MaterialUnitKey that = (MaterialUnitKey) o;
        return id == that.id &&
                plant.equals(that.plant);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, plant);
    }

    @Override
    public String toString() {
        return "MaterialUnitKey{" +
                "id=" + id +
                ", plant='" + plant + '\'' +
                '}';
    }

}
