/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxmlapplication;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;


public class JavaFXMLApplication extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        
        //Club.getInstance().setInitialData(); //ESTO ES PARA QUE SE BORREN TODOS LOS DATOS EN LA BD Y PODER HACER PRUEBAS

        //======================================================================
        // 1- creación del grafo de escena a partir del fichero FXML
        FXMLLoader loader= new  FXMLLoader(getClass().getResource("view/verPistas.fxml"));
        Parent root = loader.load();
        //======================================================================
        // 2- creación de la escena con el nodo raiz del grafo de escena
        Scene scene = new Scene(root);
        //======================================================================
        // 3- asiganación de la escena al Stage que recibe el metodo 
        //     - configuracion del stage
        //     - se muestra el stage de manera no modal mediante el metodo show()
        stage.setScene(scene);
        stage.setTitle("Disponibilidad de las pistas - Club de Tenis GreenBall");
        stage.getIcons().add(new Image("images/greenball.png"));
        // Con root.prefWidth(-1) obtenemos el width predefinido
        stage.setMinWidth(root.prefWidth(-1) + 20);
        stage.setMinHeight(root.prefHeight(-1) + 40);
        stage.show();
    } 

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
        
    }


    
}
