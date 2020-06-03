package ru.velkomfood.mm.mrp.uploader.data.repository.md;

import ru.velkomfood.mm.mrp.uploader.MrpUploaderComponent;
import ru.velkomfood.mm.mrp.uploader.data.model.md.StorePlace;
import ru.velkomfood.mm.mrp.uploader.data.repository.AsyncDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class StorePlaceDao implements AsyncDao<StorePlace, String>, MrpUploaderComponent {

    private final Connection connection;
    private final ExecutorService executor;

    public StorePlaceDao(Connection connection, ExecutorService executor) {
        this.connection = connection;
        this.executor = executor;
    }

    @Override
    public Future<Boolean> exists(String key) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                PreparedStatement statement = this.connection.prepareStatement(SQL_COMMANDS.COUNT.label);
                statement.setString(1, key);
                ResultSet rs = statement.executeQuery();
                var idx = 0L;
                if (rs.next()) {
                    idx = rs.getLong("n_rows");
                }
                rs.close();
                statement.close();
                return (idx > 0);
            } catch (SQLException ex) {
                log.error(ex.getMessage());
                return false;
            }
        }, this.executor);
    }

    @Override
    public Future<Void> create(StorePlace storePlace) {
        return CompletableFuture.runAsync(() -> {
            try (PreparedStatement stmt = this.connection.prepareStatement(SQL_COMMANDS.CREATE.label)) {
                stmt.setString(1, storePlace.getId());
                stmt.setString(2, storePlace.getDescription());
                stmt.executeUpdate();
            } catch (SQLException sqlEx) {
                log.error(sqlEx.getMessage());
            }
        }, this.executor);
    }

    @Override
    public Future<Void> update(StorePlace storePlace) {
        return CompletableFuture.runAsync(() -> {
            try (PreparedStatement stmt = this.connection.prepareStatement(SQL_COMMANDS.UPDATE.label)) {
                stmt.setString(1, storePlace.getDescription());
                stmt.setString(2, storePlace.getId());
                stmt.executeUpdate();
            } catch (SQLException sqlEx) {
                log.error(sqlEx.getMessage());
            }
        }, this.executor);
    }

    // private section
    private enum SQL_COMMANDS {

        COUNT("SELECT COUNT( id ) AS n_rows FROM storage WHERE id = ?"),
        CREATE("INSERT INTO storage VALUES (?, ?)"),
        UPDATE("UPDATE storage SET name = ? WHERE id = ?");

        private final String label;
        SQL_COMMANDS(String label) {
            this.label = label;
        }

    }

}
