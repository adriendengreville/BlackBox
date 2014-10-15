
import java.math.BigInteger;
import java.net.*;
import java.io.*;
import java.util.*;

import javax.swing.text.html.HTMLDocument.HTMLReader.IsindexAction;

public class Client  {

	private ObjectInputStream sInput;		
	private ObjectOutputStream sOutput;		
	private Socket socket;

	private ClientGUI clientUI;
	
	private String server, username;
	private int port;
	
	Cryptage clientKeys;	//module de cryptage/décryptage côté client
	Cryptage serverKeys;	//module de cryptage/décryptage des messages du/au serveur

	
	Client(String server, int port, String username, ClientGUI clientUI) {
		this.server = server;
		this.port = port;
		this.username = username;

		this.clientUI = clientUI;
		
		clientKeys = new Cryptage();	//démarage du module de cryptage client
		clientKeys.computeRSA_Key();	//génère les clés de cryptage du client
		
		serverKeys = new Cryptage();	//démarrage du module de cryptage serveur
	}//ClientCSTR
	
	
	public boolean start() {	//Connexion au serveur
		try {
			socket = new Socket(server, port);		//on se connecte
		} 
		catch(Exception e) {
			display("Erreur lors de la connexion au serveur: " + e);
			return false;
		}		
	
		/* Creating both Data Stream */
		try
		{
			sInput  = new ObjectInputStream(socket.getInputStream());
			sOutput = new ObjectOutputStream(socket.getOutputStream());
		}
		catch (IOException eIO) {
			display("Exception creating new Input/output Streams: " + eIO);
			return false;
		}

		// creates the Thread to listen from the server 
		new ListenFromServer().start();
		// Send our username to the server this is the only message that we
		// will send as a String. All other messages will be ChatMessage objects
		
		try
		{
			sOutput.writeObject(username);
		}
		catch (IOException eIO) {
			display("Erreur lors de la l'envoi du pseudo : " + eIO);
			disconnect();
			return false;
		}

		display("Soumission du mot de passe au serveur.");
		
		this.sendInit(new ChatMessage(ChatMessage.PASSWORD, clientUI.getPassword()));
		
//		ChatMessage reponse = null;
//
//		reponse = (ChatMessage) sInput.readObject();
//
//		if(reponse.getType() == ChatMessage.ConnectERR){										//on vérifie si le mot de passe est bon
//			display("La connexion a été refusée par le serveur car le mot de passe est incorrect.");
//			disconnect();
//			return false;
//		}else if (reponse.getType() == ChatMessage.ConnectOK){
//			display("Le mot de passe a été accepté par le serveur.");
//		}

		
			
		this.sendInit(new ChatMessage(ChatMessage.KEYCommon, clientKeys.getCommonKey()));
		System.out.println("clé envoyée");
		this.sendInit(new ChatMessage(ChatMessage.KEYPublic, clientKeys.getPublicKey()));
		
		display("Connexion acceptée par le serveur " + socket.getInetAddress() + ":" + socket.getPort() + ".");
		
		return true;		
	}

	/*
	 * To send a message to the console or the GUI
	 */
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
						serverKeys.setCommonKey(new BigInteger(clientKeys.decrypt(clientKeys.convert(msgIN.getMessage()))));		//on récupère la clé envoyée, que l'on convertit en vector, que l'on décrypte, que l'on met dans le set de clés
					}else if (msgIN.getType() == ChatMessage.KEYPublic) {
						serverKeys.setPublicKey(new BigInteger(clientKeys.decrypt(clientKeys.convert(msgIN.getMessage()))));
					}else if (msgIN.getType() == ChatMessage.KEYPrivate) {
						serverKeys.setPrivateKey(new BigInteger(clientKeys.decrypt(clientKeys.convert(msgIN.getMessage()))));
						display(serverKeys.getPrivateKey());
					}
				} catch(IOException e) {
					display("Erreur connexion refusée");
					if(clientUI != null) 
						clientUI.connectionFailed();
					break;
				}
				// can't happen with a String object but need the catch anyhow
				catch(ClassNotFoundException e2) {
				}
			}
		}
	}
}

