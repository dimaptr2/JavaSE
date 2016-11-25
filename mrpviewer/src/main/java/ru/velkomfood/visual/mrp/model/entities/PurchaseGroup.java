package ru.velkomfood.visual.mrp.model.entities;

/**
 * Created by dpetrov on 05.08.16.
 */
public class PurchaseGroup {

    private char checked;
    private String id;
    private String description;

    public char getChecked() {
        return checked;
    }

    public void setChecked(char checked) {
        this.checked = checked;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
