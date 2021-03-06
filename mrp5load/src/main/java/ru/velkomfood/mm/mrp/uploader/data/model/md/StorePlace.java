package ru.velkomfood.mm.mrp.uploader.data.model.md;

import ru.velkomfood.mm.mrp.uploader.data.InfoRecord;

import java.util.Objects;

public class StorePlace implements InfoRecord {

    private String id;
    private String description;

    public StorePlace() {
    }

    public StorePlace(String id, String description) {
        this.id = id;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StorePlace that = (StorePlace) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "StorePlace{" +
                "id='" + id + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

}
