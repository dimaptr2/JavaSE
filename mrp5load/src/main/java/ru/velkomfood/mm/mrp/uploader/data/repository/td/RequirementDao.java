package ru.velkomfood.mm.mrp.uploader.data.repository.td;

import ru.velkomfood.mm.mrp.uploader.MrpUploaderComponent;
import ru.velkomfood.mm.mrp.uploader.data.model.td.Requirement;
import ru.velkomfood.mm.mrp.uploader.data.model.td.RequirementKey;
import ru.velkomfood.mm.mrp.uploader.data.repository.AsyncDao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class RequirementDao implements AsyncDao<Requirement, RequirementKey>, MrpUploaderComponent {

    private final Connection connection;
    private final ExecutorService executor;

    public RequirementDao(Connection connection, ExecutorService executor) {
        this.connection = connection;
        this.executor = executor;
    }

    @Override
    public Future<Boolean> exists(RequirementKey key) {
        return CompletableFuture.supplyAsync(() -> {
            try (var stmt = this.connection.prepareStatement(REQ_QUERY.COUNT.label)) {
                stmt.setLong(1, key.getMaterialId());
                stmt.setString(2, key.getPlantId());
                stmt.setString(3, key.getPurchaseGroupId());
                stmt.setInt(4, key.getFiscalYear());
                stmt.setInt(5, key.getPeriod());
                try (var resSet = stmt.executeQuery()) {
                    var cnt = 0L;
                    if (resSet.next()) {
                        cnt = resSet.getLong("n_rows");
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
    public Future<Void> create(Requirement requirement) {
        return CompletableFuture.runAsync(() -> {
            try (var crStmt = this.connection.prepareStatement(REQ_QUERY.CREATE.label)) {
                crStmt.setLong(1, requirement.getMaterialId());
                crStmt.setString(2, requirement.getPlantId());
                crStmt.setString(3, requirement.getPurchaseGroupId());
                crStmt.setInt(4, requirement.getFiscalYear());
                crStmt.setInt(5, requirement.getPeriod());
                crStmt.setString(6, requirement.getUomId());
                crStmt.setBigDecimal(7, requirement.getQuantity());
                crStmt.executeUpdate();
            } catch (SQLException ex) {
                log.error(ex.getMessage());
            }
        }, this.executor);
    }

    @Override
    public Future<Void> update(Requirement requirement) {
        return CompletableFuture.runAsync(() -> {
            try (var upStmt = this.connection.prepareStatement(REQ_QUERY.UPDATE.label)) {
                upStmt.setString(1, requirement.getUomId());
                upStmt.setBigDecimal(2, requirement.getQuantity());
                upStmt.setLong(3, requirement.getMaterialId());
                upStmt.setString(4, requirement.getPlantId());
                upStmt.setString(5, requirement.getPurchaseGroupId());
                upStmt.setInt(6, requirement.getFiscalYear());
                upStmt.setInt(7, requirement.getPeriod());
                upStmt.executeUpdate();
            } catch (SQLException ex) {
                log.error(ex.getMessage());
            }
        }, this.executor);
    }

    // private section

    private enum REQ_QUERY {

        COUNT("SELECT COUNT( * ) AS n_rows FROM requirement " +
                "WHERE material_id = ? AND plant_id = ? AND purgrp_id = ? AND fiscal_year = ? AND period_id = ?"),
        CREATE("INSERT INTO requirement VALUES (?, ?, ?, ?, ?, ?, ?)"),
        UPDATE("UPDATE requirement SET uom_id = ?, quantity = ? " +
                "WHERE material_id = ? AND plant_id = ? AND purgrp_id = ? AND fiscal_year = ? AND period_id = ?");

        private final String label;

        REQ_QUERY(String label) {
            this.label = label;
        }

    }

}
