package ru.velkomfood.mm.mrp.uploader.output;

import org.mariadb.jdbc.MariaDbDataSource;
import ru.velkomfood.mm.mrp.uploader.data.EventBus;
import ru.velkomfood.mm.mrp.uploader.data.model.md.*;
import ru.velkomfood.mm.mrp.uploader.data.model.td.Requirement;
import ru.velkomfood.mm.mrp.uploader.data.model.td.RequirementKey;
import ru.velkomfood.mm.mrp.uploader.data.model.td.Stock;
import ru.velkomfood.mm.mrp.uploader.data.model.td.StockKey;
import ru.velkomfood.mm.mrp.uploader.data.repository.AsyncDao;
import ru.velkomfood.mm.mrp.uploader.data.repository.md.*;
import ru.velkomfood.mm.mrp.uploader.data.repository.td.RequirementDao;
import ru.velkomfood.mm.mrp.uploader.data.repository.td.StockDao;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

public class DataWriterImpl implements DataWriter {

    private final EventBus eventBus;
    private final Properties parameters;
    private final ExecutorService executor;
    private final DataSource dataSource;

    public DataWriterImpl(EventBus eventBus, Properties parameters, ExecutorService executor) {
        this.eventBus = eventBus;
        this.parameters = parameters;
        this.executor = executor;
        MariaDbDataSource mds = new MariaDbDataSource();
        try {
            mds.setServerName(this.parameters.getProperty("db.host"));
            mds.setPort(Integer.parseInt(this.parameters.getProperty("db.port")));
            mds.setUser(this.parameters.getProperty("db.user"));
            mds.setPassword(this.parameters.getProperty("db.password"));
            mds.setDatabaseName((this.parameters.getProperty("db.name")));
        } catch (SQLException ex) {
            log.error(ex.getMessage());
        }
        this.dataSource = mds;
    }

    @Override
    public void save(String dataAddress) throws SQLException {
        try (Connection connection = this.dataSource.getConnection()) {
            switch (dataAddress) {
                case "uom.queue":
                    AsyncDao<Measure, String> uomDao = new MeasureDao(connection, this.executor);
                    while (this.eventBus.queueSize(dataAddress) > 0) {
                        Measure uom = (Measure) this.eventBus.pull(dataAddress);
                        this.storeEntity(uom, uom.getId(), uomDao);
                    }
                    break;
                case "purchase.group.queue":
                    AsyncDao<PurchaseGroup, String> pgDao = new PurchaseGroupDao(connection, this.executor);
                    while (this.eventBus.queueSize(dataAddress) > 0) {
                        PurchaseGroup pg = (PurchaseGroup) this.eventBus.pull(dataAddress);
                        this.storeEntity(pg, pg.getId(), pgDao);
                    }
                    break;
                case "store.place.queue":
                    AsyncDao<StorePlace, String> storeDao = new StorePlaceDao(connection, this.executor);
                    while (this.eventBus.queueSize(dataAddress) > 0) {
                        StorePlace stp = (StorePlace) this.eventBus.pull(dataAddress);
                        this.storeEntity(stp, stp.getId(), storeDao);
                    }
                    break;
                case "material.queue":
                    AsyncDao<Material, Long> matDao = new MaterialDao(connection, this.executor);
                    while (this.eventBus.queueSize(dataAddress) > 0) {
                        Material material = (Material) this.eventBus.pull(dataAddress);
                        this.storeEntity(material, material.getId(), matDao);
                    }
                    AsyncDao<MaterialUnit, MaterialUnitKey> muDao =
                            new MaterialUnitDao(connection, this.executor);
                    while (this.eventBus.queueSize("material.unit.queue") > 0) {
                        MaterialUnit mu = (MaterialUnit) this.eventBus.pull("material.unit.queue");
                        MaterialUnitKey muKey = new MaterialUnitKey(mu.getId(), mu.getPlant());
                        this.storeEntity(mu, muKey, muDao);
                    }
                    break;
                case "stock.queue":
                    AsyncDao<Stock, StockKey> stDao = new StockDao(connection, this.executor);
                    while (this.eventBus.queueSize(dataAddress) > 0) {
                        Stock stock = (Stock) this.eventBus.pull(dataAddress);
                        StockKey stockKey = new StockKey();
                        stockKey.setMaterialId(stock.getMaterialId());
                        stockKey.setPlantId(stock.getPlantId());
                        stockKey.setStoreId(stock.getStoreId());
                        stockKey.setFiscalYear(stock.getFiscalYear());
                        stockKey.setPeriod(stock.getPeriod());
                        this.storeEntity(stock, stockKey, stDao);
                    }
                    break;
                case "requirement.queue":
                    AsyncDao<Requirement, RequirementKey> reqDao = new RequirementDao(connection, this.executor);
                    while (this.eventBus.queueSize(dataAddress) > 0) {
                        Requirement req = (Requirement) this.eventBus.pull(dataAddress);
                        RequirementKey reqKey = new RequirementKey();
                        reqKey.setMaterialId(req.getMaterialId());
                        reqKey.setPlantId(req.getPlantId());
                        reqKey.setPurchaseGroupId(req.getPurchaseGroupId());
                        reqKey.setFiscalYear(req.getFiscalYear());
                        reqKey.setPeriod(req.getPeriod());
                        this.storeEntity(req, reqKey, reqDao);
                    }
                    break;
            }
        }
    }

    // private section

    private <Entity, Key> void storeEntity(Entity entity, Key key, AsyncDao<Entity, Key> dao) {
        if (entity != null) {
            try {
                if (dao.exists(key).get()) {
                    dao.update(entity).get();
                } else {
                    dao.create(entity).get();
                }
            } catch (InterruptedException | ExecutionException ex) {
                log.error(ex.getMessage());
            }
        }
    }

}
