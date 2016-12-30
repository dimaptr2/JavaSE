package ru.velkomfood.fin.cache.model;

import java.sql.Date;

/**
 * Created by dpetrov on 30.12.2016.
 */
public class DeliveryHead {

    private int id;
    private String user;
    private java.sql.Date deliveryDate;
    private String customer;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

}
