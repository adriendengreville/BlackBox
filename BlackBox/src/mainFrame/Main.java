/*
 * Description : Cette classe permet de construire l'interface et de démarrer le programme.
 * Il n'y a normalement rien à changer ici.
 */

package mainFrame;

import java.io.IOException;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {
	//ATTRIBUTS
	    private Stage primaryStage;
	    private BorderPane rootLayout;
	    
	    private double xOffset = 0;
	    private double yOffset = 0;
	   
	//MÉTHODES
	    @Override
	    public void start(Stage primaryStage) {
	        this.primaryStage = primaryStage;
	        this.primaryStage.setTitle("Black Box");
	        this.primaryStage.setResizable(false);
	        this.primaryStage.setHeight(530);
	        this.primaryStage.setWidth(800);
	        this.primaryStage.initStyle(StageStyle.UNDECORATED);
	        initRootLayout();
	
	        showPersonOverview();
	    }
	
	    /**
	     * Initializes the root layout.
	     */
	    public void initRootLayout() {
	        try {
	            // Load root layout from fxml file.
	            FXMLLoader loader = new FXMLLoader();
	            loader.setLocation(Main.class.getResource("Fenetre.fxml"));
	            rootLayout = (BorderPane) loader.load();
	
	            rootLayout.setOnMousePressed(new EventHandler<MouseEvent>() {	//les deux prochaines procédures permettent de déplacer la fenetre sans avoir de bordure

					@Override
					public void handle(MouseEvent event) {
						xOffset = event.getSceneX();
						yOffset = event.getSceneY();
					}
				});
	            
	            rootLayout.setOnMouseDragged(new EventHandler<MouseEvent>() {

					@Override
					public void handle(MouseEvent event) {
						primaryStage.setX(event.getScreenX() - xOffset);
						primaryStage.setY(event.getScreenY() - yOffset);
					}
				});
	            
	            // Show the scene containing the root layout.
	            Scene scene = new Scene(rootLayout);
	            primaryStage.setScene(scene);
	            primaryStage.show();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	
	    /**
	     * Shows the person overview inside the root layout.
	     */
	    public void showPersonOverview() {
	        try {
	            // Load person overview.
	            FXMLLoader loader = new FXMLLoader();
	            loader.setLocation(Main.class.getResource("design.fxml"));
	            AnchorPane personOverview = (AnchorPane) loader.load();
	
	            // Set person overview into the center of root layout.
	            rootLayout.setCenter(personOverview);
	            
	            Controleur controller= loader.getController();
	            controller.setMain(this);
	            
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	
	    /**
	     * Returns the main stage.
	     * @return
	     */
	    public Stage getPrimaryStage() {
	        return primaryStage;
	    }
	
	    public static void main(String[] args) {
	        launch(args);
	    }
}