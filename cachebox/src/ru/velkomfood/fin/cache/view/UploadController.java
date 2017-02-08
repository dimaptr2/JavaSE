package ru.velkomfood.fin.cache.view;

import com.sap.conn.jco.JCoException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import ru.velkomfood.fin.cache.controller.CacheEngine;
import ru.velkomfood.fin.cache.controller.SapSniffer;

/**
 * Created by dpetrov on 07.02.2017.
 */
public class UploadController {

    private SapSniffer sapSniffer;
    private CacheEngine cache;
    private String datum;

    private Stage uploadStage;

    @FXML
    private ProgressBar pb;
    @FXML
    private Button btnUpload;

    public void setSapSniffer(SapSniffer sapSniffer) {
        this.sapSniffer = sapSniffer;
    }

    public void setCache(CacheEngine cache) {
        this.cache = cache;
    }

    public void setDatum(String datum) {
        this.datum = datum;
    }

    public void setUploadStage(Stage uploadStage) {
        this.uploadStage = uploadStage;
    }

    @FXML
    public void initialize() {
        pb.setProgress(0.0f);
    }

    @FXML
    public void uploadCreditSlips() {

        final Float[] values = new Float[] {0.1f, 0.3f, 0.6f, 0.8f, 0.9f, 1.0f};

        if (cache != null) {

            try {

                pb.setProgress(values[0]);
                sapSniffer.getMaterialInfo();
                pb.setProgress(values[1]);
                sapSniffer.readCreditSlips(datum, datum);
                pb.setProgress(values[2]);
                // Finish the process
                pb.setProgress(values[4]);
                uploadStage.close();

            } catch (JCoException e) {

                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("SAP connection error ...");
                alert.setContentText(e.getMessage());

            }
        } // cache is not null

    }

}
