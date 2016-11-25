package ru.velkomfood.mrp.client;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ru.velkomfood.mrp.client.config.ConfigGenerator;
import ru.velkomfood.mrp.client.view.StartForm;


public class Launcher extends Application {

  private ConfigGenerator configurator;

  private String nameOfUser;
  private TextField userTextField;
  private PasswordField passwordField;
  private final Text actiontTarget = new Text();

  // Before window starting ...
  @Override
  public void init() {

    configurator = ConfigGenerator.getInstance();
    nameOfUser = "mrpreader";

  } // init()

  @Override
  public void start(Stage primaryStage) throws Exception {

    Stage startWindow = primaryStage;

    GridPane grid = new GridPane();
    grid.setAlignment(Pos.CENTER);
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(25, 25, 25, 25));

    Text sceneTitle = new Text("Доступ к базе данных");
    sceneTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
    sceneTitle.setId("scene-text");
    grid.add(sceneTitle, 0, 0, 2, 1);

    Label userName = new Label("Пользователь: ");
    grid.add(userName, 0, 1);

    userTextField = new TextField();
    userTextField.setText(nameOfUser);
    grid.add(userTextField, 1, 1);

    Label pwLabel = new Label("Пароль: ");
    grid.add(pwLabel, 0, 2);

    passwordField = new PasswordField();
    grid.add(passwordField, 1, 2);

    DropShadow dropShadow = new DropShadow();

    Button btnOk = new Button("Ввод");
    btnOk.setId("enter-button");
    HBox hbBtn = new HBox(10);
    hbBtn.setAlignment(Pos.BOTTOM_RIGHT);

    btnOk.addEventHandler(MouseEvent.MOUSE_MOVED, event -> {
      btnOk.setEffect(dropShadow);
    });

    btnOk.setOnAction(event -> pressOkButton(configurator));
    hbBtn.getChildren().add(btnOk);
    grid.add(hbBtn, 1, 4);

    actiontTarget.setId("action-target");
    grid.add(actiontTarget, 1, 6);

    Scene scene = new Scene(grid, 300, 275);
    startWindow.setScene(scene);
    scene.getStylesheets().add
            (Launcher.class.getResource("/css/window.css").toExternalForm());
    startWindow.setTitle("Отчет ППМ");
    startWindow.show();

  } // start()

  // After window closing ...
  @Override
  public void stop() {

  } // stop()

  // Event handlers on this form

  // end of section of controls

  // ----- event handlers ----- //
  // Press on the button
  private void pressOkButton(ConfigGenerator configGenerator) {

    actiontTarget.setFill(Color.FIREBRICK);

    Stage secondStage = new Stage();
    secondStage.initModality(Modality.APPLICATION_MODAL);
    StartForm secondWindow = StartForm.getInstance();

    try {
      actiontTarget.setText("");
      secondWindow.setConnection(configGenerator.
              getDataSource(userTextField.getText(), passwordField.getText()).
              getConnection());
      secondWindow.start(secondStage);
    } catch (Exception e) {
      actiontTarget.setText(e.getMessage());
    }

  }

  // end of event handlers section

  public static void main(String[] args) {
    launch(args);
  }

}
