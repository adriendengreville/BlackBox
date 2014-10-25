package blackBox;
import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class Server {
//ATTRIBUTS------------------------------------------------------------------------------------------------
	private static int uniqueId;					//un ID pour chaque client
	
	private ArrayList<ClientThread> clientList;		//liste des clients (threads)
	
	private ServerGUI serverUI;						//l'UI du serveur
	
	private SimpleDateFormat simpleDate;			//un format de date cool pour afficher l'heure
	
	private static int port;						//port du serveur oé les connexions sont attendues
	
	private boolean keepGoing;						//le "bouton" ON/OFF du serveur
	
	protected Cryptage serverKeys;							//paire de clés du serveur qui sera transmise aux clients

//MÉTHODES-------------------------------------------------------------------------------------------------
	public Server(int port, ServerGUI serverUI) {
		this.serverUI = serverUI;
		Server.port = port;
		simpleDate = new SimpleDateFormat("HH:mm:ss");
		clientList = new ArrayList<ClientThread>();
	}//ServerCSTR
	
	public void start() {
		keepGoing = true;
		
		display("Génération de la paire de clés du serveur.");
		serverKeys = new Cryptage();		//démarrage du module de cryptage
		serverKeys.computeRSA_Key();		//création des clés privées et publiques du serveur qui seront transmises aux clients

		try 
		{
			ServerSocket serverSocket = new ServerSocket(port);			//socket serveur
			while (keepGoing){ 	//boucle infinie pour toujours accepter les connexions, lié au bouton stop du serveur
				display("Serveur en attente de connexions sur le port " + port + ".");

				Socket socket = serverSocket.accept();  				//accepter la connexion

				//si on stope au milieu
				if(!keepGoing)
					break;

				ClientThread clientThread = new ClientThread(socket);  	//placer la connexion dans un thread pour en faire un client
				clientList.add(clientThread);							//l'ajouter à notre liste de clients

				clientThread.start();	// /!\Appelle le run du thread défini ci-bas
			}//on sort aprés appuis du bouton stop

			try {	//tout ce qui concerne la fermeture du serveur et des sockets et flux liés
				serverSocket.close();			
				for(int i = 0; i < clientList.size(); ++i) {
					ClientThread threadToClose = clientList.get(i);
					try {
						threadToClose.sInput.close();
						threadToClose.sOutput.close();
						threadToClose.socket.close();
					}
					catch(IOException ioE) {
						//on fait quoi si y a une exception? ._.
					}
				}
			}
			catch(Exception e) {
				display("Probléme lors de la fermeture du client et du serveur: " + e);
			}
		}
		catch (IOException e) {
			display("Probléme lors de la création du socket serveur: " + e);
		}
	}//start	
   
	protected void stop() {
		keepGoing = false;
		// Socket socket = serverSocket.accept();
		try {
			new Socket("localhost", port);
		}
		catch(Exception e) {
			//RAF
		}
	}//stop
	
	private void display(String msg) {	//afficher dans la eventBox
		serverUI.appendEvent(simpleDate.format(new Date()) + " : " + msg + "\n");
	}//display

	private synchronized void broadcast(ChatMessage message) {	//envoyer un message à tout le monde dans le canal
		if (message.getSender().equals("null"))
			message.setSender("Serveur");
		
		serverUI.appendRoom(message.getTimeStamp() + " " + message.getSender() +
				" : " + "Message" + "\n");   		//affiche le message (crypté bien sûr) dans la fenétre chat du serveur
		
		ChatMessage messageToSend = new ChatMessage(message);					//on créé un ChatMessage qui contiendra le message chiffré pour chaque client
		for(int i = clientList.size(); --i >= 0;) {				// backloop pour supprimer un client qui ne répond plus
			ClientThread clientsToMessage = clientList.get(i);
			messageToSend.setMessage(clientsToMessage.clientKeys.encrypt(message.getMessage()).toString());	//on chiffre avec la clé client

			if(!clientsToMessage.writeMsg(messageToSend)) {			//d'une pierre deux coups, on tente d'envoyer le message et on teste si ça a marché pour savoir si on garde le client
				clientList.remove(i);
				display(clientsToMessage.username + " ne répond plus. Déconnecté du serveur.");
			}
		}
	}//broadcast
	
	private void mpTO (String dest, ChatMessage message){
		
		for(int i = clientList.size(); --i >= 0;){
			String name = clientList.get(i).username;
			
			if (name.equals(dest)){
				sendTo(clientList.get(i), message);
				return;
			}
		}
		
		ClientThread sender = null;
		for(int i = clientList.size(); --i >= 0;){ //on chercher l'émetteur en cas d'echec pour lui retourner l'erreur
			String name = clientList.get(i).username;
			if (name.equals(message.getSender())){
				sender = clientList.get(i);
				break;
			}
		}
		mpTO(message.getSender(), new ChatMessage(ChatMessage.MESSAGE, sender.clientKeys.encrypt("Erreur : l'utilisateur " + message.getDest() + " est introuvable.").toString()));
	}//MPTO
	
	private synchronized void sendTo(ClientThread client, ChatMessage message) {	//envoyer un message à un destinataire en particulier (pour MPs et connexions)
		message.setDest(client.username);

		if (message.getSender().equals("null"))
			message.setSender("Serveur");
		
		message.setTimeStamp(simpleDate.format(new Date()).toString());
		
		String whatToWrite = message.getTimeStamp() + " " + message.getSender() + " --> " + message.getDest() + " :  ";
		if (message.getType() == ChatMessage.KEYCommon || message.getType() == ChatMessage.KEYPrivate || message.getType() == ChatMessage.KEYPublic)
			whatToWrite += " échange de clés.";
		else
			whatToWrite += "Message";
		serverUI.appendRoom(whatToWrite + "\n"); //affiche le message (crypté bien sûr) dans la fenêtre chat du serveur et son destinataire

		ChatMessage messageToSend = new ChatMessage(message);
		for(int i = clientList.size(); --i >= 0;) {				//idem que pour brodcast sauf qu'on n'enverra qu'au client qui correspond
			ClientThread clientToMessage = clientList.get(i);
			messageToSend.setMessage(clientToMessage.clientKeys.encrypt(message.getMessage()).toString());	//on chiffre avec la clé du destinataire
			
			if(clientToMessage.id == client.id && !client.writeMsg(messageToSend)) {	//on garde la boucle afin de pouvoir supprimer le client en cas de perte de connexion
				clientList.remove(i);
				display(clientToMessage.username + " ne répond plus. Déconnecté du serveur.");
				break;
			}
		}
	}//sendTo


	public synchronized void removeClient(int id) {	//pour retirer un client qui fait une deconnexion propre
		for(int i = 0; i < clientList.size(); ++i) {
			ClientThread clientToRemove = clientList.get(i);

			if(clientToRemove.id == id) {
				clientList.remove(i);
				return;
			}
		}
	}//removeClient

	
	private class ClientThread extends Thread {		//un thread par client qui tourne tant qu'il n'a pas été perçu comme déconnecté
		private Socket socket;					//socket pour écouter le client
		private ObjectInputStream sInput;		//et ses flux
		private ObjectOutputStream sOutput;
		
		public Cryptage clientKeys;			//paire de clés transmises par le client
		
		private boolean  clientCommonKeyGiven = false;
		private boolean  clientPublicKeyGiven = false;
		private boolean  toKill = false;
		
		int id;
		
		String username;
		ChatMessage message;			//le message entrant

		ClientThread(Socket socket) {

			id = ++uniqueId;
			this.socket = socket;
			clientKeys = new Cryptage();

			System.out.println("Création des Object Input/Output Streams par un thread client");
			try
			{
				sOutput = new ObjectOutputStream(socket.getOutputStream());
				sInput  = new ObjectInputStream(socket.getInputStream());

				username = (String) sInput.readObject();	//le premier objet envoyé par le client sera le pseudo donc on le stocke
				
				for( ClientThread clToTest : clientList){	//on vérifie que le nom n'est pas déjé pris
					if (clToTest.username.equals(username)){
						removeClient(id);
						display("Un utilisateur a essayé de se connecter sous le nom de " + username + ", qui existe déjà.");
						toKill = true;
						close();
						return;
					}
				}

				display("Obtention de la clé publique de " + username + "." );
			}
			catch (IOException e) {
				display(username + " : Erreur lors de la création d'Input/output Streams: " + e);
				return;
			}
			catch (ClassNotFoundException e) {
				System.out.println("outch");
			}
		}//ClientThreadCSTR
		
		public void run() {			//ce qui tourne en boucle jusqu'à une deconnexion ou autre
			boolean keepGoing = true;
			
			if (toKill)				//si l'utilisateur n'a pas le droit d'être connecté, on le supprimer d'office
				return;
			
			while(keepGoing) {
				if (socket.isClosed())									//si le socket est fermé on a plus rien à faire ici
					break;

				try {
					message = (ChatMessage) sInput.readObject();		//récupération des objets ChatMessage
				}
				catch (IOException e) {
					display(username + " : Problème lors de la lecture des flux: " + e);
					break;				
				}
				catch(ClassNotFoundException e2) {		//je vois pas comment éa pourrait arriver mais on se protége quand méme
					break;
				}
				
				if ((message.getType() == ChatMessage.MESSAGE || message.getType() == ChatMessage.MP) &&
						!serverKeys.decrypt(serverKeys.convert(message.getPassword())).equals(serverUI.getPassword())){	//on test le mot de passe si on reçoit un message ou un mp
					display(username + " : Mot de passe incorrect. Déconnexion.");
					removeClient(id);
					break;
				}
				
				if (message.getType() == ChatMessage.MESSAGE || message.getType() == ChatMessage.MP){	//si c'est un message on le déchiffre
					String messageRecu = serverKeys.decrypt(serverKeys.convert(message.getMessage()));		//pour le chiffre plus tard pour chaque client
					message.setMessage(messageRecu);
				}

				switch(message.getType()) {				//on regarde ce qu'on a reçu
				case ChatMessage.MESSAGE:
					broadcast(message);
					break;
				case ChatMessage.LOGOUT:
					display(username + " s'est déconnecté.");
					keepGoing = false;
					break;
				case ChatMessage.KEYCommon:
					sendKey(ChatMessage.KEYCommon, message.getMessage());
					break;
				case ChatMessage.KEYPublic:
					sendKey(ChatMessage.KEYPublic, message.getMessage());
					break;
				case ChatMessage.MP:
					mpTO(message.getDest(), message);
				}				
			}
			removeClient(id);	//retire ce client de la liste des clients 
			close();			//avant de fermer le socket
		}//run
		
		private void close() {	//on ferme tout ce qu'on a ouvert
			try {
				if(sOutput != null) sOutput.close();
			}
			catch(Exception e) {}
			try {
				if(sInput != null) sInput.close();
			}
			catch(Exception e) {};
			try {
				if(socket != null) socket.close();
			}
			catch (Exception e) {}
		}

		private boolean writeMsg(ChatMessage msg) {		//pour écrire au client
			if(!socket.isConnected()) {		//si le client est déconnecté on ne va pas plus loin et on ferme tout
//				removeClient(id);			//si writeMsg échoue le client sera retiré de la liste aprés, on s'en occupe pas ici
				close();
				return false;
			}

			try {
				sOutput.writeObject(msg);	//on écrit au client
			}
			catch(IOException e) {
				display("Erreur lors de l'envoi à " + username + ". (" + e + ")");
			}
			return true;
		}
		
		private void sendKey(int type, String key){
			if (type == ChatMessage.KEYCommon){
				this.clientKeys.setCommonKey(new BigInteger(key));
				clientCommonKeyGiven = true;
			}
			else if (type == ChatMessage.KEYPublic){
				this.clientKeys.setPublicKey(new BigInteger(key));
				clientPublicKeyGiven = true;
			}
			
			if (clientCommonKeyGiven && clientPublicKeyGiven){
				sendTo(this, new ChatMessage(ChatMessage.KEYCommon, serverKeys.getCommonKey().toString()));		//COMMON
				display(serverKeys.getCommonKey().toString());
				sendTo(this, new ChatMessage(ChatMessage.KEYPublic, serverKeys.getPublicKey().toString()));		//PUBLIC
					
//				sendTo(this, new ChatMessage(ChatMessage.KEYPrivate, clientKeys.encrypt(serverKeys.getPrivateKey()).toString()));	//PRIVATE

				clientCommonKeyGiven = false;
				clientPublicKeyGiven = false;
				display("Clé publique reçue de " + username + ": " + clientKeys.getCommonKey() + " " + clientKeys.getPublicKey());
				display("Envoi de la clé publique du serveur.");
			}
		}
	}
}