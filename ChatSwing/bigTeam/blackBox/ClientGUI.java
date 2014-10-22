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
	
	//ATTRIBUTS
		private JFrame frame;									//la fen�tre principale
		
		private JTextField pseudoField;							//le champ pour entrer le nom d'utilisateur
		private JTextField ipField;								//le champ pour entrer l'adresse
		private JPasswordField passwordField;					//le champ pour entrer le mot de passe
		private JTextArea messageBox;							//le champ pour �crire les messages
		private JTextArea chatBox;								//le champ pour afficher les messages
	
		private JButton sendButton, quitNetwork, joinNetwork;	//les boutons pour interagir
	
		private boolean connected;								//la variable li�e au bouton ON/OFF
	
		private Client client;									//le client sur lequel on agit via l'UI
		final int port = 1664;									//num�ro de port sur lequel on se connecte

		private JLabel labelImgPseudo;							//les labels de d�coration
		private JLabel labelTexteIP;
		private JLabel labelImageIP;
		private JLabel labelTxtPass;
		private JLabel labelIMGPass;
		
		Font tmpFont = null;									//les polices de caract�res custom qui seront inclues en ressources
		Font customLabels = null;
		Font customTitle = null;
	
		int pX, pY;												//les variables pour le d�placement de la fen�tre (positions actuelles)

	//M�THODES
		public ClientGUI() {							//constructeur de la classe
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
				
				customLabels = tmpFont.deriveFont(Font.ITALIC, 30);							//param�trage de la police charg�e et mise dans une font d�di�es
				
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
				frame.addMouseListener(new MouseAdapter() {								//g�re la position initiale de la souris
					public void mousePressed(MouseEvent mouse) {
						pX = mouse.getX();		//r�cup�ration des positions
						pY = mouse.getY();
					}
	
					public void mouseDragged(MouseEvent mouse) {						//g�re le d�placement de la souris
						frame.setLocation(frame.getLocation().x + mouse.getX() - pX,
								frame.getLocation().y + mouse.getY() - pY);
					}
				});
	
				frame.addMouseMotionListener(new MouseMotionAdapter() {					//pour d�placer les fen�tre
					public void mouseDragged(MouseEvent mouse) {
						frame.setLocation(frame.getLocation().x + mouse.getX() - pX,
								frame.getLocation().y + mouse.getY() - pY);
					}
				});
			//DRAG IT END
			
			/*
			 * Le reste de cette proc�dure est g�n�r�e par windows builder et l�g�rement mis en forme, sauf indications contraires
			 */
			messageBox = new JTextArea();
			messageBox.setBorder(null);
			messageBox.setBackground(UIManager.getColor("ComboBox.background"));
			messageBox.setBounds(10, 473, 645, 46);
			frame.getContentPane().add(messageBox);
			
			sendButton = new JButton("Envoyer");
			sendButton.setBackground(Color.decode("#1d1d1d"));
			sendButton.setForeground(new Color(239,244,255));		//Les couleurs sont r�gl�es manuellement sur la plupart des �l�ments
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
			frame.getContentPane().add(passwordField);
			
			JLabel lblBlackboxProject = new JLabel("Blackbox Project");
			lblBlackboxProject.setFont(customTitle);					//exemple d'utilisation des polices custom
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
		
		public String getPassword(){	//r�cup du mdp pour le client
			return passwordField.getText();	//TODO Fix la m�thode d�pr�ci�e
		}//getPassword
		
		void append(String str) {	//affiche du texte dans la chat box (pour display)
			chatBox.append(str);
			chatBox.setCaretPosition(chatBox.getDocument().getLength());
		}//append
		
		void connectionFailed() {	//pour que le client puisse signaler un probl�me de connection
			sendButton.removeActionListener(this);
			sendButton.setEnabled(false);
			quitNetwork.setEnabled(false);
			joinNetwork.setEnabled(true);
			connected = false;
		}//connexion failed

		public void actionPerformed(ActionEvent e) {	//gestion des boutons		    
			Object o = e.getSource();
			if(o == quitNetwork && connected) {	//si �a vient du bouton de d�connexion
				client.sendMessage(new ChatMessage(ChatMessage.LOGOUT, ""));
				ipField.setEditable(true);
				pseudoField.setEditable(true);
				passwordField.setEditable(true);
				quitNetwork.setEnabled(false);
				joinNetwork.setEnabled(true);
				return;
			}

			if(connected) {				//si �a vient de l'autre bouton ('envoyer) et qu'on est connect� 
				client.sendMessage(new ChatMessage(ChatMessage.MESSAGE, messageBox.getText()));				
				messageBox.setText("");
				return;
			}
			
			if(o == joinNetwork) {		//si �a vient du bouton de connexion
				String username = pseudoField.getText().trim();			//on enl�ve les espaces autour du pseudo
				if(username.length() == 0 || username.contains(" ")){	//on part si le champ et vide ou que le pseudo contient des espaces
					append("Nom d'utilisateur nul ou contenant un espace \n");
					return;
				}

				String server = ipField.getText().trim();	//on enl�ve les espaces �ventuels autour de l'adresse
				if(server.length() == 0)					//si le champ IP est vide on part
					return;

				client = new Client(server, port, username, this);	//et on cr��

				if(!client.start()) 								//on regarde si le lancement du client se passe bien
					return;
				messageBox.setText("");
				connected = true;

				sendButton.addActionListener(this);					//et on active le bouton envoyer
				sendButton.setEnabled(true);

				ipField.setEditable(false);							//on r�gles les boutons et champs de l'UI pour qu'ils se comportent logiquement
				pseudoField.setEditable(false);
				passwordField.setEditable(false);
				
				joinNetwork.setEnabled(false);
				quitNetwork.setEnabled(true);
			}
		}//actionPerformed
		
		public static void main(String[] args) {		//lancement de l'appli
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