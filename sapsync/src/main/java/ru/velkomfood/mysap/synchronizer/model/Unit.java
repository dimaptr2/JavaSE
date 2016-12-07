package ru.velkomfood.mysap.synchronizer.model;

/**
 * Created by dpetrov on 06.12.16.
 */
public class Unit {

    private String uomSAP;
    private String uomISO;
    private String description;

    public String getUomSAP() {
        return uomSAP;
    }

    public void setUomSAP(String uomSAP) {
        this.uomSAP = uomSAP;
    }

    public String getUomISO() {
        return uomISO;
    }

    public void setUomISO(String uomISO) {
        this.uomISO = uomISO;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
