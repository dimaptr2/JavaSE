package ru.velkomfood.mysap.mysql.model;

import java.math.BigDecimal;

/**
 * Created by dpetrov on 12.07.16.
 */
public class MaterialPriceEntity {

    private String matnr;
    private int werks;
    private int valuation_area;
    private String pur_group;
    private char price_control;
    private BigDecimal moving_price;
    private BigDecimal standard_price;
    private String currency;
    private String base_unit;

    public String getMatnr() {
        return matnr;
    }

    public void setMatnr(String matnr) {
        this.matnr = matnr;
    }

    public int getWerks() {
        return werks;
    }

    public void setWerks(int werks) {
        this.werks = werks;
    }

    public int getValuation_area() {
        return valuation_area;
    }

    public void setValuation_area(int valuation_area) {
        this.valuation_area = valuation_area;
    }

    public String getPur_group() {
        return pur_group;
    }

    public void setPur_group(String pur_group) {
        this.pur_group = pur_group;
    }

    public char getPrice_control() {
        return price_control;
    }

    public void setPrice_control(char price_control) {
        this.price_control = price_control;
    }

    public BigDecimal getMoving_price() {
        return moving_price;
    }

    public void setMoving_price(BigDecimal moving_price) {
        this.moving_price = moving_price;
    }

    public BigDecimal getStandard_price() {
        return standard_price;
    }

    public void setStandard_price(BigDecimal standard_price) {
        this.standard_price = standard_price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getBase_unit() {
        return base_unit;
    }

    public void setBase_unit(String base_unit) {
        this.base_unit = base_unit;
    }

}
