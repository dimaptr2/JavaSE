package ru.velkomfood.fin.cache.model.SAP;

import java.math.BigDecimal;

/**
 * Created by dpetrov on 29.12.2016.
 */
public class Material {

    private Long id;
    private String description;
    private String baseUom;
    private BigDecimal netBaseWeight; // net weight in the base unit of measurement

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBaseUom() {
        return baseUom;
    }

    public void setBaseUom(String baseUom) {
        this.baseUom = baseUom;
    }

    public BigDecimal getNetBaseWeight() {
        return netBaseWeight;
    }

    public void setNetBaseWeight(BigDecimal netBaseWeight) {
        this.netBaseWeight = netBaseWeight;
    }

}
