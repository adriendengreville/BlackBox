package blackBox;
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.InputStream;
/*
 * La GUI du serveur
 */
public class ServerGUI extends JFrame {
	//ATTRIBUTS
		
		private Server server;
		final int port = 1664;
		private boolean running = false;
		
		private JFrame frame;
		private JPasswordField password;
		
		int pX, pY;
		private JTextArea chat;
		private JTextArea event;
		private JButton startStop;
		
		private Font tmpFont, customTitle;
	//MÉTHODES
		ServerGUI() {
			initUI();
		}//ServerGUI
		
		private void initUI(){
			frame = new JFrame();
			frame.getContentPane().setBackground(Color.decode("#1d1d1d"));
			frame.getContentPane().setLayout(null);
			frame.setUndecorated(true);
			
			//CUSTOM FONTS START
				InputStream is = ClientGUI.class.getResourceAsStream("Sertig.otf");						//idem
				try {
					tmpFont = Font.createFont(Font.TRUETYPE_FONT, is);
				} catch (FontFormatException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				customTitle = tmpFont.deriveFont(Font.BOLD, 40);
			//CUSTOM FONTS END
			
			//DRAG IT START
				frame.addMouseListener(new MouseAdapter() {								//gère la position initiale de la souris
					public void mousePressed(MouseEvent mouse) {
						pX = mouse.getX();		//récupération des positions
						pY = mouse.getY();
					}
	
					public void mouseDragged(MouseEvent mouse) {						//gère le déplacement de la souris
						frame.setLocation(frame.getLocation().x + mouse.getX() - pX,
								frame.getLocation().y + mouse.getY() - pY);
					}
				});
	
				frame.addMouseMotionListener(new MouseMotionAdapter() {					//pour déplacer les fenêtre
					public void mouseDragged(MouseEvent mouse) {
						frame.setLocation(frame.getLocation().x + mouse.getX() - pX,
								frame.getLocation().y + mouse.getY() - pY);
					}
				});
			//DRAG IT END
			
			password = new JPasswordField();
			password.setBorder(null);
			password.setBounds(124, 71, 148, 20);
			password.setBackground(UIManager.getColor("ComboBox.background"));
			password.setColumns(10);
			
			frame.getContentPane().add(password);
			
			JLabel passwordLabel = new JLabel("Mot de passe");
			passwordLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
			passwordLabel.setForeground(new Color(239,244,255));
			passwordLabel.setBounds(10, 70, 133, 19);
			frame.getContentPane().add(passwordLabel);
			
			startStop = new JButton("Start");
			startStop.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					bindStartStop();
				}
			});
			startStop.setForeground(new Color(239,244,255));
			startStop.setFocusPainted(false);
			startStop.setBounds(301, 70, 89, 23);
			startStop.setBackground(new Color(39,39,39));
			frame.getContentPane().add(startStop);
			
			JLabel lblBlackboxServer = new JLabel("BlackBox Server");
			lblBlackboxServer.setFont(customTitle);
			lblBlackboxServer.setForeground(new Color(239,244,255));
			lblBlackboxServer.setBounds(10, 11, 234, 47);
			frame.getContentPane().add(lblBlackboxServer);
			
			JScrollPane scrollPane = new JScrollPane();
			scrollPane.setBorder(null);
			scrollPane.setBounds(10, 112, 380, 293);
			frame.getContentPane().add(scrollPane);
			
			chat = new JTextArea();
			chat.setDisabledTextColor(Color.BLACK);
			chat.setEditable(false);
			chat.setEnabled(false);
			chat.setBackground(UIManager.getColor("ComboBox.background"));
			scrollPane.setViewportView(chat);
			
			JScrollPane scrollPane_1 = new JScrollPane();
			scrollPane_1.setBorder(null);
			scrollPane_1.setBounds(10, 430, 380, 259);
			frame.getContentPane().add(scrollPane_1);
			
			event = new JTextArea();
			event.setDisabledTextColor(Color.BLACK);
			event.setEditable(false);
			event.setEnabled(false);
			event.setBackground(UIManager.getColor("ComboBox.background"));
			event.setBorder (BorderFactory.createLineBorder (new Color (0, 0, 0, 0), 2));
			scrollPane_1.setViewportView(event);
			
			JLabel lblMessagecrypts = new JLabel("Message (crypt\u00E9s)");
			lblMessagecrypts.setForeground(new Color(239,244,255));
			lblMessagecrypts.setBounds(10, 98, 133, 14);
			frame.getContentPane().add(lblMessagecrypts);
			
			JLabel label = new JLabel("\u00C9v\u00E8nements");
			label.setForeground(new Color(239,244,255));
			label.setBounds(10, 415, 100, 14);
			frame.getContentPane().add(label);
			
			JButton closeButton = new JButton("");
			closeButton.setBounds(349, 11, 51, 40);
			closeButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (running)
						server.stop();
					dispose();
					System.exit(1);
				}
			});
			closeButton.setIcon(new ImageIcon(ClientGUI.class.getResource("/blackBox/off.png")));
			closeButton.setBorder(BorderFactory.createEmptyBorder());
			closeButton.setContentAreaFilled(false);
			closeButton.setFocusPainted(false);
			frame.getContentPane().add(closeButton);
			
			
			frame.setBounds(100, 100, 400, 700);
			frame.setVisible(true);
			frame.setResizable(false);
			frame.setBackground(UIManager.getColor("FormattedTextField.inactiveForeground"));
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}//initUI
		
		public String getPassword(){
			return password.getText();
		}//getPassword
	
		void appendRoom(String str) {		//écrit dans le champ de chat
			chat.append(str);
			chat.setCaretPosition(chat.getText().length() - 1);
		}//appendRoom
		
		void appendEvent(String str) {		//écrit dans le champ des évènements
			event.append(str);
			event.setCaretPosition(event.getText().length() - 1);
		}//appendEvent
		
		public void bindStartStop() {	//transmet les états du bouton START/STOP
			if(server != null) { //si le serveur existe c'est qu'il tourne, donc on doit l'arrêter
				server.stop();
				server = null;
				running = false;
				password.setEditable(true);
				startStop.setText("Start");
				return;
			}
			//sinon on le démarre
			server = new Server(port, this);
			running = true;
			new ServerRunning().start();	//et on démarre le thread du serveurs
			startStop.setText("Stop");
			password.setEditable(false);
		}//actionPerformed
	
		public void windowClosing(WindowEvent e) {	//permet une fermeture propre du serveur lorsque la fenêtre est fermée
			// if my Server exist
			if(server != null) {
				try {
					server.stop();			// ask the server to close the conection
				}
				catch(Exception eClose) {
				}
				server = null;
			}
			// dispose the frame
			dispose();
			System.exit(0);
		}
		// I can ignore the other WindowListener method
		public void windowClosed(WindowEvent e) {}
		public void windowOpened(WindowEvent e) {}
		public void windowIconified(WindowEvent e) {}
		public void windowDeiconified(WindowEvent e) {}
		public void windowActivated(WindowEvent e) {}
		public void windowDeactivated(WindowEvent e) {}
	
		/*
		 * A thread to run the Server
		 */
		class ServerRunning extends Thread {
			public void run() {
				appendEvent("Le serveur a été démarré.\n");
				
				server.start();         // s'éxécute tant que le serveur fonctionne

				startStop.setText("Start");
				password.setEditable(true);
				appendEvent("Le serveur a été arrêté.\n");
				server = null;
			}
		}//ServerRunning
		
		public static void main(String[] arg) {	//démarrage de la GUI et du serveur
			new ServerGUI();
		}//main
}//ServerGUI