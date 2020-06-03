package ru.velkomfood.mm.mrp.uploader.data.model.md;

import ru.velkomfood.mm.mrp.uploader.data.InfoRecord;

import java.util.Objects;

public class Measure implements InfoRecord {

    private String id;
    private String description;

    public Measure() {
    }

    public Measure(String id, String description) {
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
        Measure measure = (Measure) o;
        return id.equals(measure.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Measure{" +
                "id='" + id + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

}
