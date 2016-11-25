package ru.velkomfood.mrp.client.view;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ru.velkomfood.mrp.client.model.MaterialCache;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by dpetrov on 16.08.2016.
 */
public class DialogBoxListMaterials extends Application {

    private Connection connection;
    private final Text actiontTarget = new Text();
    private Stage window;
    private MaterialCache materials;
    private TextField textMaterial;
    private Button buttonSearch;
    private GridPane grid;
    private ObservableList<String> data;
    private int indexRow;

    public DialogBoxListMaterials(Connection connection) {

        this.connection = connection;
        materials = MaterialCache.getInstance();
        window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Список материалов");
        window.setMaxWidth(350.00);
        window.setMaxHeight(300.00);

    }

    public void display() throws Exception {
        start(window);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(0, 10, 0, 10));
        textMaterial = new TextField();
        grid.add(textMaterial, 0, 1);
        buttonSearch = new Button("Искать");
        buttonSearch.setId("enter-button");
        DropShadow shadow = new DropShadow();
        buttonSearch.addEventHandler(MouseEvent.MOUSE_MOVED, event -> {
            buttonSearch.setEffect(shadow);
        });

        buttonSearch.setOnAction(event -> {

            try {

                buildMaterialsList(textMaterial.getText());
                // Add list view and fill it
                data = FXCollections.observableArrayList();
                materials.getMaterialList().forEach(material -> {
                    String line = material.getId() + " " + material.getDescription();
                    data.add(line);
                });

                ListView<String> listView = new ListView<>(data);
                listView.setPrefSize(450, 400);
                listView.setEditable(false);
                listView.setItems(data);
                listView.addEventHandler(MouseEvent.MOUSE_CLICKED, eventClick -> {
                    indexRow = listView.getSelectionModel().getSelectedIndex();
                    materials.setIndexOfMaterial(indexRow);
                    window.close();
                });
                grid.add(listView, 0, 3);

            } catch (SQLException sqex) {
                sqex.printStackTrace();
            }

        });

        grid.add(buttonSearch, 0, 2);

        Scene scene = new Scene(grid, 500, 600);

        primaryStage.setScene(scene);
        scene.getStylesheets().add
                (DialogBoxListMaterials.class.getResource("/css/window.css").toExternalForm());
        primaryStage.showAndWait();

    }

//    private void handleAction() {
//
//        try {
//
//            buildMaterialsList(textMaterial.getText());
//            // Add list view and fill it
//            data = FXCollections.observableArrayList();
//            materials.getMaterialList().forEach(material -> {
//                String line = material.getId() + " " + material.getDescription();
//                data.add(line);
//            });
//
//            ListView<String> listView = new ListView<>(data);
//            listView.setPrefSize(450, 400);
//            listView.setEditable(false);
//            listView.setItems(data);
//            listView.addEventHandler(MouseEvent.MOUSE_CLICKED, eventClick -> {
//                indexRow = listView.getSelectionModel().getSelectedIndex();
//                materials.setIndexOfMaterial(indexRow);
//                window.close();
//            });
//            grid.add(listView, 0, 3);
//
//        } catch (SQLException sqex) {
//            sqex.printStackTrace();
//        }
//
//    }

    private void buildMaterialsList(String mask) throws SQLException {

        Statement stmt = connection.createStatement();

        try {
            materials.fillMaterialsList(stmt, mask);
        } finally {
            stmt.close();
        }
    }

    public int getIndexRow() {
        return indexRow;
    }

}
