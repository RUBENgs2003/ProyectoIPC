/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javafxmlapplication.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javafx.application.Application.launch;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
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
import model.Booking;
import model.Club;
import model.ClubDAOException;
import model.Member;

public class ActualizarDatosController implements Initializable {

    private Label label_inicioSesion;
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
    private Label errorTelefono;
    @FXML
    private Label errorNombre;
    @FXML
    private Label errorApellidos;
    @FXML
    private Label errorUsuario;
    @FXML
    private Label errorPassword;
    @FXML
    private Label errorTarjeta;
    @FXML
    private Label errorSVC;
    @FXML
    private Button btn_cambiarDatos;
    @FXML
    private Button btn_cancelar;

    private Member member;

    /**
     * Initializes the controller class.
     */
    public void initialize(URL url, ResourceBundle rb) {

        // Crear binding para el label de error del campo "nombre"
        BooleanBinding nombreNoValido = Bindings.createBooleanBinding(() -> {
            String nombre = input_nombre.getText();
            return nombre.isEmpty();
        }, input_nombre.textProperty());

        errorNombre.visibleProperty().bind(nombreNoValido);

        // Crear binding para el label de error del campo "apellidos"
        BooleanBinding apellidosNoValidos = Bindings.createBooleanBinding(() -> {
            String apellidos = input_apellidos.getText();
            return apellidos.isEmpty();
        }, input_apellidos.textProperty());
        errorApellidos.visibleProperty().bind(apellidosNoValidos);

        // Crear binding para el label de error del campo "telefono"
        //NOTA: NO SE SI ES NECESARIO HACER EL REGEX - PREGUNTAR AL PROFESOR
        BooleanBinding telefonoNoValido = Bindings.createBooleanBinding(() -> {
            String regexTel = "\\d{9,}";
            String telefono = input_telefono.getText();
            return telefono.isEmpty() || !telefono.matches(regexTel);
        }, input_telefono.textProperty());
        errorTelefono.visibleProperty().bind(telefonoNoValido);

        // Crear binding para el label de error del campo "usuario"
        BooleanBinding usuarioNoValido = Bindings.createBooleanBinding(() -> {
            String usuario = input_usuario.getText();
            return usuario.isEmpty();
        }, input_usuario.textProperty());
        errorUsuario.visibleProperty().bind(usuarioNoValido);

        // Crear binding para el label de error del campo "contrasena"
        BooleanBinding passwordNoValido = Bindings.createBooleanBinding(() -> input_password.getText().length() <= 6, input_password.textProperty());
        errorPassword.visibleProperty().bind(passwordNoValido);

        // Crear bindings para el número de tarjeta de crédito y el código de seguridad SVC
        // Crear un binding para el número de tarjeta de crédito
        BooleanBinding numeroNoValido = Bindings.createBooleanBinding(() -> {
            // Crear una expresión regular para validar el número de tarjeta de crédito
            String regexNumero = "\\d{16}";
            String numero = input_tarjeta.getText();
            String codigo = input_svc.getText();
            return !(numero.matches(regexNumero) || (numero.isEmpty() && codigo.isEmpty()));
        }, input_tarjeta.textProperty(), input_svc.textProperty());

        // Crear un binding para el código de seguridad SVC
        BooleanBinding codigoNoValido = Bindings.createBooleanBinding(() -> {
            // Crear una expresión regular para validar el código de seguridad SVC
            String regexSVC = "\\d{3}";
            String codigo = input_svc.getText();
            String numero = input_tarjeta.getText();
            return !(codigo.matches(regexSVC) || (numero.isEmpty() && codigo.isEmpty()));
        }, input_svc.textProperty(), input_tarjeta.textProperty());

        errorTarjeta.visibleProperty().bind(numeroNoValido);
        errorSVC.visibleProperty().bind(codigoNoValido);

        //si todos los campos son validos -> registrarse
        //COMPLETAR
        // Crear un binding que dependa de todos los bindings anteriores
        BooleanBinding datosValidos = Bindings.or(nombreNoValido, apellidosNoValidos)
                .or(telefonoNoValido).or(passwordNoValido).or(usuarioNoValido).or(numeroNoValido).or(codigoNoValido);

        // Vincular el binding resultante con la propiedad visible del label de error global
        btn_cambiarDatos.disableProperty().bind(datosValidos);
    }

    public void setMemberInfo(Member member) {
        this.member = member;
        input_nombre.setText(member.getName());
        input_apellidos.setText(member.getSurname());
        if (member.getSvc() == 0) {
            input_svc.setText("");
        } else {
            input_svc.setText(String.valueOf(member.getSvc()));
        }
        input_tarjeta.setText(member.getCreditCard());
        input_telefono.setText(member.getTelephone());
        input_usuario.setText(member.getNickName());
        //COMPLETAR - IMAGEN
        //...
    }

    @FXML
    private void cambiarDatos(ActionEvent event) throws ClubDAOException, IOException {

        //actualizar datos del miembro
        int svc = input_svc.getText().equals("") ? 0 : Integer.parseInt(input_svc.getText());

        member.setCreditCard(input_tarjeta.getText());

        //COMPLETAR - CAMBIAR IMAGEN
        //member.setImage(image); ...
        member.setName(input_nombre.getText());
        member.setPassword(input_password.getText());
        member.setTelephone(input_telefono.getText());
        member.setSvc(svc);
        member.setSurname(input_apellidos.getText());

        // Mostrar modal
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Datos actualizados correctamente");
        alert.setHeaderText(null);
        alert.setContentText("Sus datos han sido actualizados satisfactoriamente.");

        // Agregar un ícono de éxito
        ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("/images/successIcon.png")));
        imageView.setFitHeight(64);
        imageView.setFitWidth(64);
        alert.setGraphic(imageView);

        // Cambiar el texto del botón OK
        ButtonType loginButtonType = new ButtonType("Entendido", ButtonData.OK_DONE);
        alert.getButtonTypes().setAll(loginButtonType);

        // Agregar un evento de botón
        Button loginButton = (Button) alert.getDialogPane().lookupButton(loginButtonType);
        loginButton.setOnAction(e -> {

            Stage currentStage = (Stage) btn_cambiarDatos.getScene().getWindow();
            currentStage.close();

            FXMLLoader miCargador = new FXMLLoader(getClass().getResource("../view/verPistasAutenticado.fxml"));
            Parent root = null;
            try {
                root = miCargador.load();
            } catch (IOException ex) {
                Logger.getLogger(ActualizarDatosController.class.getName()).log(Level.SEVERE, null, ex);
            }
            VerPistasAutenticadoController controller = miCargador.getController();
            controller.setMemberInfo(member);
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Ver pistas - Club de tenis GreenBall");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(currentStage);
            stage.getIcons().add(new Image("images/greenball.png"));

            stage.show();
        });
        alert.showAndWait();

    }

    @FXML
    private void cambiarImagen(ActionEvent event) throws IOException {
        //COMPLETAR
    }

    @FXML
    private void cancelar(ActionEvent event) throws IOException {
        Stage currentStage = (Stage) btn_cambiarDatos.getScene().getWindow();
        currentStage.close();

        FXMLLoader miCargador = new FXMLLoader(getClass().getResource("../view/verPistasAutenticado.fxml"));
        Parent root = miCargador.load();
        VerPistasAutenticadoController controller = miCargador.getController();
        controller.setMemberInfo(member);
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Ver pistas - Club de tenis GreenBall");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(currentStage);
        stage.getIcons().add(new Image("images/greenball.png"));

        stage.show();
    }

}
