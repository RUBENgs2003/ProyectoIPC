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
import java.util.List;
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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import model.Booking;
import model.Club;
import model.ClubDAOException;
import model.Court;
import model.Member;

/**
 *
 * @author ruben
 */
public class VerPistasAutenticadoController implements Initializable {

    @FXML
    private GridPane gridPane;
    @FXML
    private Text lbl_fechaHora;
    @FXML
    private HBox gridPaneHbox;
    @FXML
    private DatePicker datePicker;
    @FXML
    private Spinner<LocalTime> spinner;
    @FXML
    private Label lbl_pista1;
    @FXML
    private ImageView img_pista1;
    @FXML
    private Label lbl_pista2;
    @FXML
    private ImageView img_pista2;
    @FXML
    private Label lbl_pista3;
    @FXML
    private ImageView img_pista3;
    @FXML
    private Label lbl_pista4;
    @FXML
    private ImageView img_pista4;
    @FXML
    private Label lbl_pista5;
    @FXML
    private ImageView img_pista5;
    @FXML
    private Label lbl_pista6;
    @FXML
    private ImageView img_pista6;
    @FXML
    private ImageView imgView_imagenMiembro;
    @FXML
    private Text text_nombreApellidos;
    @FXML
    private Button btn_verReservas;
    @FXML
    private Button btn_reservarPista;
    @FXML
    private Button btn_actualizarDatos;

    Member member;
    @FXML
    private Button btn_pista1;
    @FXML
    private Button btn_pista2;
    @FXML
    private Button btn_pista3;
    @FXML
    private Button btn_pista4;
    @FXML
    private Button btn_pista5;
    @FXML
    private Button btn_pista6;

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

            // Crear una fábrica de celdas de día personalizada
            datePicker.setDayCellFactory(picker -> new DateCell() {
                @Override
                public void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);
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
        lbl_pista1.setText("Pista 1 disponible");
        lbl_pista2.setText("Pista 2 disponible");
        lbl_pista3.setText("Pista 3 disponible");
        lbl_pista4.setText("Pista 4 disponible");
        lbl_pista5.setText("Pista 5 disponible");
        lbl_pista6.setText("Pista 6 disponible");

        List<Booking> bookings = club.getForDayBookings(fecha);
        for (Booking booking : bookings) {
            String pista = booking.getCourt().getName();
            String miembroReserva = booking.getMember().getNickName();
            //si la pista ya ha sido reservada a la hora seleccionada, marcarla como reservada
            if (booking.getFromTime().toString().equals(spinner.getValue().format(DateTimeFormatter.ofPattern("HH:mm")))) {
                System.out.println(booking.getCourt().getName() + " reservada por " + booking.getMember().getNickName());
                //MEJORABLE. LAS PISTAS PUEDEN TENER OTRO NOMBRE Y ESTO NO FUNCIONARIA
                switch (pista) {
                    
                    case "Pista 1":
                        lbl_pista1.setText("Pista 1 reservada por " + miembroReserva);
                        img_pista1.setImage(cambiarImagenPista(img_pista1.getImage()));
                        break;
                    case "Pista 2":
                        lbl_pista2.setText("Pista 2 reservada por " + miembroReserva);
                        img_pista2.setImage(cambiarImagenPista(img_pista2.getImage()));
                        break;
                    case "Pista 3":
                        lbl_pista3.setText("Pista 3 reservada por " + miembroReserva);
                        img_pista3.setImage(cambiarImagenPista(img_pista3.getImage()));
                        break;
                    case "Pista 4":
                        lbl_pista4.setText("Pista 4 reservada por " + miembroReserva);
                        img_pista4.setImage(cambiarImagenPista(img_pista4.getImage()));
                        break;
                    case "Pista 5":
                        lbl_pista5.setText("Pista 5 reservada por " + miembroReserva);
                        img_pista5.setImage(cambiarImagenPista(img_pista5.getImage()));
                        break;
                    case "Pista 6":
                        lbl_pista6.setText("Pista 6 reservada por " + miembroReserva);
                        img_pista6.setImage(cambiarImagenPista(img_pista6.getImage()));
                        break;
                    default:
                        System.out.println(pista);
                        break;
                }
            }

        }
    }

