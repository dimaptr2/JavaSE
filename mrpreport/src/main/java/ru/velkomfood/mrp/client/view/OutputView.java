package ru.velkomfood.mrp.client.view;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Created by DPetrov on 17.08.2016.
 */
public class OutputView {

    private SimpleStringProperty material;
    private SimpleStringProperty materialDescription;
    private SimpleIntegerProperty werks, year;
    private SimpleStringProperty purchaseGroup;
    private SimpleStringProperty nameOfPurchaseGroup;
    private SimpleStringProperty baseUnit;

    private SimpleStringProperty stocks;

    private SimpleStringProperty req01;
    private SimpleStringProperty req02;
    private SimpleStringProperty req03;
    private SimpleStringProperty req04;
    private SimpleStringProperty req05;
    private SimpleStringProperty req06;
    private SimpleStringProperty req07;
    private SimpleStringProperty req08;
    private SimpleStringProperty req09;
    private SimpleStringProperty req10;
    private SimpleStringProperty req11;
    private SimpleStringProperty req12;

    public OutputView() {
        material = new SimpleStringProperty();
        materialDescription = new SimpleStringProperty();
        werks = new SimpleIntegerProperty();
        year = new SimpleIntegerProperty();
        purchaseGroup = new SimpleStringProperty();
        nameOfPurchaseGroup = new SimpleStringProperty();
        baseUnit = new SimpleStringProperty();
        stocks = new SimpleStringProperty();
        req01 = new SimpleStringProperty();
        req02 = new SimpleStringProperty();
        req03 = new SimpleStringProperty();
        req04 = new SimpleStringProperty();
        req05 = new SimpleStringProperty();
        req06 = new SimpleStringProperty();
        req07 = new SimpleStringProperty();
        req08 = new SimpleStringProperty();
        req09 = new SimpleStringProperty();
        req10 = new SimpleStringProperty();
        req11 = new SimpleStringProperty();
        req12 = new SimpleStringProperty();
    }

    public String getMaterial() {
        return material.get();
    }

    public SimpleStringProperty materialProperty() {
        return material;
    }

    public void setMaterial(String material) {
        this.material.set(material);
    }

    public String getMaterialDescription() {
        return materialDescription.get();
    }

    public SimpleStringProperty materialDescriptionProperty() {
        return materialDescription;
    }

    public void setMaterialDescription(String materialDescription) {
        this.materialDescription.set(materialDescription);
    }

    public int getWerks() {
        return werks.get();
    }

    public SimpleIntegerProperty werksProperty() {
        return werks;
    }

    public void setWerks(int werks) {
        this.werks.set(werks);
    }

    public int getYear() {
        return year.get();
    }

    public SimpleIntegerProperty yearProperty() {
        return year;
    }

    public void setYear(int year) {
        this.year.set(year);
    }

    public String getPurchaseGroup() {
        return purchaseGroup.get();
    }

    public SimpleStringProperty purchaseGroupProperty() {
        return purchaseGroup;
    }

    public void setPurchaseGroup(String purchaseGroup) {
        this.purchaseGroup.set(purchaseGroup);
    }

    public String getNameOfPurchaseGroup() {
        return nameOfPurchaseGroup.get();
    }

    public SimpleStringProperty nameOfPurchaseGroupProperty() {
        return nameOfPurchaseGroup;
    }

    public void setNameOfPurchaseGroup(String nameOfPurchaseGroup) {
        this.nameOfPurchaseGroup.set(nameOfPurchaseGroup);
    }

    public String getBaseUnit() {
        return baseUnit.get();
    }

    public SimpleStringProperty baseUnitProperty() {
        return baseUnit;
    }

    public void setBaseUnit(String baseUnit) {
        this.baseUnit.set(baseUnit);
    }

    public String getStocks() {
        return stocks.get();
    }

    public SimpleStringProperty stocksProperty() {
        return stocks;
    }

    public void setStocks(String stocks) {
        this.stocks.set(stocks);
    }

    public String getReq01() {
        return req01.get();
    }

    public SimpleStringProperty req01Property() {
        return req01;
    }

    public void setReq01(String req01) {
        this.req01.set(req01);
    }

    public String getReq02() {
        return req02.get();
    }

    public SimpleStringProperty req02Property() {
        return req02;
    }

    public void setReq02(String req02) {
        this.req02.set(req02);
    }

    public String getReq03() {
        return req03.get();
    }

    public SimpleStringProperty req03Property() {
        return req03;
    }

    public void setReq03(String req03) {
        this.req03.set(req03);
    }

    public String getReq04() {
        return req04.get();
    }

    public SimpleStringProperty req04Property() {
        return req04;
    }

    public void setReq04(String req04) {
        this.req04.set(req04);
    }

    public String getReq05() {
        return req05.get();
    }

    public SimpleStringProperty req05Property() {
        return req05;
    }

    public void setReq05(String req05) {
        this.req05.set(req05);
    }

    public String getReq06() {
        return req06.get();
    }

    public SimpleStringProperty req06Property() {
        return req06;
    }

    public void setReq06(String req06) {
        this.req06.set(req06);
    }

    public String getReq07() {
        return req07.get();
    }

    public SimpleStringProperty req07Property() {
        return req07;
    }

    public void setReq07(String req07) {
        this.req07.set(req07);
    }

    public String getReq08() {
        return req08.get();
    }

    public SimpleStringProperty req08Property() {
        return req08;
    }

    public void setReq08(String req08) {
        this.req08.set(req08);
    }

    public String getReq09() {
        return req09.get();
    }

    public SimpleStringProperty req09Property() {
        return req09;
    }

    public void setReq09(String req09) {
        this.req09.set(req09);
    }

    public String getReq10() {
        return req10.get();
    }

    public SimpleStringProperty req10Property() {
        return req10;
    }

    public void setReq10(String req10) {
        this.req10.set(req10);
    }

    public String getReq11() {
        return req11.get();
    }

    public SimpleStringProperty req11Property() {
        return req11;
    }

    public void setReq11(String req11) {
        this.req11.set(req11);
    }

    public String getReq12() {
        return req12.get();
    }

    public SimpleStringProperty req12Property() {
        return req12;
    }

    public void setReq12(String req12) {
        this.req12.set(req12);
    }

}
