package ru.velkomfood.mysap.data.model.master;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

@DatabaseTable(tableName = "vendors")
public class Vendor implements Serializable {

    @DatabaseField(id = true, dataType = DataType.STRING, width = 10)
    private String id;
    @DatabaseField(dataType = DataType.STRING, width = 50)
    private String name;
    @DatabaseField(dataType = DataType.STRING, width = 2)
    private String country;
    @DatabaseField(dataType = DataType.STRING, width = 15)
    private String postcode;
    @DatabaseField(dataType = DataType.STRING, width = 50)
    private String city;
    @DatabaseField(dataType = DataType.STRING, width = 50)
    private String street;
    @DatabaseField(dataType = DataType.STRING, width = 15)
    private String building;

    public Vendor() { }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vendor vendor = (Vendor) o;

        return id.equals(vendor.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

}
