package ru.velkomfood.mm.mrp.uploader.data.repository.md;

import ru.velkomfood.mm.mrp.uploader.MrpUploaderComponent;
import ru.velkomfood.mm.mrp.uploader.data.model.md.Material;
import ru.velkomfood.mm.mrp.uploader.data.repository.AsyncDao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class MaterialDao implements AsyncDao<Material, Long>, MrpUploaderComponent {

    private final Connection connection;
    private final ExecutorService executor;

    public MaterialDao(Connection connection, ExecutorService executor) {
        this.connection = connection;
        this.executor = executor;
    }

    @Override
    public Future<Boolean> exists(Long key) {
        return CompletableFuture.supplyAsync(() -> {
            try (var stmt = this.connection.prepareStatement(MATERIAL_QUERY.COUNT.label)) {
                stmt.setLong(1, key);
                try (var resultSet = stmt.executeQuery()) {
                    var numberRows = 0L;
                    if (resultSet.next()) {
                        numberRows = resultSet.getLong("n_rows");
                    }
                    return (numberRows > 0);
                }
            } catch (SQLException ex) {
                log.error(ex.getMessage());
                return false;
            }
        }, this.executor);
    }

    @Override
    public Future<Void> create(Material material) {
        return CompletableFuture.runAsync(() -> {
            try (var stmt = this.connection.prepareStatement(MATERIAL_QUERY.CREATE.label)) {
                stmt.setLong(1, material.getId());
                stmt.setString(2, material.getDescription());
                stmt.executeUpdate();
            } catch (SQLException ex) {
                log.error(ex.getMessage());
            }
        }, this.executor);
    }

    @Override
    public Future<Void> update(Material material) {
        return CompletableFuture.runAsync(() -> {
            try (var stmt = this.connection.prepareStatement(MATERIAL_QUERY.UPDATE.label)) {
                stmt.setString(1, material.getDescription());
                stmt.setLong(2, material.getId());
                stmt.executeUpdate();
            } catch (SQLException ex) {
                log.error(ex.getMessage());
            }
        }, this.executor);
    }

    // private section

    private enum MATERIAL_QUERY {

        COUNT("SELECT COUNT( id ) AS n_rows FROM material WHERE id = ?"),
        CREATE("INSERT INTO material VALUES (?, ?)"),
        UPDATE("UPDATE material SET description = ? WHERE id = ?");

        private final String label;
        MATERIAL_QUERY(String label) {
            this.label = label;
        }

    }
}
