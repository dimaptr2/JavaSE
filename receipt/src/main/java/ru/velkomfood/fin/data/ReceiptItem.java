package ru.velkomfood.fin.data;

import java.math.BigDecimal;

/**
 * Created by dpetrov on 29.03.2017.
 */
public class ReceiptItem {

    private long receiptId;
    private long position;
    private String material;
    private String materialName;
    private BigDecimal quantity;
    private BigDecimal amount;

    public long getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(long receiptId) {
        this.receiptId = receiptId;
    }

    public long getPosition() {
        return position;
    }

    public void setPosition(long position) {
        this.position = position;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    // Special methods for printing purpose
    public long getLongQuantity() {
        BigDecimal thousand = new BigDecimal(1000.00);
        long value = (quantity.multiply(thousand)).longValue();
        return value;
    }

    public long getLongAmount() {
        BigDecimal hundred = new BigDecimal(100.00);
        long value = (amount.multiply(hundred)).longValue();
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReceiptItem that = (ReceiptItem) o;

        if (receiptId != that.receiptId) return false;
        if (position != that.position) return false;
        if (!material.equals(that.material)) return false;
        if (materialName != null ? !materialName.equals(that.materialName) : that.materialName != null) return false;
        if (quantity != null ? !quantity.equals(that.quantity) : that.quantity != null) return false;
        return amount != null ? amount.equals(that.amount) : that.amount == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (receiptId ^ (receiptId >>> 32));
        result = 31 * result + (int) (position ^ (position >>> 32));
        result = 31 * result + material.hashCode();
        result = 31 * result + (materialName != null ? materialName.hashCode() : 0);
        result = 31 * result + (quantity != null ? quantity.hashCode() : 0);
        result = 31 * result + (amount != null ? amount.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder(0);
        sb.append("ReceiptItem{").append("receiptId=").append(receiptId).append("\r\n");
        sb.append(", position=").append(position).append(", material=").append(material).append("\r\n");
        sb.append(", materialName=").append(materialName).append(", quantity=").append(quantity).append("\r\n");
        sb.append(", amount=").append(amount).append("}");
        return sb.toString();

    }

}
