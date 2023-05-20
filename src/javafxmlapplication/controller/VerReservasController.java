/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package javafxmlapplication.controller;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Booking;
import model.Club;
import model.ClubDAOException;
import model.Member;

/**
 * FXML Controller class
 *
 * @author X
 */
public class VerReservasController implements Initializable {

    @FXML
    private Button btn_cancel;

    Member member;
    @FXML
    private Button btn_volver;
    @FXML
    private TableView<Booking> tableView;
    @FXML
    private TableColumn<Booking, String> col_nombrePista;
    @FXML
    private TableColumn<Booking, String> col_fechaHoraEntrada;
    @FXML
    private TableColumn<Booking, String> col_fechaReserva;
    @FXML
    private TableColumn<Booking, String> col_estadoReserva;
    @FXML
    private ImageView img_usuario;
    @FXML
    private Label lbl_nombreApellidos;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Enlace de la propiedad selectedItem a una propiedad ObservableValue<Booking>
        ReadOnlyObjectProperty<Booking> selectedBooking = tableView.getSelectionModel().selectedItemProperty();
        btn_cancel.disableProperty().setValue(Boolean.TRUE);
        // Agregar un listener a la propiedad selectedBooking para detectar cambios de selección
        selectedBooking.addListener((obs, oldSelection, newSelection) -> {
            btn_cancel.disableProperty().setValue(newSelection == null);
        });
    }

    void setMember(Member member) {
        this.member = member;
        lbl_nombreApellidos.setText(member.getName() + " " + member.getSurname());
        //COMPLETAR - poner la imagen del usuario
        //...
    }

    @FXML
    private void cancelar(ActionEvent event) {

        //COMPLETAR - Mostrar modal de confirmación tipo: ¿Estás seguro de cancelar la reserva tal tal tal?
        //Solo se puede reservar con 24 horas de antelacion minimo
        ReadOnlyObjectProperty<Booking> selectedBooking = tableView.getSelectionModel().selectedItemProperty();
        LocalDate dia = selectedBooking.getValue().getMadeForDay();
        LocalTime hora = selectedBooking.getValue().getFromTime();

        // Combinar la fecha y hora para obtener el LocalDateTime específico
        LocalDateTime specificDateTime = LocalDateTime.of(dia, hora);

        // Calcular la duración entre el LocalDateTime específico y la fecha y hora actual
        Duration duration = Duration.between(LocalDateTime.now(), specificDateTime);

        // Comparar si la duracion es mayor o igual a 24 horas
        
        boolean cancelable = duration.toHours() >= 24;
        System.out.println(cancelable);
        if (cancelable) {
            //CANCELAR RESERVA
            //mostrar modal de confirmacion
            //cancelar la reserva
        } else {
            //mostrar alerta/error
        }
    }

    @FXML
    private void volver(ActionEvent event) throws IOException {
        Stage currentStage = (Stage) btn_volver.getScene().getWindow();
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

    void cargarReservas() throws ClubDAOException, IOException {

        List<Booking> bookings = Club.getInstance().getUserBookings(member.getNickName());
        ObservableList<Booking> bookingList = FXCollections.observableArrayList(bookings);

        col_nombrePista.setCellValueFactory(reserva -> new SimpleStringProperty(reserva.getValue().getCourt().getName()));
        col_fechaHoraEntrada.setCellValueFactory(reserva -> new SimpleStringProperty(reserva.getValue().getMadeForDay().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " " + reserva.getValue().getFromTime() + "h"));
        col_fechaReserva.setCellValueFactory(reserva -> new SimpleStringProperty(reserva.getValue().getBookingDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
        col_estadoReserva.setCellValueFactory(reserva -> {
            if (reserva.getValue().getPaid()) {
                return new SimpleStringProperty("Pagada");
            } else {
                return new SimpleStringProperty("No pagada");
            }
        });

        tableView.setItems(bookingList);

    }

}
