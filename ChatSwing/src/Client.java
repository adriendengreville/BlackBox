
import java.math.BigInteger;
import java.net.*;
import java.io.*;
import java.security.PublicKey;
import java.util.*;
import java.util.concurrent.TimeUnit;

import javax.swing.text.html.HTMLDocument.HTMLReader.IsindexAction;

public class Client  {

	private ObjectInputStream sInput;		
	private ObjectOutputStream sOutput;		
	private Socket socket;

	private ClientGUI clientUI;
	
	private String server, username;
	private int port;
	
	Cryptage clientKeys;	//module de cryptage/d�cryptage c�t� client
	Cryptage serverKeys;	//module de cryptage/d�cryptage des messages du/au serveur

	
	Client(String server, int port, String username, ClientGUI clientUI) {
		this.server = server;
		this.port = port;
		this.username = username;

		this.clientUI = clientUI;
		
		clientKeys = new Cryptage();	//d�marage du module de cryptage client
		clientKeys.computeRSA_Key();	//g�n�re les cl�s de cryptage du client
		
		serverKeys = new Cryptage();	//d�marrage du module de cryptage serveur
	}//ClientCSTR
	
	
	public boolean start() {	//Connexion au serveur
		try {
			socket = new Socket(server, port);		//on se connecte
		} 
		catch(Exception e) {
			display("Erreur lors de la connexion au serveur: " + e);
			return false;
		}		
	
		try
		{
			sInput  = new ObjectInputStream(socket.getInputStream());
			sOutput = new ObjectOutputStream(socket.getOutputStream());
		}
		catch (IOException eIO) {
			display("Erreur lors de la cr�ation des Input/output Streams: " + eIO);
			return false;
		}
 
		new ListenFromServer().start();	//on commence � �couter ce que dis le serveur
		
		try
		{
			sOutput.writeObject(username);		//premi�re comm : envois du pseudo
		}
		catch (IOException eIO) {
			display("Erreur lors de l'envoi du pseudo: " + eIO);
			disconnect();
			return false;
		}
	
		this.sendInit(new ChatMessage(ChatMessage.PASSWORD, clientUI.getPassword()));
		display("Mot de passe envoy� au serveur.");
		
		this.sendInit(new ChatMessage(ChatMessage.KEYCommon, clientKeys.getCommonKey()));
		display("Cl� commune envoy�e au serveur (" + clientKeys.getCommonKey() + ")");
		this.sendInit(new ChatMessage(ChatMessage.KEYPublic, clientKeys.getPublicKey()));
		display("Cl� publique envoy�e au serveur (" + clientKeys.getPublicKey() + ")");
		
		display("Connexion accept�e par le serveur " + socket.getInetAddress() + ":" + socket.getPort() + ".");
		
		return true;
	}

	private void display(String msg) {
		clientUI.append(msg + "\n");		// append to the ClientGUI JTextArea (or whatever)
	}
	
	/*
	 * To send a message to the server
	 */
	void sendInit(ChatMessage msg){
		try {
			sOutput.writeObject(msg);
		}
		catch(IOException e) {
			display("Exception writing to server: " + e);
		}
	}
	
	void sendMessage(ChatMessage msg) {
		try {
			msg.setMessage(serverKeys.encrypt(msg.getMessage()).toString());
			msg.setSender(username);
			sOutput.writeObject(msg);
		}
		catch(IOException e) {
			display("Exception writing to server: " + e);
		}
	}

	/*
	 * When something goes wrong
	 * Close the Input/Output streams and disconnect not much to do in the catch clause
	 */
	private void disconnect() {
		try { 
			if(sInput != null) sInput.close();
		}
		catch(Exception e) {} // not much else I can do
		try {
			if(sOutput != null) sOutput.close();
		}
		catch(Exception e) {} // not much else I can do
        try{
			if(socket != null) socket.close();
		}
		catch(Exception e) {} // not much else I can do
		
		// inform the GUI
		if(clientUI != null)
			clientUI.connectionFailed();
	}
	
	/*
	 * a class that waits for the message from the server and append them to the JTextArea
	 * if we have a GUI or simply System.out.println() it in console mode
	 */
	class ListenFromServer extends Thread {

		private ChatMessage msgIN;
		
		public void run() {
			while(true) {
				try {
					msgIN = (ChatMessage) sInput.readObject();
					
					if (msgIN.getType() == ChatMessage.MESSAGE){
						display(msgIN.getTimeStamp() + " " + msgIN.getSender() + " : " + serverKeys.decrypt(serverKeys.convert(msgIN.getMessage())));
					}else if (msgIN.getType() == ChatMessage.KEYCommon) {
						serverKeys.setCommonKey(new BigInteger(clientKeys.decrypt(clientKeys.convert(msgIN.getMessage()))));		//on r�cup�re la cl� envoy�e, que l'on convertit en vector, que l'on d�crypte, que l'on met dans le set de cl�s
						display(clientKeys.decrypt(clientKeys.convert(msgIN.getMessage())));
					}else if (msgIN.getType() == ChatMessage.KEYPublic) {
						serverKeys.setPublicKey(new BigInteger(clientKeys.decrypt(clientKeys.convert(msgIN.getMessage()))));
						display(clientKeys.decrypt(clientKeys.convert(msgIN.getMessage())));
					}else if (msgIN.getType() == ChatMessage.KEYPrivate) {
						serverKeys.setPrivateKey(new BigInteger(clientKeys.decrypt(clientKeys.convert(msgIN.getMessage()))));
						display(serverKeys.getPrivateKey());
					}
				} catch(IOException e) {
					display("Erreur connexion refus�e");
					if(clientUI != null) 
						clientUI.connectionFailed();
					break;
				}
				catch(ClassNotFoundException e2) {
					//RAF
				}
			}
		}
	}
}

