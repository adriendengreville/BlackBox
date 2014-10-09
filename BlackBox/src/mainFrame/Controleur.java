/*
 * Description : Cette classe permet de contr�ler l'UI du serveur et du client
 */
package mainFrame;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import mainFrame.Main;
import mainFrame.Client;

public class Controleur {
	//ATTRIBUTS
		private Client client;
		
		//Attributs du client
	    @FXML	//le @FXML permet de faire comprendre � JavaFX que l'attribut ou m�thode suivant est directement li� � l'UI
	    private Label userLabel;
	    @FXML
	    public TextArea messageBox = new TextArea();
	    @FXML
	    private TextArea chatBox;
	
	    //Attributs du serveur
	    @FXML
	    private TextArea serverName;
	    @FXML
	    private TextArea serverPwd;
	    @FXML
	    private TableView<Client> userTable;
	    @FXML
	    private TableColumn<Client, String> pseudos;
	    @FXML
	    private TableColumn<Client, String> ips;
	    @FXML
	    private TextArea logs;
	    // Reference to the main application.
	    @SuppressWarnings("unused")
		private Main main;
	
	//M�THODES
	    /**
	     * The constructor.
	     * The constructor is called before the initialize() method.
	     */
	    public Controleur() {
	    	
	    }//ControleurCSTR
	    
	    /**
	     * Initializes the controller class. This method is automatically called
	     * after the fxml file has been loaded.
	     */
	    @FXML
	    private void initialize() {
	        userLabel.setText("JK DE LA MONTAGNE");
	    }
	    
	    //Client-------------------------------------------------------------------------------------------------------------------
	    @FXML
	    private void sendClicked(){			//enregistre l'action du clic sur le bouton envoyer du chat
	    	String tmp = new String(chatBox.getText());
	    	tmp += userLabel.getText() + " : ";
	    	tmp += messageBox.getText() + "\n";
	    	
	    	chatBox.setText(tmp);
	    	
	    	messageBox.setText("");
	    }//sentClicked
	    
	    @FXML
	    private void connect(){			//enregistre l'action du clic sur le bouton connecter � un r�seau
	    	client = new Client(null, 0, null);
	    	client.start();
	    }//connect
	    
	    @FXML							//enregistre le clic sur le bouton d�connecter
	    private void disconnect(){
	    	
	    }//disconnect
	    
	    @FXML
	    private void quit(){			//enregistre l'action du clic sur le bouton quitter
	    	//mettre ce qu'il faut pour ne pas planter le syst�me en cas d'arr�t de l'appli
	    	Platform.exit();
	    }
	    

	    //Serveur-------------------------------------------------------------------------------------------------------------------
	    @FXML
	    private void startServer(){		//enregistre le clic sur lancer
	    	
	    }
	    /**
	     * Is called by the main application to give a reference back to itself.
	     * 
	     * @param mainApp
	     */
	    public void setMain(Main main) {
	        this.main = main;
	
	        //userLabel.setText("JK DE LA MONTAGNE");
	    }
	}