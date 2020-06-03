package ru.velkomfood.mm.mrp.uploader.input;

import com.sap.conn.jco.JCoDestination;
import ru.velkomfood.mm.mrp.uploader.MrpUploaderComponent;

import java.util.concurrent.CompletableFuture;

public interface DataReader extends MrpUploaderComponent {
    JCoDestination createDestination();
    CompletableFuture<Void> readUnitsOfMeasure();
    CompletableFuture<Void> readPurchaseGroups();
    CompletableFuture<Void> readStorePlaces();
    CompletableFuture<Void> readMaterials(int rangeNumber);
    CompletableFuture<Void> readStocks();
    CompletableFuture<Void> readRequirements();
}