    @FXML
    private void verReservas(ActionEvent event) {
        //COMPLETAR
    }

    @FXML
    private void reservarPista(ActionEvent event) throws ClubDAOException, IOException {

        Button btn = (Button) event.getSource();
        String numPista = "";
        String txtPagado = "";
        Boolean pistaLibre = true;

        if (Club.getInstance().hasCreditCard(member.getNickName())) {
            txtPagado = "La reserva ha sido pagada a través de la tarjeta de crédito vinculada a su perfil.";
        }

        //registrar pista según el boton pulsado
        LocalDate fechaSeleccionada = datePicker.getValue();

        List<Booking> bookings = Club.getInstance().getForDayBookings(fechaSeleccionada);
        
        //MEJORABLE -> LAS PISTAS PUEDEN TENER OTRO NOMBRE Y ESTO NO FUNCIONARIA
        String id = btn.getId();
        switch (id) {
            case "btn_pista1":
                numPista = "1";
                break;
            case "btn_pista2":
                numPista = "2";
                break;
            case "btn_pista3":
                numPista = "3";
                break;
            case "btn_pista4":
                numPista = "4";
                break;
            case "btn_pista5":
                numPista = "5";
                break;
            case "btn_pista6":
                numPista = "6";
                break;
            default:
                break;
        }

        for (Booking booking : bookings) {
            //obtewner si la pista seleccionada está reservada a la hora seleccionada
            if (booking.getCourt().getName().equals("Pista " + numPista) && 
                    booking.getFromTime().toString().equals(spinner.getValue().format(DateTimeFormatter.ofPattern("HH:mm")))) {
                pistaLibre = false;
                break;
            }
        }

        //se podria mejorar añadiendo un modal de confirmacion, etc
        if (pistaLibre) {
            // Mostrar modal
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Estado de la reserva");
            alert.setHeaderText(null);
            alert.setContentText("Ha reservado la pista " + numPista + " satisfactoriamente." + txtPagado);

            // Agregar un ícono de éxito
            ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("/images/successIcon.png")));
            imageView.setFitHeight(64);
            imageView.setFitWidth(64);
            alert.setGraphic(imageView);

            // Cambiar el texto del botón OK
            ButtonType loginButtonType = new ButtonType("Aceptar", ButtonBar.ButtonData.OK_DONE);
            alert.getButtonTypes().setAll(loginButtonType);

            // Agregar un evento de botón
            Button loginButton = (Button) alert.getDialogPane().lookupButton(loginButtonType);
            loginButton.setOnAction(e -> {
                alert.close();
            });
            alert.showAndWait(); 

            Court selectedCourt = Club.getInstance().getCourt("Pista " + numPista);
            Club.getInstance().registerBooking(LocalDateTime.now(), fechaSeleccionada, spinner.getValue(), Club.getInstance().hasCreditCard(member.getNickName()), selectedCourt, member);
            //refrescar las pistas - no funciona. No se muestra la pista reservada hasta que no vuelves a iniciar la app. 
            //No se si es fallo de la base de datos o de que
            obtenerDisponibilidad(Club.getInstance(), fechaSeleccionada);
            
        } else {

            // Mostrar modal
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Estado de la reserva");
            alert.setHeaderText(null);
            alert.setContentText("La pista ya ha sido reservada por otro usuario para la fecha seleccionada.");

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

    }

    @FXML
    private void actualizarDatos(ActionEvent event) throws IOException {
        Stage currentStage = (Stage) btn_actualizarDatos.getScene().getWindow();
        currentStage.close();

        FXMLLoader miCargador = new FXMLLoader(getClass().getResource("../view/actualizarDatos.fxml"));
        Parent root = miCargador.load();
        ActualizarDatosController controller = miCargador.getController();
        controller.setMemberInfo(member);
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Registrar socio - Club de tenis GreenBall");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(currentStage);
        stage.getIcons().add(new Image("images/greenball.png"));

        stage.show();
    }

    void setMemberInfo(Member member) {
        this.member = member;
        text_nombreApellidos.setText(member.getName() + " " + member.getSurname());
        //COMPLETAR - PONER IMAGEN DEL USUARIO

    }

}
