package ru.velkomfood.dms.info.model;

import java.sql.Date;

public class Status {

    private long id;
    private String vendor;
    private String vendorName;
    private String contract;

    private java.sql.Date term1;
    private java.sql.Date term2;
    private java.sql.Date term3;
    private java.sql.Date term4;
    private java.sql.Date term5;

    private String status1;
    private String status2;
    private String status3;
    private String status4;
    private String status5;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getContract() {
        return contract;
    }

    public void setContract(String contract) {
        this.contract = contract;
    }

    public Date getTerm1() {
        return term1;
    }

    public void setTerm1(Date term1) {
        this.term1 = term1;
    }

    public Date getTerm2() {
        return term2;
    }

    public void setTerm2(Date term2) {
        this.term2 = term2;
    }

    public Date getTerm3() {
        return term3;
    }

    public void setTerm3(Date term3) {
        this.term3 = term3;
    }

    public Date getTerm4() {
        return term4;
    }

    public void setTerm4(Date term4) {
        this.term4 = term4;
    }

    public Date getTerm5() {
        return term5;
    }

    public void setTerm5(Date term5) {
        this.term5 = term5;
    }

    public String getStatus1() {
        return status1;
    }

    public void setStatus1(String status1) {
        this.status1 = status1;
    }

    public String getStatus2() {
        return status2;
    }

    public void setStatus2(String status2) {
        this.status2 = status2;
    }

    public String getStatus3() {
        return status3;
    }

    public void setStatus3(String status3) {
        this.status3 = status3;
    }

    public String getStatus4() {
        return status4;
    }

    public void setStatus4(String status4) {
        this.status4 = status4;
    }

    public String getStatus5() {
        return status5;
    }

    public void setStatus5(String status5) {
        this.status5 = status5;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Status status = (Status) o;

        return id == status.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

}
