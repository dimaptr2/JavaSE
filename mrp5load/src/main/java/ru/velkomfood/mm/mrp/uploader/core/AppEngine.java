package ru.velkomfood.mm.mrp.uploader.core;

import ru.velkomfood.mm.mrp.uploader.MrpUploaderComponent;
import ru.velkomfood.mm.mrp.uploader.input.DataReader;
import ru.velkomfood.mm.mrp.uploader.output.DataWriter;

import java.sql.SQLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

public class AppEngine implements MrpUploaderComponent {

    // Components
    private final MrpUploaderComponent[] components;
    private final ExecutorService service;
    private final Runnable[] executableTasks;

    // Tasks
    // 0 index for Data Reader
    // 1 index for Data Writer
    public AppEngine(ExecutorService service) {
        components = new MrpUploaderComponent[2];
        this.service = service;
        executableTasks = new Runnable[2];
    }

    // 0 - Data Reader;
    // 1 - Data Writer.
    public void addComponent(MrpUploaderComponent component) {
        if (component instanceof DataReader) {
            components[0] = component;
        } else if (component instanceof DataWriter) {
            components[1] = component;
        }
    }

    public void buildTasks(int rangeMaterials) {

        Runnable taskForReadMasterData = () -> {
            log.info("Start the master data reading");
            final DataReader reader = (DataReader) components[0];
            final DataWriter writer = (DataWriter) components[1];
            var destination = reader.createDestination();
            if (destination != null && destination.isValid()) {
                var futureUoms = reader.readUnitsOfMeasure();
                var futurePurchaseGroups = reader.readPurchaseGroups();
                var futureStorePlaces = reader.readStorePlaces();
                var futureMaterials = reader.readMaterials(rangeMaterials);
                try {
                    futureUoms.get();
                    futurePurchaseGroups.get();
                    futureStorePlaces.get();
                    futureMaterials.get();
                    try {
                        if (futureUoms.isDone()) {
                            writer.save("uom.queue");
                        }
                        if (futurePurchaseGroups.isDone()) {
                            writer.save("purchase.group.queue");
                        }
                        if (futureStorePlaces.isDone()) {
                            writer.save("store.place.queue");
                        }
                        if (futureMaterials.isDone() && futureUoms.isDone()) {
                            writer.save("material.queue");
                        }
                    } catch (SQLException sqlEx) {
                        log.error(sqlEx.getMessage());
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    log.error(ex.getMessage());
                }
                if (futureUoms.isDone()
                        && futurePurchaseGroups.isDone()
                        && futureStorePlaces.isDone()
                        && futureMaterials.isDone()) {
                    log.info("Finish the master data reading");
                }
            }
        };

        Runnable taskForReadTransactionData = () -> {
            final DataReader reader = (DataReader) components[0];
            final DataWriter writer = (DataWriter) components[1];
            log.info("Start the transaction data reading");
            var futureStocks = reader.readStocks();
            var futureRequirements = reader.readRequirements();
            try {
                futureStocks.get();
                futureRequirements.get();
                if (futureStocks.isDone()) {
                    writer.save("stock.queue");
                    log.info("Stocks are read");
                }
                if (futureRequirements.isDone()) {
                    writer.save("requirement.queue");
                    log.info("Requirements are read");
                }
            } catch (InterruptedException | ExecutionException | SQLException ex) {
                log.error(ex.getMessage());
            }
            log.info("Finish the transaction data reading");
        };

        executableTasks[0] = taskForReadMasterData;
        executableTasks[1] = taskForReadTransactionData;

    }

    public void start() {

        int executionFlag = 0;
        for (Runnable task : executableTasks) {
            if (task != null) {
                executionFlag += 1;
            }
        }

        if (executionFlag != 2) {
            log.error("No tasks found");
            service.shutdown();
            return;
        }
        // Start the main job
        var mdReading = service.submit(executableTasks[0]);
        try {
            mdReading.get();
            if (mdReading.isDone()) {
                var tdReading = service.submit(executableTasks[1]);
                tdReading.get();
                if (tdReading.isDone()) {
                    log.info("The job is finished");
                }
            }
        } catch (InterruptedException | ExecutionException ex) {
            log.error(ex.getMessage());
        }
        // Finish the work of executor
        if (!service.isShutdown()) {
            service.shutdown();
        }

    } // end of start

}
