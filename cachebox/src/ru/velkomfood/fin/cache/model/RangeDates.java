package ru.velkomfood.fin.cache.model;

/**
 * Created by dpetrov on 29.12.2016.
 */
public class RangeDates {

    private String fromDate;
    private String toDate;

    public RangeDates(String fromDate, String toDate) {
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public String getFromDate() {
        return fromDate;
    }

    public String getToDate() {
        return toDate;
    }

}
