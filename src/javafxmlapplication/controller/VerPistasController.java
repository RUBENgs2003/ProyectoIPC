/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javafxmlapplication.controller;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import model.Booking;
import model.Club;
import model.ClubDAOException;
import model.Court;

/**
 *
 * @author ruben
 */
public class VerPistasController implements Initializable {

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
    private Spinner<LocalTime> spinner;
    @FXML
    private Text lbl_fechaHora;
    @FXML
    private ImageView img_pista5;

    Map<String, Pista> pistaMap = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        try {

            Club club = Club.getInstance();

            List<Court> courts = club.getCourts();

            //se sabe que el número de pistas es 6 (no se puede aumentar ni disminuir)
            pistaMap.put(courts.get(0).getName(), new Pista(img_pista1, lbl_pista1));
            pistaMap.put(courts.get(1).getName(), new Pista(img_pista2, lbl_pista2));
            pistaMap.put(courts.get(2).getName(), new Pista(img_pista3, lbl_pista3));
            pistaMap.put(courts.get(3).getName(), new Pista(img_pista4, lbl_pista4));
            pistaMap.put(courts.get(4).getName(), new Pista(img_pista5, lbl_pista5));
            pistaMap.put(courts.get(5).getName(), new Pista(img_pista6, lbl_pista6));

            int timeDuration = club.getBookingDuration();

            SpinnerValueFactory<LocalTime> valueFactory = new SpinnerValueFactory<LocalTime>() {

                private final LocalTime minTime = LocalTime.of(9, 0);
                private final LocalTime maxTime = LocalTime.of(21, 0);

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
                    if (time.minusMinutes(minutes).compareTo(minTime) >= 0) {
                        setValue(time.minusMinutes(minutes));
                    } else {
                        Duration duration = Duration.between(minTime, time);
                        long minutesUntilMinTime = duration.toMinutes() % (maxTime.toSecondOfDay() / 60);
                        setValue(maxTime.minusMinutes(minutesUntilMinTime));
                    }
                }

                @Override
                public void increment(int steps) {
                    LocalTime time = getValue();
                    int minutes = steps * timeDuration;
                    if (time.plusMinutes(minutes).compareTo(maxTime) <= 0) {
                        setValue(time.plusMinutes(minutes));
                    } else {
                        Duration duration = Duration.between(time, maxTime);
                        long minutesUntilMaxTime = duration.toMinutes() % (maxTime.toSecondOfDay() / 60);
                        setValue(minTime.plusMinutes(minutesUntilMaxTime));
                    }
                }

            };

            spinner.setValueFactory(valueFactory);

            // Crear una fábrica de celdas de día personalizada
            datePicker.setDayCellFactory(picker -> new DateCell() {
                @Override
                public void updateItem(LocalDate date, boolean empty) {
                    if (date.isBefore(LocalDate.now())) {
                        setDisable(true); // Desactivar la celda si la fecha es anterior a la fecha actual
                    }
                }
            });

            datePicker.setValue(LocalDate.now());

            StringBinding fechaHoraBinding = Bindings.createStringBinding(() -> {

                LocalDate fecha = datePicker.getValue(); // Obtener el valor del DatePicker
                int hora = spinner.getValue().getHour(); // Obtener el valor del Spinner
                return "Fecha seleccionada: " + fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " " + hora + ":00 horas"; // Combinar la fecha y la hora en un texto

            }, datePicker.valueProperty(), spinner.valueProperty());

            lbl_fechaHora.textProperty().bind(fechaHoraBinding); // Asignar el Binding al texto del Label

            // Agregar un ChangeListener al valueProperty
            datePicker.valueProperty().addListener((ObservableValue<? extends LocalDate> observable, LocalDate oldValue, LocalDate newValue) -> {
                // Llamar a la función deseada con el nuevo valor seleccionado como argumento
                obtenerDisponibilidad(club, newValue);
            });

            spinner.valueProperty().addListener((observable, oldValue, newValue) -> {
                LocalDate fechaSeleccionada = datePicker.getValue();
                if (fechaSeleccionada != null) {
                    LocalDateTime fechaHoraSeleccionada = LocalDateTime.of(fechaSeleccionada, newValue);
                    obtenerDisponibilidad(club, fechaHoraSeleccionada.toLocalDate());
                }
            });

