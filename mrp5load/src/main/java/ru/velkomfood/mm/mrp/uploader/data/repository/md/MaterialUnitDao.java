package ru.velkomfood.mm.mrp.uploader.data.repository.md;

import ru.velkomfood.mm.mrp.uploader.MrpUploaderComponent;
import ru.velkomfood.mm.mrp.uploader.data.model.md.MaterialUnit;
import ru.velkomfood.mm.mrp.uploader.data.model.md.MaterialUnitKey;
import ru.velkomfood.mm.mrp.uploader.data.repository.AsyncDao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class MaterialUnitDao implements AsyncDao<MaterialUnit, MaterialUnitKey>, MrpUploaderComponent {

    private final Connection connection;
    private final ExecutorService executor;

    public MaterialUnitDao(Connection connection, ExecutorService executor) {
        this.connection = connection;
        this.executor = executor;
    }

    @Override
    public Future<Boolean> exists(MaterialUnitKey key) {
        return CompletableFuture.supplyAsync(() -> {
            try (var stmt = this.connection.prepareStatement(UNIT_QUERY.COUNT.label)) {
                stmt.setLong(1, key.getId());
                stmt.setString(2, key.getPlant());
                try (var rs = stmt.executeQuery()) {
                    var counter = 0L;
                    if (rs.next()) {
                        counter = rs.getLong("n_rows");
                    }
                    return (counter > 0);
                }
            } catch (SQLException ex) {
                log.error(ex.getMessage());
                return false;
            }
        }, this.executor);
    }

    @Override
    public Future<Void> create(MaterialUnit materialUnit) {
        return CompletableFuture.runAsync(() -> {
            try (var stmt = this.connection.prepareStatement(UNIT_QUERY.CREATE.label)) {
                stmt.setLong(1, materialUnit.getId());
                stmt.setString(2, materialUnit.getPlant());
                stmt.setString(3, materialUnit.getUom());
                stmt.setBigDecimal(4, materialUnit.getRate());
                stmt.executeUpdate();
            } catch (SQLException sqlEx) {
                log.error(sqlEx.getMessage());
            }
        }, this.executor);
    }

    @Override
    public Future<Void> update(MaterialUnit materialUnit) {
        return CompletableFuture.runAsync(() -> {
            try (var stmt = this.connection.prepareStatement(UNIT_QUERY.UPDATE.label)) {
                stmt.setString(1, materialUnit.getUom());
                stmt.setBigDecimal(2, materialUnit.getRate());
                stmt.setLong(3, materialUnit.getId());
                stmt.setString(4, materialUnit.getPlant());
                stmt.executeUpdate();
            } catch (SQLException sqlEx) {
                log.error(sqlEx.getMessage());
            }
        }, this.executor);
    }

    // private section

    private enum UNIT_QUERY {

        COUNT("SELECT COUNT( * ) AS n_rows FROM matunit WHERE id = ? AND plant_id = ?"),
        CREATE("INSERT INTO matunit VALUES (?, ?, ?, ?)"),
        UPDATE("UPDATE matunit SET uom_id = ?, rate = ? WHERE id = ? AND plant_id = ?");

        private final String label;
        UNIT_QUERY(String label) {
            this.label = label;
        }

    }

}
