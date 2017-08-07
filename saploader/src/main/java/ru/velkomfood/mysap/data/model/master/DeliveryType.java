package ru.velkomfood.mysap.data.model.master;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

@DatabaseTable(tableName = "delivery_type")
public class DeliveryType implements Serializable {

    @DatabaseField(id = true, dataType = DataType.INTEGER)
    private int id;
    @DatabaseField(dataType = DataType.STRING, width = 25)
    private String description;

    public DeliveryType() { }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

        DeliveryType that = (DeliveryType) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

}
