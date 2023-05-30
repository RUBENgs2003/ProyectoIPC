package javafxmlapplication.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

import java.io.File;
import java.io.IOException;

public class SeleccionarImagenController {

    private ListView<File> imageListView;

    private File selectedImage;

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
        btn.getStyleClass().add("button3");
        imagen = btn.getId();
        btn_aceptar.disableProperty().setValue(Boolean.FALSE);
    }
}
