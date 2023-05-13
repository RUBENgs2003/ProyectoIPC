/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javafxmlapplication.controller;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import model.Booking;
import model.Club;
import model.ClubDAOException;

/**
 *
 * @author ruben
 */
public class VerPistasController implements Initializable {

    @FXML
    private Button btn_registrarse;
    @FXML
    private Button btn_iniciarSesion;
    @FXML
    private DatePicker datePicker;
    @FXML
    private Label lbl_pista1;
    @FXML
    private Label lbl_pista2;
    @FXML
    private Label lbl_pista3;
    @FXML
    private ImageView img_pista1;
    @FXML
    private ImageView img_pista2;
    @FXML
    private ImageView img_pista3;
    @FXML
    private Label lbl_pista4;
    @FXML
    private Label lbl_pista5;
    @FXML
    private Label lbl_pista6;
    @FXML
    private ImageView img_pista4;
    @FXML
    private ImageView img_pista6;
    @FXML
    private GridPane gridPane;
    @FXML
    private HBox gridPaneHbox;
    @FXML
    private Spinner<LocalTime> spinner;
    @FXML
    private Text lbl_fechaHora;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        try {

            Club club = Club.getInstance();

            int timeDuration = club.getBookingDuration();

            SpinnerValueFactory<LocalTime> valueFactory = new SpinnerValueFactory<LocalTime>() {
                {
                    setConverter(new StringConverter<LocalTime>() {
                        private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

                        @Override
                        public String toString(LocalTime time) {
                            if (time == null) {
                                return "";
                            }
                            return formatter.format(time);
                        }

                        @Override
                        public LocalTime fromString(String string) {
                            if (string == null || string.isEmpty()) {
                                return null;
                            }
                            return LocalTime.parse(string, formatter);
                        }
                    });

                    setValue(LocalTime.now().plusHours(1).withMinute(0).withSecond(0)); // Establece el valor predeterminado

                }

                @Override
                public void decrement(int steps) {
                    LocalTime time = getValue();
                    int minutes = steps * timeDuration;
                    setValue(time.minusMinutes(minutes));
                }

                @Override
                public void increment(int steps) {
                    LocalTime time = getValue();
                    int minutes = steps * timeDuration;
                    setValue(time.plusMinutes(minutes));
                }
            };

            spinner.setValueFactory(valueFactory);

            datePicker.setValue(LocalDate.now());

            StringBinding fechaHoraBinding = Bindings.createStringBinding(() -> {

                LocalDate fecha = datePicker.getValue(); // Obtener el valor del DatePicker
                int hora = spinner.getValue().getHour(); // Obtener el valor del Spinner
                return "Fecha seleccionada: " + fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " " + hora + ":00 horas"; // Combinar la fecha y la hora en un texto

            }, datePicker.valueProperty(), spinner.valueProperty());

            lbl_fechaHora.textProperty().bind(fechaHoraBinding); // Asignar el Binding al texto del Label

            club.getForDayBookings(LocalDate.now());
            List<Booking> bookings = Club.getInstance().getForDayBookings(LocalDate.now());
            for (Booking booking : bookings) {
                String pista = booking.getCourt().getName();
                switch (pista) {
                    case "Pista 1":
                        img_pista1.setImage(cambiarImagenPista(img_pista1.getImage()));
                        break;
                    default:
                        System.out.println(pista);
                }
            }

        } catch (ClubDAOException ex) {
            Logger.getLogger(VerPistasController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(VerPistasController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @FXML
    private void cambiarAInicioSesion() throws IOException {
        Stage currentStage = (Stage) lbl_pista1.getScene().getWindow();
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
    private void cambiarARegistro() throws IOException {
        Stage currentStage = (Stage) lbl_pista1.getScene().getWindow();
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

        stage.show();

    }

    // Si la pista está ocupada, crea una versión en rojo de la imagen y la devuelve
    private WritableImage cambiarImagenPista(Image imagen) {
        PixelReader pixelReader = imagen.getPixelReader();
        int width = (int) imagen.getWidth();
        int height = (int) imagen.getHeight();
        WritableImage rojo = new WritableImage(width, height);
        PixelWriter pixelWriter = rojo.getPixelWriter();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Color color = pixelReader.getColor(x, y);
                if (color.equals(Color.web("#55a357")) || color.equals(Color.web("#d8ead8")) 
                        || color.equals(Color.web("#54a454")) || color.equals(Color.web("#90c391"))
                        || color.equals(Color.web("#a0cca0"))) {
                    pixelWriter.setColor(x, y, Color.web("#fb4949"));
                } else {
                    pixelWriter.setColor(x, y, color);
                }
            }
        }
        return rojo;
    }
}
