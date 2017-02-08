package ru.velkomfood.fin.cache.model.DB;

/**
 * Created by dpetrov on 12.01.2017.
 */
public class CreditSlipStatus {

    private Long id; // number of credit slip
    private String status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
