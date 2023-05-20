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
    private Button btn_actualizarDatos;
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

    Member member;
    Map<String, Pista> pistaMap = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        try {

            Club club = Club.getInstance();

            List<Court> courts = club.getCourts();
            //se sabe que el número de pistas es 6 (no se puede aumentar ni disminuir)
            pistaMap.put(courts.get(0).getName(), new Pista(courts.get(0).getName(), lbl_pista1, img_pista1, btn_pista1));
            pistaMap.put(courts.get(1).getName(), new Pista(courts.get(1).getName(), lbl_pista2, img_pista2, btn_pista2));
            pistaMap.put(courts.get(2).getName(), new Pista(courts.get(2).getName(), lbl_pista3, img_pista3, btn_pista3));
            pistaMap.put(courts.get(3).getName(), new Pista(courts.get(3).getName(), lbl_pista4, img_pista4, btn_pista4));
            pistaMap.put(courts.get(4).getName(), new Pista(courts.get(4).getName(), lbl_pista5, img_pista5, btn_pista5));
            pistaMap.put(courts.get(5).getName(), new Pista(courts.get(5).getName(), lbl_pista6, img_pista6, btn_pista6));

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

        //mostrar como disponible, posteriormente se marcaran como reservados aquellos que lo esten
        List<Court> courts = club.getCourts();

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
    private void verReservas(ActionEvent event) throws IOException, ClubDAOException {
        //COMPLETAR
        Stage currentStage = (Stage) lbl_pista1.getScene().getWindow();
        currentStage.close();

        FXMLLoader miCargador = new FXMLLoader(getClass().getResource("../view/verReservas.fxml"));
        Parent root = miCargador.load();
        VerReservasController controller = miCargador.getController();
        controller.setMember(member);
        controller.cargarReservas();
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Ver mis reservas - Club de tenis GreenBall");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(currentStage);
        stage.getIcons().add(new Image("images/greenball.png"));

        stage.show();

    }

    @FXML
    private void reservarPista(ActionEvent event) throws ClubDAOException, IOException {

        Button btn = (Button) event.getSource();
        String txtPagado = "";
        Boolean pistaLibre = true;
        Pista pistaSeleccionada = null;

        if (Club.getInstance().hasCreditCard(member.getNickName())) {
            txtPagado = "La reserva ha sido pagada a través de la tarjeta de crédito vinculada a su perfil.";
        }

        //registrar pista según el boton pulsado
        LocalDate fechaSeleccionada = datePicker.getValue();

        List<Booking> bookings = Club.getInstance().getForDayBookings(fechaSeleccionada);

        //MEJORABLE -> LAS PISTAS PUEDEN TENER OTRO NOMBRE Y ESTO NO FUNCIONARIA
        //obtener el nombre de la pista
        for (Pista pista : pistaMap.values()) {

            //obtener pista seleccionada
            if (pista.getButton() == btn) {
                pistaSeleccionada = pista;
            }

        }

        for (Booking booking : bookings) {
            //obtener si la pista seleccionada está reservada a la hora seleccionada
            if (booking.getCourt().getName().equals(pistaSeleccionada.getName())
                    && booking.getFromTime().toString().equals(spinner.getValue().format(DateTimeFormatter.ofPattern("HH:mm")))) {
                pistaLibre = false;
                break;
            }
        }

        if (pistaLibre) {

            // Obtener los datos de la reserva
            String pista = pistaSeleccionada.getName();
            int duracion = Club.getInstance().getBookingDuration();
            String fechaEntrada = fechaSeleccionada.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " " + LocalTime.parse(spinner.getValue().format(DateTimeFormatter.ofPattern("HH:mm"))) + "h";
            boolean pagado = Club.getInstance().hasCreditCard(member.getNickName()); // Si está pagado o no

            // Calcular la duración en horas y minutos
            int horas = duracion / 60;
            int minutos = duracion % 60;

            // Crear el mensaje con los datos de la reserva, incluyendo la duración en horas y minutos
            String duracionTexto = "";
            if (horas > 0) {
                duracionTexto += horas + " hora" + (horas > 1 ? "s" : "");
            }
            if (minutos > 0) {
                if (!duracionTexto.isEmpty()) {
                    duracionTexto += " ";
                }
                duracionTexto += minutos + " minuto" + (minutos > 1 ? "s" : "");
            }

            // Crear el mensaje con los datos de la reserva
            String mensaje = "Datos de la reserva:\n\n"
                    + "Nombre de la pista: " + pista + "\n"
                    + "Duración: " + duracionTexto + "\n"
                    + "Hora de entrada: " + fechaEntrada + "\n\n"
                    + "AVISO: " + (pagado ? "El pago se llevará a cabo a través de la tarjeta de crédito vinculada a su perfil"
                            : "No ha proporcionado ninguna tarjeta de crédito, por lo que deberá pagar la reserva posteriormente. Puede proporcionar una a través del botón 'actualizar datos'");

            // Mostrar el modal de confirmación
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Confirmación de reserva");
            confirmationAlert.setHeaderText(null);
            confirmationAlert.setContentText("Está a punto de efectuar su reserva, confírmela para llevarla a cabo: " + "\n\n" + mensaje);

            // Configurar los botones del modal
            ButtonType cancelButton = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
            ButtonType confirmButton = new ButtonType("Confirmar reserva", ButtonBar.ButtonData.OK_DONE);
            confirmationAlert.getButtonTypes().setAll(cancelButton, confirmButton);

            // Obtener el botón de confirmar y asignarle un evento
            Button confirmBtn = (Button) confirmationAlert.getDialogPane().lookupButton(confirmButton);
            confirmBtn.setOnAction(e -> {
                // Realizar acciones adicionales al confirmar la reserva
                confirmationAlert.close();
            });

            // Mostrar el modal y esperar la respuesta
            confirmationAlert.showAndWait();

            // Verificar la opción seleccionada
            if (confirmationAlert.getResult() == confirmButton) {

                //RESERVAR PISTA 
                Court selectedCourt = Club.getInstance().getCourt(pistaSeleccionada.getName());
                Club.getInstance().registerBooking(LocalDateTime.now(), fechaSeleccionada, LocalTime.parse(spinner.getValue().format(DateTimeFormatter.ofPattern("HH:mm"))), Club.getInstance().hasCreditCard(member.getNickName()), selectedCourt, member);
                obtenerDisponibilidad(Club.getInstance(), fechaSeleccionada);

                // El usuario confirmó la reserva
                // Mostrar modal
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Estado de la reserva");
                alert.setHeaderText(null);
                alert.setContentText("Ha reservado la pista '" + pistaSeleccionada.getName() + "' satisfactoriamente.\n" + txtPagado);

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

            }

        } else {

            // Mostrar modal
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Estado de la reserva");
            alert.setHeaderText(null);
            alert.setContentText("La pista ya ha sido reservada para la fecha seleccionada.");

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

    class Pista {

        private final Label lbl;
        private final ImageView imgView;
        private final String nombre;
        private final Button btn;

        public Pista(String nombre, Label lbl, ImageView imgView, Button btn) {
            this.lbl = lbl;
            this.imgView = imgView;
            this.nombre = nombre;
            this.btn = btn;
        }

        public Pista() {
            this.lbl = null;
            this.imgView = null;
            this.nombre = null;
            this.btn = null;
        }

        public Label getLabel() {
            return lbl;
        }

        public ImageView getImageView() {
            return imgView;
        }

        public String getName() {
            return nombre;
        }

        public Button getButton() {
            return btn;
        }

    }

}
