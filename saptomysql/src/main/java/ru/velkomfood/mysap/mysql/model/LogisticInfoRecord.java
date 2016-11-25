package ru.velkomfood.mysap.mysql.model;

import java.util.Date;

/**
 * Created by dpetrov on 22.07.16.
 */

public class LogisticInfoRecord {

    private String infoRecordNumber;
    private String material;
    private String vendor;
    private String dateCreation;
    private int month;
    private int year;

    public String getInfoRecordNumber() {
        return infoRecordNumber;
    }

    public void setInfoRecordNumber(String infoRecordNumber) {
        this.infoRecordNumber = infoRecordNumber;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(String dateCreation) {
        this.dateCreation = dateCreation;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

}
