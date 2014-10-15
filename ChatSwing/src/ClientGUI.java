import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

import org.jdesktop.xswingx.PromptSupport;

import java.awt.Font;
import java.util.Arrays;


public class ClientGUI extends JFrame implements ActionListener {

	private JFrame frame;
	private JTextField textField;
	private JTextField txtF;
	private JTextArea chatBox;
	private JTextArea messageBox;
	private JTextField ipField;
	private JPasswordField passwordField;

	private JButton sendButton, quitNetwork, joinNetwork;
	// if it is for connection
	private boolean connected;
	// the Client object
	private Client client;
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientGUI window = new ClientGUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	public String getPassword(){
		return passwordField.getText();
	}
	/**
	 * Create the application.
	 */
	public ClientGUI() {
		initialize();
		joinNetwork.addActionListener(this);
		quitNetwork.addActionListener(this);
		passwordField.addActionListener(this);
		
	}
	// called by the Client to append text in the TextArea 
		void append(String str) {
			chatBox.append(str);
			chatBox.setCaretPosition(txtF.getText().length() - 1);
		}
		
		// called by the GUI is the connection failed
		// we reset our buttons, label, textfield
		void connectionFailed() {
			sendButton.removeActionListener(this);
			connected = false;
		}

		/*
		* Button or JTextField clicked
		*/
		public void actionPerformed(ActionEvent e) {
		    
			Object o = e.getSource();
			// if it is the Logout button
			if(o == quitNetwork) {
				client.sendMessage(new ChatMessage(ChatMessage.LOGOUT, ""));
				return;
			}
			// ok it is coming from the JTextField
			if(connected) {
				// just have to send the message
				client.sendMessage(new ChatMessage(ChatMessage.MESSAGE, messageBox.getText()));				
				messageBox.setText("");
				return;
			}
			

			if(o == joinNetwork) {
				// ok it is a connection request
				String username = txtF.getText().trim();
				// empty username ignore it
				if(username.length() == 0)
					return;
				// empty serverAddress ignore it
				String server = ipField.getText().trim();
				if(server.length() == 0)
					return;
				// empty or invalid port numer, ignore it
				String portNumber = "1664";
				if(portNumber.length() == 0)
					return;
				int port = 0;
				try {
					port = Integer.parseInt(portNumber);
				}
				catch(Exception en) {
					return;   // nothing I can do if port number is not valid
				}
				// try creating a new Client with GUI
				client = new Client(server, port, username, this);
				// test if we can start the Client
				if(!client.start()) 
					return;
				messageBox.setText("");
				connected = true;
				// Action listener for when the user enter a message
				sendButton.addActionListener(this);
			}

		}
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.getContentPane().setBackground(Color.BLACK);
		frame.getContentPane().setLayout(null);
		
		messageBox = new JTextArea();
		messageBox.setBackground(UIManager.getColor("ComboBox.background"));
		messageBox.setBounds(10, 444, 645, 58);
		frame.getContentPane().add(messageBox);
		
		chatBox = new JTextArea();
		chatBox.setBackground(UIManager.getColor("ComboBox.background"));
		chatBox.setBounds(10, 165, 784, 267);
		frame.getContentPane().add(chatBox);
		
		sendButton = new JButton("Envoyer");
		sendButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		sendButton.setBounds(667, 444, 127, 58);
		frame.getContentPane().add(sendButton);
		
		quitNetwork = new JButton("Quitter le réseau");
		quitNetwork.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		quitNetwork.setBounds(639, 124, 151, 35);
		frame.getContentPane().add(quitNetwork);
		
		joinNetwork = new JButton("Rejoindre un réseau");
		joinNetwork.setBounds(482, 124, 151, 35);
		frame.getContentPane().add(joinNetwork);
		
		passwordField = new JPasswordField(10);
		
		//Text Area for messages
		txtF = new JTextField();
		PromptSupport.setPrompt("Pseudo", txtF);
		txtF.setBounds(10, 123, 141, 35);
		frame.getContentPane().add(txtF);
		txtF.setColumns(10);
		
		//Ip Field of network
		ipField = new JTextField();
		PromptSupport.setPrompt("IP", ipField);
		ipField.setColumns(10);
		ipField.setBounds(151, 123, 141, 35);
		frame.getContentPane().add(ipField);
		
		//Password Field of network
		passwordField = new JPasswordField();
		passwordField.setBounds(292, 123, 136, 35);
		frame.getContentPane().add(passwordField);
		
		JLabel lblBlackboxProject = new JLabel("Blackbox Project");
		lblBlackboxProject.setFont(new Font("Lucida Grande", Font.PLAIN, 30));
		lblBlackboxProject.setForeground(UIManager.getColor("CheckBoxMenuItem.selectionForeground"));
		lblBlackboxProject.setBounds(245, 6, 256, 50);
		frame.getContentPane().add(lblBlackboxProject);
		frame.setResizable(false);
		frame.setBounds(100, 100, 800, 530);
		frame.setBackground(UIManager.getColor("FormattedTextField.inactiveForeground"));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}