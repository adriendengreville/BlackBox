/*
 * Description : Cette classe permet de contr�ler l'UI du serveur et du client
 */
package mainFrame;

import javax.swing.SwingUtilities;

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
		private Server server = null;
		
		//Attributs du client
	    @FXML	//le @FXML permet de faire comprendre � JavaFX que l'attribut ou m�thode suivant est directement li� � l'UI
	    private TextArea userField;
	    @FXML
	    public TextArea messageBox = new TextArea();
	    @FXML
	    private TextArea chatBox;
	    @FXML
	    private TextArea ipField;
	    @FXML
	    private TextArea mdpField;
	    
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
	        userField.setText("JK DE LA MONTAGNE");
	    }
	    
	    //Client-------------------------------------------------------------------------------------------------------------------
	    @FXML
	    private void sendClicked(){			//enregistre l'action du clic sur le bouton envoyer du chat
	    	String tmp = new String(chatBox.getText());
	    	tmp += userField.getText() + " : ";
	    	tmp += messageBox.getText() + "\n";
	    	
	    	chatBox.setText(tmp);
	    	
	    	messageBox.setText("");
	    	// just have to send the message
	    	client.sendMessage(new ChatMessage(ChatMessage.MESSAGE, chatBox.getText()));				

	    }//sentClicked
	    
	    @FXML
	    private void connect(){			//enregistre l'action du clic sur le bouton connecter � un r�seau
	    	// ok it is a connection request
	    	String username = userField.getText().trim();
	    	// empty username ignore it
	    	if(username.length() == 0)
	    	return;
	    	// empty serverAddress ignore it
	    	String server = ipField.getText().trim();
	    	if(server.length() == 0)
	    	return;
	    	// try creating a new Client with GUI
	    	client = new Client(server, 6969, username, this);
	    	// test if we can start the Client
	    	if(!client.start()) 
	    	return;
	    	chatBox.setText("");
	    }//connect
	    
	    @FXML							//enregistre le clic sur le bouton d�connecter
	    private void disconnect(){
	    	
	    }//disconnect
	    
	    @FXML
	    private void quit(){			//enregistre l'action du clic sur le bouton quitter
	    	//mettre ce qu'il faut pour ne pas planter le syst�me en cas d'arr�t de l'appli
	    	Platform.exit();
	    }
	    
	 // called by the Client to append text in the TextArea 
		void append(String str) {
			chatBox.appendText(str);
			chatBox.positionCaret(chatBox.getText().length() - 1);
		}
	    

	    //Serveur-------------------------------------------------------------------------------------------------------------------
	    @FXML
	    private void startServer(){		//enregistre le clic sur lancer
	    	Platform.runLater(new Runnable() {
		        @Override
		        public void run() {
		        	// if running we have to stop
					if(server != null) {
						server.stop();
						server = null;
						return;
					}
			      	// OK start the server
					// ceate a new Server
					server = new Server(6969);
					// and start it as a thread
					new ServerRunning().start();
			    	server = new Server(6969, null);
			    	server.start();
		        }
				});
	    	
	    }
	    
		/*
		 * A thread to run the Server
		 */
		class ServerRunning extends Thread {
			public void run() {
				Platform.runLater(new Runnable() {
		        @Override
		        public void run() {
		        	server.start();     // should execute until if fails
					// the server failed
					appendEvent("Server crashed\n");
					server = null;
		        }
				});
			}
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
	    
		void appendEvent(String str) {
			logs.appendText(str);
			logs.positionCaret(chatBox.getText().length() - 1);
			
		}
	}