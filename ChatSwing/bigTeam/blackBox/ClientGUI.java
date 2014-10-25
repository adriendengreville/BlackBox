package blackBox;
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.Font;
import java.io.IOException;
import java.io.InputStream;


public class ClientGUI extends JFrame implements ActionListener {
//ATTRIBUTS------------------------------------------------------------------------------------------------
	private JFrame frame;									//la fenètre principale
	
	private JTextField pseudoField;							//le champ pour entrer le nom d'utilisateur
	private JTextField ipField;								//le champ pour entrer l'adresse
	private JPasswordField passwordField;					//le champ pour entrer le mot de passe
	private JTextArea messageBox;							//le champ pour écrire les messages
	private JTextArea chatBox;								//le champ pour afficher les messages

	private JButton sendButton, quitNetwork, joinNetwork;	//les boutons pour interagir

	private boolean connected;								//la variable liée au bouton ON/OFF

	private Client client;									//le client sur lequel on agit via l'UI
	private final int port = 1664;									//numéro de port sur lequel on se connecte

	private JLabel labelImgPseudo;							//les labels de décoration
	private JLabel labelTexteIP;
	private JLabel labelImageIP;
	private JLabel labelTxtPass;
	private JLabel labelIMGPass;
	
	private Font tmpFont = null;									//les polices de caractères custom qui seront inclues en ressources
	private Font customLabels = null;
	private Font customTitle = null;

	private int pX, pY;												//les variables pour le déplacement de la fenètre (positions actuelles)

//METHODES-------------------------------------------------------------------------------------------------
	public ClientGUI() {							//constructeur de la classe
		try {	//pour garder le même design sur toutes les plateformes
			UIManager.setLookAndFeel(
			        UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		initialize();
		joinNetwork.addActionListener(this);
		quitNetwork.addActionListener(this);
		passwordField.addActionListener(this);
	}//ClientGUI_CSTR
	
	private void initialize() {						//initialisation de la GUI du client
		//CUSTOM FONTS START
			InputStream is = ClientGUI.class.getResourceAsStream("comesinhandy.ttf");	//chargement de la police des ressources
			try {
				tmpFont = Font.createFont(Font.TRUETYPE_FONT, is);
			} catch (FontFormatException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			customLabels = tmpFont.deriveFont(Font.ITALIC, 30);							//paramétrage de la police chargée et mise dans une font dédiées
			
			is = ClientGUI.class.getResourceAsStream("Sertig.otf");						//idem
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
			frame.addMouseListener(new MouseAdapter() {								//gére la position initiale de la souris
				public void mousePressed(MouseEvent mouse) {
					pX = mouse.getX();		//récupération des positions
					pY = mouse.getY();
				}

				public void mouseDragged(MouseEvent mouse) {						//gére le déplacement de la souris
					frame.setLocation(frame.getLocation().x + mouse.getX() - pX,
							frame.getLocation().y + mouse.getY() - pY);
				}
			});

			frame.addMouseMotionListener(new MouseMotionAdapter() {					//pour déplacer les fenétre
				public void mouseDragged(MouseEvent mouse) {
					frame.setLocation(frame.getLocation().x + mouse.getX() - pX,
							frame.getLocation().y + mouse.getY() - pY);
				}
			});
		//DRAG IT END
		
		/*
		 * Le reste de cette procédure est générée par windows builder et légèrement mis en forme, sauf indications contraires
		 */
		messageBox = new JTextArea();
		messageBox.setBorder(null);
		messageBox.setBackground(UIManager.getColor("ComboBox.background"));
		messageBox.setBounds(10, 473, 645, 46);
		Action send = new AbstractAction() {				//pour envoyer le message avec ENTER
			public void actionPerformed(ActionEvent e) {
				send();
			}
		};
		Action returnLine = new AbstractAction() {			//pour faire un retour à la ligne avec SHIFT+ENTER
			public void actionPerformed(ActionEvent e) {
				messageBox.append("\n");
			}
		};
		messageBox.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "envoyer");
		messageBox.getActionMap().put("envoyer", send);
		messageBox.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.SHIFT_MASK), "returnLine");
		messageBox.getActionMap().put("returnLine", returnLine);
		messageBox.setEnabled(false);
		frame.getContentPane().add(messageBox);

		sendButton = new JButton("Envoyer");
		sendButton.setBackground(Color.decode("#1d1d1d"));
		sendButton.setForeground(new Color(239,244,255));		//Les couleurs sont réglées manuellement sur la plupart des éléments
		sendButton.setFocusPainted(false);
		sendButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		sendButton.setBounds(667, 473, 117, 46);
		sendButton.setEnabled(false);
		frame.getContentPane().add(sendButton);
		
		quitNetwork = new JButton("Quitter le r\u00E9seau");
		quitNetwork.setBackground(Color.decode("#1d1d1d"));
		quitNetwork.setForeground(new Color(239,244,255));
		quitNetwork.setFocusPainted(false);
		quitNetwork.setEnabled(false);
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
		joinNetwork.setFocusPainted(false);
		frame.getContentPane().add(joinNetwork);
		
		passwordField = new JPasswordField(10);
		
		pseudoField = new JTextField();
		pseudoField.setBorder(BorderFactory.createEmptyBorder());
//			PromptSupport.setPrompt("Pseudo", pseudoField);
		pseudoField.setBounds(10, 123, 141, 35);
		pseudoField.setBackground(UIManager.getColor("ComboBox.background"));
		frame.getContentPane().add(pseudoField);

		pseudoField.setColumns(10);
		
		ipField = new JTextField();
		ipField.setBorder(BorderFactory.createEmptyBorder());
		ipField.setBackground(UIManager.getColor("ComboBox.background"));
//			PromptSupport.setPrompt("IP", ipField);
		ipField.setColumns(10);
		ipField.setBounds(161, 123, 141, 35);
		frame.getContentPane().add(ipField);
		
		passwordField = new JPasswordField();
		passwordField.setBorder(null);
		passwordField.setBounds(312, 123, 136, 35);
		passwordField.setBackground(UIManager.getColor("ComboBox.background"));
		Action connexion = new AbstractAction() {
		    public void actionPerformed(ActionEvent e) {
		        connect();
		    }
		};
		passwordField.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "connexion");
		passwordField.getActionMap().put("connexion", connexion);
		frame.getContentPane().add(passwordField);
		
