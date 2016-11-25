package ru.velkomfood.mysap.mysql.bdb.engine;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import ru.velkomfood.mysap.mysql.bdb.entities.MrpReportEntity;

/**
 * Created by dpetrov on 10.08.16.
 */
public class ReportDA {

    PrimaryIndex<String, MrpReportEntity> pIdx;

    public ReportDA(EntityStore store) throws DatabaseException {
        pIdx = store.getPrimaryIndex(String.class, MrpReportEntity.class);
    }

}
