/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package javafxmlapplication.view;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafxmlapplication.controller.RegistroSocioController;
import javafxmlapplication.controller.VerPistasController;
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
    private ListView<String> lista;
    @FXML
    private Button btn_cancel;

    private Member member;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)  {
        try {
            // TODO
            
            Club club = Club.getInstance();
            
            
            List<Booking> reservas = club.getUserBookings(member.getNickName());
            ArrayList<String> reservasTexto = new ArrayList<String>();
            
            ObservableList<String> reserves = FXCollections.observableArrayList();
            
            for(int i = 0; i < 9; i++){
                reserves.add(i, reservas.get(i).toString());
                
            }
            lista.setItems(reserves);
        } catch (ClubDAOException ex) {
            Logger.getLogger(VerReservasController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception e) {} 
        
        
        
    }      

    private void setMember(Member mem){
        member = mem;
    }
    @FXML
    private void cancelar(ActionEvent event) {
        
    }
          
      
}