            obtenerDisponibilidad(club, LocalDate.now());

        } catch (ClubDAOException ex) {
            Logger.getLogger(VerPistasController.class
                    .getName()).log(Level.SEVERE, null, ex);

        } catch (IOException ex) {
            Logger.getLogger(VerPistasController.class
                    .getName()).log(Level.SEVERE, null, ex);
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

    private WritableImage cambiarImagenPista(Image imagen) {
        int width = (int) imagen.getWidth();
        int height = (int) imagen.getHeight();
        double maxDistSquared = 0.4 * 0.4 * 441.67 * 441.67; // Máxima distancia permitida al cuadrado
        Color verde = Color.web("#55a357");
        Color rojo = Color.web("#fb4949");
        WritableImage nuevaImagen = new WritableImage(width, height);
        PixelReader pixelReader = imagen.getPixelReader();
        PixelWriter pixelWriter = nuevaImagen.getPixelWriter();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Color color = pixelReader.getColor(x, y);
                double distSquared = distanciaAlCuadrado(color, verde);
                if (distSquared <= maxDistSquared) {
                    pixelWriter.setColor(x, y, rojo);
                } else {
                    pixelWriter.setColor(x, y, color);
                }
            }
        }
        return nuevaImagen;
    }

    // Función auxiliar para calcular la distancia al cuadrado entre dos colores
    private static double distanciaAlCuadrado(Color c1, Color c2) {
        double rDist = (c1.getRed() - c2.getRed()) * 255;
        double gDist = (c1.getGreen() - c2.getGreen()) * 255;
        double bDist = (c1.getBlue() - c2.getBlue()) * 255;
        return rDist * rDist + gDist * gDist + bDist * bDist;
    }

    private void obtenerDisponibilidad(Club club, LocalDate fecha) {

        img_pista1.setImage(new Image("images/tennisCourt1.png"));
        img_pista2.setImage(new Image("images/tennisCourt2.png"));
        img_pista3.setImage(new Image("images/tennisCourt3.png"));
        img_pista4.setImage(new Image("images/tennisCourt4.png"));
        img_pista5.setImage(new Image("images/tennisCourt5.png"));
        img_pista6.setImage(new Image("images/tennisCourt6.png"));

        List<Court> courts = club.getCourts();

        //mostrar como disponible, posteriormente se marcaran como reservados aquellos que lo esten
        lbl_pista1.setText(courts.get(0).getName() + " disponible");
        lbl_pista2.setText(courts.get(1).getName() + " disponible");
        lbl_pista3.setText(courts.get(2).getName() + " disponible");
        lbl_pista4.setText(courts.get(3).getName() + " disponible");
        lbl_pista5.setText(courts.get(4).getName() + " disponible");
        lbl_pista6.setText(courts.get(5).getName() + " disponible");

        List<Booking> bookings = club.getForDayBookings(fecha);
        for (Booking booking : bookings) {
            String nombrePista = booking.getCourt().getName();
            String miembroReserva = booking.getMember().getNickName();
            //si la pista ya ha sido reservada a la hora seleccionada, marcarla como reservada
            if (booking.getFromTime().toString().equals(spinner.getValue().format(DateTimeFormatter.ofPattern("HH:mm")))) {

                // Actualizar la ImageView y Label correspondiente al nombre de la pista
                Pista pista = pistaMap.get(nombrePista);
                if (pista != null) {
                    pista.getLabel().setText(nombrePista + " reservada por " + miembroReserva);
                    pista.getImageView().setImage(cambiarImagenPista(pista.getImageView().getImage()));
                } else {
                    System.out.println(pista);
                }

            }

        }
    }

    @FXML
    private void reservarPista(ActionEvent event) {

        // Mostrar modal
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Alerta");
        alert.setHeaderText(null);
        alert.setContentText("Inicie sesión para llevar a cabo una reserva.");

        // Cambiar el texto del botón OK
        ButtonType loginButtonType = new ButtonType("Aceptar", ButtonBar.ButtonData.OK_DONE);
        alert.getButtonTypes().setAll(loginButtonType);

        // Agregar un evento de botón
        Button loginButton = (Button) alert.getDialogPane().lookupButton(loginButtonType);
        loginButton.setOnAction(e -> {
            alert.close();
        });
        alert.showAndWait();

    }

    class Pista {

        private final Label lbl;
        private final ImageView imgView;

        public Pista(ImageView imgView, Label lbl) {
            this.lbl = lbl;
            this.imgView = imgView;
        }

        public Label getLabel() {
            return lbl;
        }

        public ImageView getImageView() {
            return imgView;
        }

    }
}
