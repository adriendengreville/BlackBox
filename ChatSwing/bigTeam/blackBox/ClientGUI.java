package blackBox;
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

import org.jdesktop.xswingx.PromptSupport;

import java.awt.Font;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;


public class ClientGUI extends JFrame implements ActionListener {

	private JFrame frame;
	private JTextField textField;
	private JTextField pseudoField;
	private JTextArea chatBox;
	private JTextArea messageBox;
	private JTextField ipField;
	private JPasswordField passwordField;

	private JButton sendButton, quitNetwork, joinNetwork;
	// if it is for connection
	private boolean connected;
	// the Client object
	private Client client;
	private JLabel labelImgPseudo;
	private JLabel labelTexteIP;
	private JLabel labelImageIP;
	private JLabel labelTxtPass;
	private JLabel labelIMGPass;
	
	Font tmpFont = null;
	Font customLabels = null;
	Font customTitle = null;

	int pX, pY;

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
			chatBox.setCaretPosition(chatBox.getDocument().getLength());
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
				ipField.setEditable(true);
				pseudoField.setEditable(true);
				passwordField.setEditable(true);
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
				String username = pseudoField.getText().trim();
				// empty username ignore it
				if(username.length() == 0 || username.contains(" ")){
					append("Nom d'utilisateur nul ou contenant un espace \n");
					return;
				}
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
				
