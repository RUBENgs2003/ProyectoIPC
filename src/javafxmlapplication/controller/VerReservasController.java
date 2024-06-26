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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
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
    @FXML
    private TableColumn<Booking, String> col_fechaHoraSalida;

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
        tableView.setPlaceholder(new Label("No ha realizado ninguna reserva"));
    }

    void setMember(Member member) {
        this.member = member;
        lbl_nombreApellidos.setText(member.getName() + " " + member.getSurname());
        img_usuario.setImage(member.getImage());
    }

    @FXML
    private void cancelar(ActionEvent event) throws ClubDAOException, IOException {

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
        if (cancelable) {
            //mostrar modal de confirmacion
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Cancelar reserva");
            alert.setHeaderText(null);
            alert.setContentText("¿Seguro que quieres cancelar la reserva?");
            ButtonType botonCancelar = new ButtonType("Sí", ButtonBar.ButtonData.OK_DONE);
            ButtonType botonVolver = new ButtonType("Volver", ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(botonCancelar, botonVolver);
            alert.showAndWait();
            //cancelar la reserva
            if (alert.getResult().getButtonData().equals(botonCancelar.getButtonData())) {

                String txt_pagado = "\nNota: No se procederá a realizar el cobro.";
                Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
                alert2.setTitle("Reserva cancelada");
                alert2.setHeaderText(null);

                if (selectedBooking.getValue().getPaid()) {
                    alert2.setContentText("Ha cancelado la reserva con éxito." + txt_pagado);
                } else {
                    alert2.setContentText("Ha cancelado la reserva con éxito.");
                }
                alert2.setContentText("Ha cancelado la reserva con éxito.");

                // Agregar un ícono de éxito
                ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("/images/successIcon.png")));
                imageView.setFitHeight(64);
                imageView.setFitWidth(64);
                alert2.setGraphic(imageView);

                // Cambiar el texto del botón OK
                ButtonType loginButtonType = new ButtonType("Aceptar", ButtonBar.ButtonData.OK_DONE);
                alert2.getButtonTypes().setAll(loginButtonType);

                // Agregar un evento de botón
                Button loginButton = (Button) alert2.getDialogPane().lookupButton(loginButtonType);
                loginButton.setOnAction(e -> {
                    alert2.close();
                });
                alert2.showAndWait();
                //si está pagada se podría decir que se le va a devolver el dinero

                //eliminar reserva
                Booking delete = tableView.getSelectionModel().getSelectedItem();
                Club.getInstance().removeBooking(delete);
                cargarReservas();
            }

        } else {
            //mostrar error
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("No es posible cancelar una reserva con menos de 24 horas de antelación.");
            ButtonType boton = new ButtonType("Volver", ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(boton);
            alert.showAndWait();
        }
    }

    @FXML
    private void volver(ActionEvent event) throws IOException {
        //volver atras - ventana principal
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
        // Con root.prefWidth(-1) obtenemos el width predefinido
        stage.setMinWidth(root.prefWidth(-1) + 20);
        stage.setMinHeight(root.prefHeight(-1) + 40);

        stage.show();
    }

    void cargarReservas() throws ClubDAOException, IOException {

        Club club = Club.getInstance();
        List<Booking> bookings = club.getUserBookings(member.getNickName());

        // Crear una nueva lista modificable a partir de la lista original
        List<Booking> modifiableBookings = new ArrayList<>(bookings);

        // Ordenar la lista de bookings por fecha de reserva descendente (la más reciente primero)
        Collections.sort(modifiableBookings, Comparator.comparing(Booking::getBookingDate).reversed());

        // Obtener los 10 últimos bookings o menos, si hay menos de 10 bookings en total
        List<Booking> last10Bookings = modifiableBookings.stream()
                .limit(10)
                .collect(Collectors.toList());

        ObservableList<Booking> bookingList = FXCollections.observableArrayList(last10Bookings);

        col_nombrePista.setCellValueFactory(reserva -> new SimpleStringProperty(reserva.getValue().getCourt().getName()));
        col_fechaHoraEntrada.setCellValueFactory(reserva -> new SimpleStringProperty(reserva.getValue().getMadeForDay().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " " + reserva.getValue().getFromTime() + "h"));
        col_fechaReserva.setCellValueFactory(reserva -> new SimpleStringProperty(reserva.getValue().getBookingDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
        col_fechaHoraSalida.setCellValueFactory(reserva -> new SimpleStringProperty(reserva.getValue().getMadeForDay().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " " + reserva.getValue().getFromTime().plusMinutes(club.getBookingDuration()) + "h"));
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
