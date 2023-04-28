/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javafxmlapplication.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

/**
 *
 * @author ruben
 */
public class RegistroSocioController {

    @FXML
    private TextField textFieldNombre;
    @FXML
    private TextField textFieldApellidos;
    @FXML
    private TextField textFieldTelefono;
    @FXML
    private TextField textFieldNickname;
    @FXML
    private Label labelErrorNombre;
    @FXML
    private Label labelErrorApellidos;
    @FXML
    private Label labelErrorTelefono;
    @FXML
    private Label labelErrorNickname;
    @FXML
    private Label labelErrorPassword;
    @FXML
    private ImageView imagenUsser;
    @FXML
    private Button buttonCambiarImagen;
    @FXML
    private TextField textFieldCredito;
    @FXML
    private Label labelErrorCredito;
    @FXML
    private PasswordField password;
    @FXML
    private TextField textFieldSCV;
    @FXML
    private Label labelErrorSVC;
    @FXML
    private Button buttonAceptar;
    @FXML
    private Button buttonCancelar;
    @FXML
    private Button buttonVerPassord;

    @FXML
    private void accionCambiarImagen(ActionEvent event) {
    }

    @FXML
    private void accionAceptar(ActionEvent event) {
    }

    @FXML
    private void accionCancelar(ActionEvent event) {
    }

    @FXML
    private void accionVerPassord(ActionEvent event) {
    }
    
}
