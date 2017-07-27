package ru.velkomfood.fin.cash.logon.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class FiscalHead {

    private long id;
    private String companyId;
    private LocalDate postingDate;
    private BigDecimal amount;

    public FiscalHead() { }

    public FiscalHead(long id, String companyId,
                      LocalDate postingDate,
                      BigDecimal amount) {
        this.id = id;
        this.companyId = companyId;
        this.postingDate = postingDate;
        this.amount = amount;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public LocalDate getPostingDate() {
        return postingDate;
    }

    public void setPostingDate(LocalDate postingDate) {
        this.postingDate = postingDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FiscalHead that = (FiscalHead) o;

        if (id != that.id) return false;
        return companyId.equals(that.companyId);
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + companyId.hashCode();
        return result;
    }

}
