package ru.velkomfood.mysap.mysql.model;

/**
 * Created by dpetrov on 12.07.16.
 */
public class MaterialEntity {

    private String matnr;
    private String description;

    public String getMatnr() {
        return matnr;
    }

    public void setMatnr(String matnr) {
        this.matnr = matnr;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
