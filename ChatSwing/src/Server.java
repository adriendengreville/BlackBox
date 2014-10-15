import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

/*
 * The server that can be run both as a console application or a GUI
 */
public class Server {
	private static int uniqueId;					//un ID pour chaque client
	
	private ArrayList<ClientThread> clientList;		//liste des clients (threads)
	
	private ServerGUI serverUI;						//l'UI du serveur
	
	private SimpleDateFormat simpleDate;			//un format de date cool pour afficher l'heure
	
	private static int port;					//port du serveur o� les connexions sont attendues
	
	private boolean keepGoing;						//le "bouton" ON/OFF du serveur
	
	Cryptage serverKeys;							//paire de cl�s du serveur qui sera transmise aux clients

	public Server(int port, ServerGUI serverUI) {
		this.serverUI = serverUI;
		Server.port = port;
		simpleDate = new SimpleDateFormat("HH:mm:ss");
		clientList = new ArrayList<ClientThread>();
	}//ServerCSTR
	
	public void start() {
		keepGoing = true;
		serverKeys = new Cryptage();		//d�marrage du module de cryptage
		serverKeys.computeRSA_Key();		//cr�ation des cl�s priv�es et publiques du serveur qui seront transmises aux clients

		try 
		{
			ServerSocket serverSocket = new ServerSocket(port);			//socket serveur
			while (keepGoing){ 	//boucle infinie pour toujours accepter les connexions avec le bouton stop du serveur
				display("Serveur en attente de connexions sur le port " + port + ".");

				Socket socket = serverSocket.accept();  				//accepter la connexion

				//si on stope au milieu
				if(!keepGoing)
					break;

				ClientThread clientThread = new ClientThread(socket);  	//placer la connexion dans un thread pour en faire un client
				clientList.add(clientThread);							//l'ajouter � notre liste de clients
				clientThread.start();	///!\Appelle le run du thread d�fini ci-bas
			}//on sort apr�s appuis du bouton stop

			try {
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
				display("Probl�me lors de la fermeture du client et du serveur: " + e);
			}
		}
		catch (IOException e) {
			display("Probl�me lors de la cr�ation du socket serveur: " + e);
		}
	}//start	
   
