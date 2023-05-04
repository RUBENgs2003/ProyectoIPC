/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javafxmlapplication.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class RegistroSocioController {

    @FXML
    private Label label_inicioSesion;
    @FXML
    private Button btn_registrarse;
    @FXML
    private TextField input_nombre;
    @FXML
    private TextField input_telefono;
    @FXML
    private Button btn_cambiarImagen;
    @FXML
    private TextField input_apellidos;
    @FXML
    private TextField input_usuario;
    @FXML
    private TextField input_password;
    @FXML
    private TextField input_tarjeta;
    @FXML
    private TextField input_svc;
    @FXML
    private ImageView imagenPerfil;

    @FXML
    private void registrarse(ActionEvent event) {
    }

    @FXML
    private void cambiarAInicioSesion() throws IOException {
        Stage currentStage = (Stage) label_inicioSesion.getScene().getWindow();
        currentStage.close();

        FXMLLoader miCargador = new FXMLLoader(getClass().getResource("../view/inicioSesion.fxml"));
        Parent root = miCargador.load();
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Iniciar sesión - Club de tenis GreenBall");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(currentStage);
        stage.getIcons().add(new Image("images/greenball.png"));

        stage.show();

    }

    @FXML
    private void cambiarImagen(ActionEvent event) throws IOException {
        
        /*  -------------------------- EN PRUEBAS  ----------------------------------- */
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Abrir fichero");
        fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.gif"));
        File selectedFile = fileChooser.showOpenDialog(
                ((Node) event.getSource()).getScene().getWindow());
        if (selectedFile != null) {
            Path profileImageFolder = Paths.get("images");
            Path destinationPath = profileImageFolder.resolve(selectedFile.toPath().getFileName());
            Files.copy(selectedFile.toPath(), destinationPath);
        }

        /*  -------------------------- EN PRUEBAS  ----------------------------------- */
        
    }
}
