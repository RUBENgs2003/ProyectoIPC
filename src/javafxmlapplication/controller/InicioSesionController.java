/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package javafxmlapplication.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Club;
import model.ClubDAOException;
import model.Member;

/**
 * FXML Controller class
 *
 *
 */
public class InicioSesionController implements Initializable {

    @FXML
    private Button btn_registro;
    @FXML
    private TextField input_usuario;
    @FXML
    private TextField input_password;
    @FXML
    private Button btn_iniciarSesion;
    @FXML
    private Label label_errorPassword;
    @FXML
    private Label label_errorUsuario;
    @FXML
    private TextField input_password_2;
    @FXML
    private Button vista_input_password;
    @FXML
    private ImageView ojo_vista;
    @FXML
    private Button btn_volver;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        //bindear boton inicio de sesion con los campos
        btn_iniciarSesion.disableProperty().bind(
                Bindings.or(Bindings.isEmpty(input_usuario.textProperty()), Bindings.isEmpty(input_password.textProperty()))
        );
        
        // Crear binding para mostrar la contraseña
        input_password_2.textProperty().bindBidirectional(input_password.textProperty());

    }

    @FXML
    private void cambiarARegistro() throws IOException {
        Stage currentStage = (Stage) btn_registro.getScene().getWindow();
        currentStage.close();

        FXMLLoader miCargador = new FXMLLoader(getClass().getResource("../view/registroSocio.fxml"));
        Parent root = miCargador.load();
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Registrar socio - Club de tenis GreenBall");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(currentStage);
        stage.getIcons().add(new Image("images/greenball.png"));
        // Deshabilitar la función redimensionar la ventana
        stage.setResizable(false);

        stage.show();

    }

    @FXML
    private void iniciarSesion(ActionEvent event) throws ClubDAOException, IOException {

        //por defecto
        label_errorPassword.visibleProperty().setValue(false);
        label_errorUsuario.visibleProperty().setValue(false);

        //obtenemos si el usuario existe
        Boolean exists = Club.getInstance().existsLogin(input_usuario.getText());
        //si no existe mostramos el error
        label_errorUsuario.visibleProperty().setValue(!exists);

        //miramos si la contrasena es correcta
        if (exists) {
            Member member = Club.getInstance().getMemberByCredentials(input_usuario.getText(), input_password.getText());
            //si no es correcta mostramos error
            label_errorPassword.visibleProperty().setValue(member == null);

            if (member != null) {
                
                Stage currentStage = (Stage) btn_registro.getScene().getWindow();
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
                // Con root.prefWidth(-1) obtenemos el width predefinido
                stage.setMinWidth(root.prefWidth(-1) + 20);
                stage.setMinHeight(root.prefHeight(-1) + 40);
                stage.show();
                
            }
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

    @FXML
    private void volver(ActionEvent event) throws IOException {
        //volver atras - ventana principal
        Stage currentStage = (Stage) btn_volver.getScene().getWindow();
        currentStage.close();

        FXMLLoader miCargador = new FXMLLoader(getClass().getResource("../view/verPistas.fxml"));
        Parent root = miCargador.load();
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Ver pistas - Club de tenis GreenBall");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(currentStage);
        stage.getIcons().add(new Image("images/greenball.png"));
        // Con root.prefWidth(-1) obtenemos el width predefinido
        stage.setMinWidth(root.prefWidth(-1) + 20);
        stage.setMinHeight(root.prefHeight(-1) + 40);

        stage.show();
    }

}
