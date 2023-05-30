/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javafxmlapplication.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
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

public class RegistroSocioController implements Initializable {

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
    private int numeroImagen = 1;
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
    private boolean flag;
    @FXML
    private Button vista_input_password;
    @FXML
    private TextField input_password_2;
    @FXML
    private ImageView ojo_vista;

    /**
     * Initializes the controller class.
     */
    public void initialize(URL url, ResourceBundle rb) {
        flag = true;

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
        BooleanBinding telefonoNoValido = Bindings.createBooleanBinding(() -> {
            String regexTel = "\\d{9}";
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
        
        // Crear binding para mostrar la contraseña
        input_password_2.textProperty().bindBidirectional(input_password.textProperty());

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

        // Crear un binding que dependa de todos los bindings anteriores
        BooleanBinding datosValidos = Bindings.or(nombreNoValido, apellidosNoValidos)
                .or(telefonoNoValido).or(passwordNoValido).or(usuarioNoValido).or(numeroNoValido).or(codigoNoValido);

        // Vincular el binding resultante con la propiedad visible del label de error global
        btn_registrarse.disableProperty().bind(datosValidos);
    }

    @FXML
    private void registrarse(ActionEvent event) throws ClubDAOException, IOException {

        Club club = Club.getInstance();
        //checkear si el usuario ya existe
        List<Member> members = club.getMembers();
        for (Member member : members) {
            if (member.getNickName().equals(input_usuario.getText())) {
                flag = false;
                break;
            }
        }

        if (flag) {

            //registrar miembro
            int svc = input_svc.getText().equals("") ? 0 : Integer.parseInt(input_svc.getText());

            Member result = club.registerMember(
                    input_nombre.getText(),
                    input_apellidos.getText(),
                    input_telefono.getText(),
                    input_usuario.getText(),
                    input_password.getText(),
                    input_tarjeta.getText(),
                    svc,
                    imagenPerfil.getImage());

            //desactivar boton de registro
            btn_registrarse.disableProperty().unbind();
            btn_registrarse.disableProperty().setValue(Boolean.TRUE);

            // Mostrar modal
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Usuario registrado");
            alert.setHeaderText(null);
            alert.setContentText("Su usuario ha sido registrado satisfactoriamente. Ahora puede iniciar sesión.");

            // Agregar un ícono de éxito
            ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("/images/successIcon.png")));
            imageView.setFitHeight(64);
            imageView.setFitWidth(64);
            alert.setGraphic(imageView);

            // Cambiar el texto del botón OK
            ButtonType loginButtonType = new ButtonType("Iniciar sesión", ButtonData.OK_DONE);
            alert.getButtonTypes().setAll(loginButtonType);

            // Agregar un evento de botón
            Button loginButton = (Button) alert.getDialogPane().lookupButton(loginButtonType);
            loginButton.setOnAction(e -> {
                try {
                    cambiarAInicioSesion();
                } catch (IOException ex) {
                    Logger.getLogger(RegistroSocioController.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            alert.showAndWait();

        } else {
            errorUsuario.visibleProperty().unbind();
            errorUsuario.setText("Este nombre de usuario ya está en uso");
            errorUsuario.setVisible(true);
        }

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
        stage.setMinWidth(637);
        stage.setMinHeight(476);

        stage.show();

    }

    @FXML
    private void cambiarImagen(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/seleccionarImagen.fxml"));
        Parent root = loader.load();

        // Obtener el controlador de la nueva ventana
        SeleccionarImagenController controller = loader.getController();

        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Seleccionar imagen de perfil - Club de tenis GreenBall");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.getIcons().add(new Image("images/greenball.png"));
        controller.setStage(stage);
        
        // Registrar el evento setOnHidden para capturar los datos al cerrar la ventana
        stage.setOnHidden(e -> {
            String imagen = controller.getImagen();
            imagenPerfil.setImage(new Image("/images/" + imagen + ".png"));

        });
        
        stage.showAndWait();

    }

    @FXML
    private void escribe_usuario(MouseEvent event) {
        if (!flag) {
            errorUsuario.setText("Introduce un nombre de usuario");
            // Crear binding para el label de error del campo "usuario"
            BooleanBinding usuarioNoValido = Bindings.createBooleanBinding(() -> {
                String usuario = input_usuario.getText();
                return usuario.isEmpty();
            }, input_usuario.textProperty());
            errorUsuario.visibleProperty().bind(usuarioNoValido);
            flag = true;
        }
    }

    @FXML
    private void modificar_vista_input_password(ActionEvent event) {
        if (input_password_2.isVisible()) {
            ojo_vista.setImage(new Image("/images/ojo_1.png"));
            input_password_2.setVisible(false);
            input_password.setVisible(true);
        } else {
            ojo_vista.setImage(new Image("/images/ojo_2.png"));
            input_password_2.setVisible(true);
            input_password.setVisible(false);
        }
    }
}
