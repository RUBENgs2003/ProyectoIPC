package javafxmlapplication.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class SeleccionarImagenController {

    private Stage stage;
    @FXML
    private Button men4;
    @FXML
    private Button men1;
    @FXML
    private Button men2;
    @FXML
    private Button men3;
    @FXML
    private Button men5;
    @FXML
    private Button woman6;
    @FXML
    private Button woman5;
    @FXML
    private Button woman1;
    @FXML
    private Button woman2;
    @FXML
    private Button woman3;
    @FXML
    private Button woman4;
    @FXML
    private Button btn_aceptar;
    @FXML
    private Button raqueta;
    @FXML
    private Button none;

    private String imagen = "default";
    private Stage seleccionarImagenStage;
    private Button lastButtonSelected;

    public void initialize() {
        btn_aceptar.disableProperty().setValue(Boolean.TRUE);
    }

    @FXML
    private void seleccionarImagen(ActionEvent event) {
        seleccionarImagenStage.close();
    }

    public void setStage(Stage stage) {
        this.seleccionarImagenStage = stage;
        seleccionarImagenStage.setResizable(false);
    }

    String getImagen() {
        return imagen;
    }

    @FXML
    private void imgSeleccionada(ActionEvent event) {
        Button btn = (Button) event.getSource();
        if(lastButtonSelected == null) lastButtonSelected = btn;
        else{
            lastButtonSelected.getStyleClass().clear();
            lastButtonSelected.getStyleClass().add("button");
            lastButtonSelected = btn;
        }
        btn.getStyleClass().add("button1");
        imagen = btn.getId();
        btn_aceptar.disableProperty().setValue(Boolean.FALSE);
    }
}
