package ru.velkomfood.mm.mrp.uploader.data.repository.md;

import ru.velkomfood.mm.mrp.uploader.MrpUploaderComponent;
import ru.velkomfood.mm.mrp.uploader.data.model.md.PurchaseGroup;
import ru.velkomfood.mm.mrp.uploader.data.repository.AsyncDao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class PurchaseGroupDao implements AsyncDao<PurchaseGroup, String>, MrpUploaderComponent {

    private final Connection connection;
    private final ExecutorService executor;

    public PurchaseGroupDao(Connection connection, ExecutorService executor) {
        this.connection = connection;
        this.executor = executor;
    }

    @Override
    public Future<Boolean> exists(String key) {
        return new CompletableFuture<Boolean>().completeAsync(() -> {
            try (var pstmt = this.connection.prepareStatement(PRG_QUERY.COUNT.label)) {
                pstmt.setString(1, key);
                try (ResultSet result = pstmt.executeQuery()) {
                    var cnt = 0L;
                    if (result.next()) {
                        cnt = result.getLong("n_rows");
                    }
                    return (cnt > 0);
                }
            } catch (SQLException ex) {
                log.error(ex.getMessage());
                return false;
            }
        }, this.executor);
    }

    @Override
    public Future<Void> create(PurchaseGroup purchaseGroup) {
        return CompletableFuture.runAsync(() -> {
            try (var stmt = this.connection.prepareStatement(PRG_QUERY.CREATE.label)) {
                stmt.setString(1, purchaseGroup.getId());
                stmt.setString(2, purchaseGroup.getName());
                stmt.executeUpdate();
            } catch (SQLException ex) {
                log.error(ex.getMessage());
            }
        }, this.executor);
    }

    @Override
    public Future<Void> update(PurchaseGroup purchaseGroup) {
        return CompletableFuture.runAsync(() -> {
            try (var stmt = this.connection.prepareStatement(PRG_QUERY.UPDATE.label)) {
                stmt.setString(1, purchaseGroup.getName());
                stmt.setString(2, purchaseGroup.getId());
                stmt.executeUpdate();
            } catch (SQLException ex) {
                log.error(ex.getMessage());
            }
        }, this.executor);
    }

    // private section

    private enum PRG_QUERY {

        COUNT("SELECT COUNT( id ) AS n_rows FROM purgroup WHERE id = ?"),
        CREATE("INSERT INTO purgroup VALUES (?, ?)"),
        UPDATE("UPDATE purgroup SET name = ? WHERE id = ?");

        private final String label;
        PRG_QUERY(String label) {
            this.label = label;
        }

    }

}
