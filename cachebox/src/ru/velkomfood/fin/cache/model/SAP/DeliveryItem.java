package ru.velkomfood.fin.cache.model.SAP;

import java.math.BigDecimal;

/**
 * Created by dpetrov on 30.12.2016.
 */
public class DeliveryItem {

    private Long deliveryId;
    private Long posId;
    private Long materialId;
    private String materialName;
    private String uom;
    private BigDecimal quantity;
    private BigDecimal quantityKG;

    public Long getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(Long deliveryId) {
        this.deliveryId = deliveryId;
    }

    public Long getPosId() {
        return posId;
    }

    public void setPosId(Long posId) {
        this.posId = posId;
    }

    public Long getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Long materialId) {
        this.materialId = materialId;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getQuantityKG() {
        return quantityKG;
    }

    public void setQuantityKG(BigDecimal quantityKG) {
        this.quantityKG = quantityKG;
    }

}
