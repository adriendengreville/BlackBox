package mainFrame;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import mainFrame.Main;
import mainFrame.Client;

public class Controleur {
	private Client client;
	
    @FXML
    private TableView<Client> personTable;
    @FXML
    private TableColumn<Client, String> pseudo;
    @FXML
    private TableColumn<Client, String> ip;

    @FXML
    private Label userLabel;
    
    @FXML
    public TextArea messageBox = new TextArea();
    @FXML
    private TextArea chatBox;

    // Reference to the main application.
    @SuppressWarnings("unused")
	private Main main;

    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    public Controleur() {
    }
    
    public void setMessageBox(String msg){
    	chatBox.setText(msg);
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        userLabel.setText("JK DE LA MONTAGNE");
    }
    
    @FXML
    private void sendClicked(){
    	String tmp = new String(chatBox.getText());
    	tmp += userLabel.getText() + " : ";
    	tmp += messageBox.getText() + "\n";
    	
    	chatBox.setText(tmp);
    	
    	messageBox.setText("");
    }
    
    @FXML
    private void connect(){
    	client = new Client();
    	client.connect();
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