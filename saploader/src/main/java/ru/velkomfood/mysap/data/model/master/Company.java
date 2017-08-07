package ru.velkomfood.mysap.data.model.master;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.io.Serializable;

@DatabaseTable(tableName = "companies")
public class Company implements Serializable {

    @DatabaseField(id = true, dataType = DataType.STRING, width = 4)
    private String id;
    @DatabaseField(dataType = DataType.STRING, width = 50)
    private String name1;
    @DatabaseField(dataType = DataType.STRING, width = 50)
    private String name2;
    @DatabaseField(dataType = DataType.STRING, width = 2)
    private String country;
    @DatabaseField(dataType = DataType.STRING, width = 15)
    private String postcode;
    @DatabaseField(dataType = DataType.STRING, width = 50)
    private String city;
    @DatabaseField(dataType = DataType.STRING, width = 50)
    private String street;
    @DatabaseField(dataType = DataType.STRING, width = 50)
    private String building;
    @DatabaseField(dataType = DataType.STRING, width = 30)
    private String phone;
    @DatabaseField(dataType = DataType.STRING, width = 30)
    private String fax;

    public Company() { }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName1() {
        return name1;
    }

    public void setName1(String name1) {
        this.name1 = name1;
    }

    public String getName2() {
        return name2;
    }

    public void setName2(String name2) {
        this.name2 = name2;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Company company = (Company) o;

        return id.equals(company.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

}
