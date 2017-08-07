package ru.velkomfood.mysap.data.model.master;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

@DatabaseTable(tableName = "channels")
public class Channel implements Serializable {

    @DatabaseField(id = true, dataType = DataType.STRING, width = 2)
    private String id;
    @DatabaseField(columnName = "description", dataType = DataType.STRING, width = 50)
    private String description;

    public Channel() { }


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

        Channel channel = (Channel) o;

        return id.equals(channel.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

}
