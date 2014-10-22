package blackBox;

import java.math.BigInteger;
import java.net.*;
import java.io.*;
import java.security.PublicKey;
import java.text.SimpleDateFormat;
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
	
	private SimpleDateFormat simpleDate;			//un format de date cool pour afficher l'heure
	
	Cryptage clientKeys;	//module de cryptage/d�cryptage c�t� client
	Cryptage serverKeys;	//module de cryptage/d�cryptage des messages du/au serveur

	
	Client(String server, int port, String username, ClientGUI clientUI) {
		this.server = server;
		this.port = port;
		this.username = username;

		this.clientUI = clientUI;
		
		simpleDate = new SimpleDateFormat("HH:mm:ss");
		
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
			sInput  = new ObjectInputStream(socket.getInputStream());	//on cr�� les flux
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
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try{
		sOutput.writeObject("Is somebody out there?");
		}catch (Exception e){
			display("Nom d'utilisateur d�j� utilis�.");
			return false;
		}
		//TODO R�gler le probl�me d'authentification
		this.sendInit(new ChatMessage(ChatMessage.PASSWORD, clientUI.getPassword()));		//envois mot de passe /!\ EN CLAIR DONC A DEPLACER /!\
		display("Mot de passe envoy� au serveur.");
		this.sendInit(new ChatMessage(ChatMessage.KEYCommon, clientKeys.getCommonKey()));	//envois de la cl� publique
		display("Cl� commune envoy�e au serveur (" + clientKeys.getCommonKey() + ")");
		this.sendInit(new ChatMessage(ChatMessage.KEYPublic, clientKeys.getPublicKey()));
		display("Cl� publique envoy�e au serveur (" + clientKeys.getPublicKey() + ")");
		
		display("Connexion accept�e par le serveur " + socket.getInetAddress() + ":" + socket.getPort() + ".");
		
		return true;
	}//start

	private void display(String msg) {	//permet d'afficher du texte dans la fenetre de chat
		clientUI.append(msg + "\n");
	}//display
	
	void sendInit(ChatMessage msg){ //pour envoyer un message non crypt� au serveur
		try {
			sOutput.writeObject(msg);
		}
		catch(IOException e) {
			display("Exception writing to server: " + e);
		}
	}//sendInit
	
	void sendMessage(ChatMessage msg) {		//permet de mettre en forme les messages et de les envoyer
		try {
			msg.setSender(username);
			msg.setTimeStamp(simpleDate.format(new Date()).toString());
			
			if (msg.getMessage().length() > 3 && msg.getMessage().substring(0, 3).equals("/to")){	//module d'�valutation pour les MP
				StringBuilder transform = new StringBuilder(msg.getMessage());
				transform = transform.delete(0, 4);													//on supprime le mot cl� "/to "
				
				String name = "";
				
				for (int i = 0; i < transform.length();){											//on extrait le nom d'utilisateur du destinataire du message
					if(transform.charAt(i) == ' '){
						transform = transform.deleteCharAt(i);
						break;
					}
					name += transform.charAt(i);													//on le stocke
					transform = transform.deleteCharAt(i);
				}
				
				msg.setDest(name);																	//on r�gle le destinataire dans les param�tres du message
				msg.setMessage(transform.toString());
				msg.setType(ChatMessage.MP);
				display(msg.getTimeStamp() + " " + msg.getSender() + " : " + msg.getMessage());
			}
			
			msg.setMessage(serverKeys.encrypt(msg.getMessage()).toString());
			
			sOutput.writeObject(msg);
		}
		catch(IOException e) {
			display("Exception writing to server: " + e);
		}
	}//sendMEssage

	
	private void disconnect() {	//pour d�connecter le client proprement
		try { 
			if(sInput != null) sInput.close();
		}
		catch(Exception e) {}
		try {
			if(sOutput != null) sOutput.close();
		}
		catch(Exception e) {}
        try{
			if(socket != null) socket.close();
		}
		catch(Exception e) {}
		
		if(clientUI != null)
			clientUI.connectionFailed();
	}//disconnect
	
	class ListenFromServer extends Thread {	//thread permettant de lire depuis le r�seau en boucle
		private ChatMessage msgIN;
		
		public void run() {
			while(true) {
				try {
					msgIN = (ChatMessage) sInput.readObject();												//on r�cup�re le message du r�seau
					
					if (msgIN.getType() == ChatMessage.MESSAGE || msgIN.getType() == ChatMessage.MP || msgIN.getType() == ChatMessage.LOGOUT){	//si on lit un message ou un MP
						display(msgIN.getTimeStamp() + " " + msgIN.getSender() + " : " + serverKeys.decrypt(serverKeys.convert(msgIN.getMessage())));
					}else if (msgIN.getType() == ChatMessage.KEYCommon) {	//tout ce qui concerne les cl�s
						serverKeys.setCommonKey(new BigInteger(clientKeys.decrypt(clientKeys.convert(msgIN.getMessage()))));		//on r�cup�re la cl� envoy�e, que l'on convertit en vector, que l'on d�crypte, que l'on met dans le set de cl�s
					}else if (msgIN.getType() == ChatMessage.KEYPublic) {
						serverKeys.setPublicKey(new BigInteger(clientKeys.decrypt(clientKeys.convert(msgIN.getMessage()))));
					}else if (msgIN.getType() == ChatMessage.KEYPrivate) {
						serverKeys.setPrivateKey(new BigInteger(clientKeys.decrypt(clientKeys.convert(msgIN.getMessage()))));
					}
				} catch(IOException e) {
					display("Erreur connexion refus�e");
					clientUI.connectionFailed();
					break;
				}
				catch(ClassNotFoundException e2) {
					//RAF
				}
			}
		}//run
	}//listenFromServer
}//Client

