package ru.velkomfood.mm.mrp.uploader.data.repository.md;

import ru.velkomfood.mm.mrp.uploader.MrpUploaderComponent;
import ru.velkomfood.mm.mrp.uploader.data.model.md.Measure;
import ru.velkomfood.mm.mrp.uploader.data.repository.AsyncDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class MeasureDao implements AsyncDao<Measure, String>, MrpUploaderComponent {

    private final Connection connection;
    private final ExecutorService executor;

    public MeasureDao(Connection connection, ExecutorService executor) {
        this.connection = connection;
        this.executor = executor;
    }

    @Override
    public Future<Boolean> exists(String key) {
        return CompletableFuture.supplyAsync(() -> {
            try (PreparedStatement statement = this.connection.prepareStatement(SQL_QUERY.COUNT.label)) {
                statement.setString(1, key);
                try (ResultSet rs = statement.executeQuery()) {
                    var count = 0L;
                    if (rs.next()) {
                        count = rs.getLong("n_rows");
                    }
                    return (count > 0);
                }
            } catch (SQLException ex) {
                log.error(ex.getSQLState());
                return false;
            }
        }, this.executor);
    }

    @Override
    public Future<Void> create(Measure measure) {
        return CompletableFuture.runAsync(() -> {
            try (PreparedStatement statement = this.connection.prepareStatement(SQL_QUERY.CREATE.label)) {
                statement.setString(1, measure.getId());
                statement.setString(2, measure.getDescription());
                statement.executeUpdate();
            } catch (SQLException ex) {
                log.error(ex.getSQLState());
            }
        }, this.executor);
    }

    @Override
    public Future<Void> update(Measure measure) {
        return CompletableFuture.runAsync(() -> {
            try (PreparedStatement statement = this.connection.prepareStatement(SQL_QUERY.UPDATE.label)) {
                statement.setString(1, measure.getDescription());
                statement.setString(2, measure.getId());
                statement.executeUpdate();
            } catch (SQLException ex) {
                log.error(ex.getSQLState());
            }
        }, this.executor);
    }

    // private section
    private enum SQL_QUERY {

        COUNT("SELECT COUNT( id ) AS n_rows FROM measure WHERE id = ?"),
        CREATE("INSERT INTO measure VALUES (?, ?)"),
        UPDATE("UPDATE measure SET name = ? WHERE id = ?");

        private final String label;
        SQL_QUERY(String label) {
            this.label = label;
        }

    }

}
