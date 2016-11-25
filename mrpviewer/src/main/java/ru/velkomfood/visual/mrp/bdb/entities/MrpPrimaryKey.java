package ru.velkomfood.visual.mrp.bdb.entities;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

/**
 * Created by dpetrov on 10.08.16.
 */

@Entity
public class MrpPrimaryKey {

    @PrimaryKey
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
