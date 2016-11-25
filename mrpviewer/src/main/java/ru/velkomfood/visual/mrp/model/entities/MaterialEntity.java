package ru.velkomfood.visual.mrp.model.entities;

import java.io.Serializable;

/**
 * Created by dpetrov on 05.08.16.
 */
public class MaterialEntity implements Serializable {

    private String matnr;
    private String maktx;

    public String getMatnr() {
        return matnr;
    }

    public void setMatnr(String matnr) {
        this.matnr = matnr;
    }

    public String getMaktx() {
        return maktx;
    }

    public void setMaktx(String maktx) {
        this.maktx = maktx;
    }

}
