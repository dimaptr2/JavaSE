package ru.velkomfood.mm.mrp.uploader.data.repository.td;

import ru.velkomfood.mm.mrp.uploader.MrpUploaderComponent;
import ru.velkomfood.mm.mrp.uploader.data.model.td.Stock;
import ru.velkomfood.mm.mrp.uploader.data.model.td.StockKey;
import ru.velkomfood.mm.mrp.uploader.data.repository.AsyncDao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class StockDao implements AsyncDao<Stock, StockKey>, MrpUploaderComponent {

    private final Connection connection;
    private final ExecutorService executor;

    public StockDao(Connection connection, ExecutorService executor) {
        this.connection = connection;
        this.executor = executor;
    }

    @Override
    public Future<Boolean> exists(StockKey key) {
        return CompletableFuture.supplyAsync(() -> {
            try (var stmt = this.connection.prepareStatement(STOCK_QUERY.COUNT.label)) {
                stmt.setLong(1, key.getMaterialId());
                stmt.setString(2, key.getPlantId());
                stmt.setString(3, key.getStoreId());
                stmt.setInt(4, key.getFiscalYear());
                stmt.setInt(5, key.getPeriod());
                try (var rs = stmt.executeQuery()) {
                    var numRows = 0L;
                    if (rs.next()) {
                        numRows = rs.getLong("n_rows");
                    }
                    return (numRows > 0);
                }
            } catch (SQLException ex) {
                log.error(ex.getMessage());
                return false;
            }
        }, this.executor);
    }

    @Override
    public Future<Void> create(Stock stock) {
        return CompletableFuture.runAsync(() -> {
            try (var statement = this.connection.prepareStatement(STOCK_QUERY.CREATE.label)) {
                statement.setLong(1, stock.getMaterialId());
                statement.setString(2, stock.getPlantId());
                statement.setString(3, stock.getStoreId());
                statement.setInt(4, stock.getFiscalYear());
                statement.setInt(5, stock.getPeriod());
                statement.setString(6, stock.getUomId());
                statement.setBigDecimal(7, stock.getFree());
                statement.setBigDecimal(8, stock.getBlock());
                statement.setBigDecimal(9, stock.getQuality());
                statement.executeUpdate();
            } catch (SQLException ex) {
                log.error(ex.getMessage());
            }
        }, this.executor);
    }

    @Override
    public Future<Void> update(Stock stock) {
        return CompletableFuture.runAsync(() -> {
            try (var statement = this.connection.prepareStatement(STOCK_QUERY.UPDATE.label)) {
                statement.setString(1, stock.getUomId());
                statement.setBigDecimal(2, stock.getFree());
                statement.setBigDecimal(3, stock.getBlock());
                statement.setBigDecimal(4, stock.getQuality());
                statement.setLong(5, stock.getMaterialId());
                statement.setString(6, stock.getPlantId());
                statement.setString(7, stock.getStoreId());
                statement.setInt(8, stock.getFiscalYear());
                statement.setInt(9, stock.getPeriod());
                statement.executeUpdate();
            } catch (SQLException ex) {
                log.error(ex.getMessage());
            }
        }, this.executor);
    }

    // private section

    private enum STOCK_QUERY {

        COUNT("SELECT COUNT( * ) AS n_rows FROM stock " +
                "WHERE material_id = ? AND plant_id = ? AND store_id = ? " +
                "AND fiscal_year = ? AND period_id = ?"),
        CREATE("INSERT INTO stock VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"),
        UPDATE("UPDATE stock SET uom_id = ?, free = ?, block = ?, quality = ? " +
                "WHERE material_id = ? AND plant_id = ? AND store_id = ? " +
                "AND fiscal_year = ? AND period_id = ?");

        private final String label;
        STOCK_QUERY(String label) {
            this.label = label;
        }

    }

}