		JLabel lblBlackboxProject = new JLabel("Blackbox Project");
		lblBlackboxProject.setFont(customTitle);					//exemple d'utilisation des polices personnalisées
		lblBlackboxProject.setForeground(new Color(239,244,255));
		lblBlackboxProject.setBounds(292, 20, 256, 50);
		frame.getContentPane().add(lblBlackboxProject);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBorder(null);
		scrollPane.setBounds(10, 169, 774, 293);
		frame.getContentPane().add(scrollPane);
		
		chatBox = new JTextArea();
		chatBox.setBorder(null);
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
	}//initialize
	
	@SuppressWarnings("deprecation")
	public String getPassword(){	//récupération du mot de passe pour le client
		return passwordField.getText();	//TODO Fix la méthode dépréciée
	}//getPassword
	
	public void append(String str) {	//affiche du texte dans la chat box (pour display)
		chatBox.append(str);
		chatBox.setCaretPosition(chatBox.getDocument().getLength());
	}//append
	
	public void connectionFailed() {	//pour que le client puisse signaler un problème de connection
		sendButton.removeActionListener(this);
		sendButton.setEnabled(false);
		messageBox.setEnabled(false);
		quitNetwork.setEnabled(false);
		joinNetwork.setEnabled(true);
		connected = false;
	}//connexion failed
	
	private void send(){
		client.sendMessage(new ChatMessage(ChatMessage.MESSAGE, messageBox.getText()));				
		messageBox.setText("");
	}//send

	private void connect(){
		String username = pseudoField.getText().trim();			//on enlève les espaces autour du pseudo
		if(username.length() == 0 || username.contains(" ")){	//on part si le champ et vide ou que le pseudo contient des espaces
			append("Nom d'utilisateur nul ou contenant un espace \n");
			return;
		}

		String server = ipField.getText().trim();	//on enlève les espaces éventuels autour de l'adresse
		if(server.length() == 0)					//si le champ IP est vide on part
			return;

		client = new Client(server, port, username, this);	//et on créé

		if(!client.start()) 								//on regarde si le lancement du client se passe bien
			return;
		messageBox.setText("");
		connected = true;

		sendButton.addActionListener(this);					//et on active le bouton envoyer
		sendButton.setEnabled(true);
		messageBox.setEnabled(true);

		ipField.setEditable(false);							//on règle les boutons et champs de l'UI pour qu'ils se comportent logiquement
		pseudoField.setEditable(false);
		passwordField.setEditable(false);
		
		joinNetwork.setEnabled(false);
		quitNetwork.setEnabled(true);
	}//connect
	
	public void actionPerformed(ActionEvent e) {	//gestion des boutons		    
		Object o = e.getSource();
		if(o == quitNetwork && connected) {	//si ça vient du bouton de déconnexion
			client.sendMessage(new ChatMessage(ChatMessage.LOGOUT, ""));
			ipField.setEditable(true);
			pseudoField.setEditable(true);
			passwordField.setEditable(true);
			quitNetwork.setEnabled(false);
			joinNetwork.setEnabled(true);
			return;
		}

		if(connected) {				//si ça vient de l'autre bouton (envoyer) et qu'on est connecté 
			send();
			return;
		}
		
		if(o == joinNetwork) {		//si ça vient du bouton de connexion
			connect();
		}
	}//actionPerformed
	
	public static void main(String[] args) {		//lancement de l'application
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
	}//main
}//Client GUI