				ipField.setEditable(false);
				pseudoField.setEditable(false);
				passwordField.setEditable(false);
			}

		}
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		//CUSTOM FONTS START
		InputStream is = ClientGUI.class.getResourceAsStream("comesinhandy.ttf");
		try {
			tmpFont = Font.createFont(Font.TRUETYPE_FONT, is);
		} catch (FontFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		customLabels = tmpFont.deriveFont(Font.ITALIC, 30);
		
		is = ClientGUI.class.getResourceAsStream("Sertig.otf");
		try {
			tmpFont = Font.createFont(Font.TRUETYPE_FONT, is);
		} catch (FontFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		customTitle = tmpFont.deriveFont(Font.BOLD, 40);

		//CUSTOM FONTS END
		
		frame = new JFrame();
		frame.getContentPane().setBackground(Color.decode("#1d1d1d"));
		frame.getContentPane().setLayout(null);
		frame.setUndecorated(true);
		
		//DRAG IT START
		frame.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent me) {
				// Get x,y and store them
				pX = me.getX();
				pY = me.getY();

			}

			public void mouseDragged(MouseEvent me) {

				frame.setLocation(frame.getLocation().x + me.getX() - pX,
						frame.getLocation().y + me.getY() - pY);
			}
		});

		frame.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent me) {

				frame.setLocation(frame.getLocation().x + me.getX() - pX,
						frame.getLocation().y + me.getY() - pY);
			}
		});
		
		messageBox = new JTextArea();
		messageBox.setBackground(UIManager.getColor("ComboBox.background"));
		messageBox.setBounds(10, 473, 645, 46);
		frame.getContentPane().add(messageBox);
		
		sendButton = new JButton("Envoyer");
		sendButton.setBackground(Color.decode("#1d1d1d"));
		sendButton.setForeground(new Color(239,244,255));
		sendButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		sendButton.setBounds(667, 473, 117, 46);
		frame.getContentPane().add(sendButton);
		
		quitNetwork = new JButton("Quitter le r\u00E9seau");
		quitNetwork.setBackground(Color.decode("#1d1d1d"));
		quitNetwork.setForeground(new Color(239,244,255));
		quitNetwork.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		quitNetwork.setBounds(633, 123, 151, 35);
		frame.getContentPane().add(quitNetwork);
		
		joinNetwork = new JButton("Rejoindre un r\u00E9seau");
		joinNetwork.setBackground(Color.decode("#1d1d1d"));
		joinNetwork.setForeground(new Color(239,244,255));
		joinNetwork.setBounds(472, 123, 151, 35);
		frame.getContentPane().add(joinNetwork);
		
		passwordField = new JPasswordField(10);
		
		//Text Area for messages
		pseudoField = new JTextField();
		PromptSupport.setPrompt("Pseudo", pseudoField);
		pseudoField.setBounds(10, 123, 141, 35);
		frame.getContentPane().add(pseudoField);
		pseudoField.setColumns(10);
		
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
		lblBlackboxProject.setFont(customTitle);
		lblBlackboxProject.setForeground(new Color(239,244,255));
		lblBlackboxProject.setBounds(292, 20, 256, 50);
		frame.getContentPane().add(lblBlackboxProject);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 169, 774, 293);
		frame.getContentPane().add(scrollPane);
		
		chatBox = new JTextArea();
		chatBox.setCaretPosition(chatBox.getText().length());
		chatBox.setDisabledTextColor(Color.BLACK);
		chatBox.setSelectionColor(Color.BLACK);
		chatBox.setSelectedTextColor(Color.BLACK);
		chatBox.setEnabled(false);
		chatBox.setEditable(false);
		chatBox.setFocusable(false);
		scrollPane.setViewportView(chatBox);
		chatBox.setBackground(UIManager.getColor("ComboBox.background"));
		
		JLabel labelTextePseudo = new JLabel("Entrez un nom d'utilisateur");
		labelTextePseudo.setFont(customLabels);
		labelTextePseudo.setForeground(new Color(239,244,255));
		labelTextePseudo.setBounds(10, 49, 215, 41);
		frame.getContentPane().add(labelTextePseudo);
		
		labelImgPseudo = new JLabel("");
		labelImgPseudo.setIcon(new ImageIcon(ClientGUI.class.getResource("/blackBox/arrow.png")));
		labelImgPseudo.setBounds(54, 85, 50, 30);
		frame.getContentPane().add(labelImgPseudo);
		
		labelTexteIP = new JLabel("Une adresse");
		labelTexteIP.setForeground(new Color(239,244,255));
		labelTexteIP.setFont(customLabels);
		labelTexteIP.setBounds(217, 75, 100, 30);
		frame.getContentPane().add(labelTexteIP);
		
		labelImageIP = new JLabel("");
		labelImageIP.setAlignmentX(Component.CENTER_ALIGNMENT);
		labelImageIP.setIcon(new ImageIcon(ClientGUI.class.getResource("/blackBox/arrowFlip.png")));
		labelImageIP.setBounds(175, 94, 58, 27);
		frame.getContentPane().add(labelImageIP);
		
		labelTxtPass = new JLabel("Et un mot de passe");
		labelTxtPass.setForeground(new Color(239,244,255));
		labelTxtPass.setFont(customLabels);
		labelTxtPass.setBounds(368, 75, 161, 30);
		frame.getContentPane().add(labelTxtPass);
		
		labelIMGPass = new JLabel("");
		labelIMGPass.setIcon(new ImageIcon(ClientGUI.class.getResource("/blackBox/arrowFlip.png")));
		labelIMGPass.setAlignmentX(0.5f);
		labelIMGPass.setBounds(325, 94, 58, 27);
		frame.getContentPane().add(labelIMGPass);
		
		JButton closeButton = new JButton("");
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (connected)
					client.sendMessage(new ChatMessage(ChatMessage.LOGOUT, ""));
				dispose();
				System.exit(1);
			}
		});
		closeButton.setIcon(new ImageIcon(ClientGUI.class.getResource("/blackBox/off.png")));
		closeButton.setBounds(750, 20, 40, 40);
		closeButton.setBorder(BorderFactory.createEmptyBorder());
		closeButton.setContentAreaFilled(false);

		
		frame.getContentPane().add(closeButton);
		frame.setResizable(false);
		frame.setBounds(100, 100, 800, 530);
		frame.setBackground(UIManager.getColor("FormattedTextField.inactiveForeground"));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}