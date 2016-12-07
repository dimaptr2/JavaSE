package ru.velkomfood.mysap.synchronizer.model;

/**
 * Created by dpetrov on 02.12.16.
 */
public class Material {

    private int matnr;
    private String description;

    public Material(int matnr, String description) {
        this.matnr = matnr;
        this.description = description;
    }

    public int getMatnr() {
        return matnr;
    }

    public String getDescription() {
        return description;
    }

}