	protected void stop() {
		keepGoing = false;
		// connect to myself as Client to exit statement 
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

	private synchronized void broadcast(ChatMessage message) {	//envoyer un message � tout le monde dans le canal
		serverUI.appendRoom(message.getTimeStamp() + " " + message.getSender() +
				" : " + message.getMessage() + "\n");   //affiche le message (crypt� bien s�r) dans la fen�tre chat du serveur
		
		for(int i = clientList.size(); --i >= 0;) {		// backloop pour supprimer un client qui ne r�pond plus
			ClientThread clientsToMessage = clientList.get(i);
			
			if(!clientsToMessage.writeMsg(message)) {	//d'une pierre deux coups, on tente d'envoyer le message et on teste si �a a march� pour savoir si on garde le client
				clientList.remove(i);
				display(clientsToMessage.username + " ne r�pond plus. D�connect� du serveur.");
			}
		}
	}//broadcast
	
	private ClientThread mpTO (String username){
		for(int i = 0; i < clientList.size(); i++)
			if (clientList.get(i).username == username)
				return clientList.get(i);
		
		return null;	//si le client n'existe pas
	}
	
	private synchronized void sendTo(ClientThread client, ChatMessage message) {	//envoyer un message � un destinataire en particulier
		if(client == null)	//si jamais le client cherch� dans le cas des MP n'existe pas
			return;
		
		message.setDest(client.username);

		if (message.getSender().equals("null"))
			message.setSender("Serveur");
		
		message.setTimeStamp(simpleDate.format(new Date()).toString());

		serverUI.appendRoom(message.getTimeStamp() + " " + message.getSender() +
				" --> " + message.getDest() + " :  " + message.getMessage() + "\n"); //affiche le message (crypt� bien s�r) dans la fen�tre chat du serveur et son destinataire

		for(int i = clientList.size(); --i >= 0;) {				//idem que pour brodcast sauf qu'on n'enverra qu'au client qui correspond
			ClientThread clientToMessage = clientList.get(i);

			if(clientToMessage.id == client.id && !client.writeMsg(message)) {	//on garde la boucle afin de pouvoir supprimer le client en cas de perte de connexion
				clientList.remove(i);
				display(clientToMessage.username + " ne r�pond plus. D�connect� du serveur.");
				break;
			}
		}
	}//sendTo


	synchronized void removeClient(int id) {	//pour retirer un client qui fait une deconnexion propre
		for(int i = 0; i < clientList.size(); ++i) {
			ClientThread clientToRemove = clientList.get(i);

			if(clientToRemove.id == id) {
				clientList.remove(i);
				return;
			}
		}
	}//remove

	
	class ClientThread extends Thread {		//un thread par client qui tourne tant qu'il n'a pas �t� per�u comme d�connect�
		Socket socket;					//socket pour �couter le client
		ObjectInputStream sInput;		//et ses flux
		ObjectOutputStream sOutput;
		
		Cryptage clientKeys;			//paire de cl�s transmises par le client
		
		boolean  clientCommonKeyGiven = false;
		
		boolean  clientPublicKeyGiven = false;
		
		int id;
		
		String username;
		
		ChatMessage message;			//le message entrant

		ClientThread(Socket socket) {

			id = ++uniqueId;
			this.socket = socket;
			clientKeys = new Cryptage();

			System.out.println("Cr�ation des Object Input/Output Streams par un thread client");
			try
			{
				sOutput = new ObjectOutputStream(socket.getOutputStream());
				sInput  = new ObjectInputStream(socket.getInputStream());

				username = (String) sInput.readObject();	//le premier objet envoy� par le client sera le pseudo donc on le stocke
				try {
					message = (ChatMessage) sInput.readObject();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(!message.getMessage().equals(serverUI.getPassword())) {					//v�rification du mot de passe
					display(username + " : Mot de passe incorrect. D�connexion.");
//					sendTo(this, new ChatMessage(ChatMessage.ConnectERR, ""));				//on pr�vient le client qu'il a �t� rejet�
					removeClient(id);
					close();
				}else {
//					sendTo(this, new ChatMessage(ChatMessage.ConnectOK, ""));
					display(username + " s'est connect�.");
				}
			}
			catch (IOException e) {
				display(username + " : Erreur lors de la cr�ation d'Input/output Streams: " + e);
				return;
			}
			catch (ClassNotFoundException e) {
				System.out.println("outch");
			}
		}

		public void run() {			//ce qui tourne en boucle jusqu'� une deconnexion ou autre
			boolean keepGoing = true;
			
			while(keepGoing) {
				if (socket.isClosed())									//si le socket est ferm� on a plus rien � faire ici
					break;
				
				try {
					message = (ChatMessage) sInput.readObject();		//r�cup�ration des objets ChatMessage
				}
				catch (IOException e) {
					display(username + " : Probl�me lors de la lecture des flux: " + e);
					break;				
				}
				catch(ClassNotFoundException e2) {		//je vois pas comment �a pourrait arriver mais on se prot�ge quand m�me
					break;
				}

				switch(message.getType()) {				//on regarde ce qu'on a re�u
				case ChatMessage.MESSAGE:
					broadcast(message);
					break;
				case ChatMessage.LOGOUT:
					display(username + " s'est d�connect�.");
					keepGoing = false;
					break;
				case ChatMessage.KEYCommon:
					sendKey(ChatMessage.KEYCommon, message.getMessage());
					break;
				case ChatMessage.KEYPublic:
					sendKey(ChatMessage.KEYPublic, message.getMessage());
					break;
				}
			}
			
			removeClient(id);	//retire ce client de la liste des clients 
			close();			//avant de fermer le socket
		}
		
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

		private boolean writeMsg(ChatMessage msg) {		//pour �crire au client
			if(!socket.isConnected()) {		//si le client est d�connect� on ne va pas plus loin et on ferme tout
//				removeClient(id);			//si writeMsg �choue le client sera retir� de la liste apr�s, on s'en occupe pas ici
				close();
				return false;
			}

			try {
				sOutput.writeObject(msg);	//on �crit au client
			}
			catch(IOException e) {
				display("Erreur lors de l'envoi � " + username + ". (" + e + ")");
			}
			return true;
		}
		
		private void sendKey(int type, String key){			//permet l'�change des cl�s entre le client et le serveur
			if (type == ChatMessage.KEYCommon){
				clientKeys.setCommonKey(new BigInteger(key));
				clientCommonKeyGiven = true;
			}
			else if (type == ChatMessage.KEYPublic){
				clientKeys.setPublicKey(new BigInteger(key));
				clientPublicKeyGiven = true;
			}
			
			if (clientCommonKeyGiven && clientPublicKeyGiven){	//une fois qu'on a la cl� publique du client on peut lui envoyer les cl�s du serveur
				sendTo(this, new ChatMessage(ChatMessage.KEYCommon, clientKeys.encrypt(serverKeys.getCommonKey()).toString()));
				sendTo(this, new ChatMessage(ChatMessage.KEYPublic, clientKeys.encrypt(serverKeys.getPublicKey()).toString()));
				sendTo(this, new ChatMessage(ChatMessage.KEYPrivate, clientKeys.encrypt(serverKeys.getPrivateKey()).toString()));
				clientCommonKeyGiven = false;
				clientPublicKeyGiven = false;
			}
		}
	}
}


