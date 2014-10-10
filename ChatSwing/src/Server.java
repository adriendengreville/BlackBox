import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

/*
 * The server that can be run both as a console application or a GUI
 */
public class Server {
	// a unique ID for each connection
	private static int uniqueId;
	// an ArrayList to keep the list of the Client
	private ArrayList<ClientThread> al;
	// if I am in a GUI
	private ServerGUI sg;
	// to display time
	private SimpleDateFormat sdf;
	// the port number to listen for connection
	private static int port = 6969;
	private String password;
	// the boolean that will be turned of to stop the server
	private boolean keepGoing;
	
	Cryptage serverKeys;	//paire de cl�s du serveur qui sera transmise aux clients
	Cryptage clientKeys;
	boolean  clientCommonKeyGiven = false;
	boolean  clientPublicKeyGiven = false;

	/*
	 *  server constructor that receive the port to listen to for connection as parameter
	 *  in console
	 */
	public Server(int port) {
		this(port, null);
	}
	
	public Server(int port, ServerGUI sg) {
		// GUI or not
		this.sg = sg;
		// the port
		this.port = port;
		// to display hh:mm:ss
		sdf = new SimpleDateFormat("HH:mm:ss");
		// ArrayList for the Client list
		al = new ArrayList<ClientThread>();
	}
	
	public void start() {
		keepGoing = true;
		serverKeys = new Cryptage();		//d�marrage du module de cryptage
		serverKeys.computeRSA_Key();		//cr�ation des cl�s priv�es et publiques du serveur qui seront transmises aux clients
		
		/* create socket server and wait for connection requests */
		try 
		{
			// the socket used by the server
			ServerSocket serverSocket = new ServerSocket(port);
			// infinite loop to wait for connections
			while(keepGoing) 
			{
				// format message saying we are waiting
				display("Server waiting for Clients on port " + port + ".");
				Socket socket = serverSocket.accept();  	// accept connection
				
				// if I was asked to stop
				if(!keepGoing)
					break;
				ClientThread t = new ClientThread(socket);  // make a thread of it
				al.add(t);									// save it in the ArrayList
				t.start();
			}
			// I was asked to stop
			try {
				serverSocket.close();
				for(int i = 0; i < al.size(); ++i) {
					ClientThread tc = al.get(i);
					try {
					tc.sInput.close();
					tc.sOutput.close();
					tc.socket.close();
					}
					catch(IOException ioE) {
						// not much I can do
					}
				}
			}
			catch(Exception e) {
				display("Exception closing the server and clients: " + e);
			}
		}
		// something went bad
		catch (IOException e) {
            String msg = sdf.format(new Date()) + " Exception on new ServerSocket: " + e + "\n";
			display(msg);
		}
	}		
    /*
     * For the GUI to stop the server
     */
	protected void stop() {
		keepGoing = false;
		// connect to myself as Client to exit statement 
		// Socket socket = serverSocket.accept();
		try {
			new Socket("localhost", port);
		}
		catch(Exception e) {
			// nothing I can really do
		}
	}
	/*
	 * Display an event (not a message) to the console or the GUI
	 */
	private void display(String msg) {
		String time = sdf.format(new Date()) + " " + msg;
		if(sg == null)
			System.out.println(time);
//		else
//			sg.appendEvent(time + "\n");
	}
	/*
	 *  to broadcast a message to all Clients
	 */
	private synchronized void broadcast(ChatMessage message) {
		// add HH:mm:ss and \n to the message
//		String time = sdf.format(new Date());
//		String messageLf = /*time + " " + */message /*+ "\n"*/;
//		ChatMessage messageFinal = new ChatMessage(ChatMessage.MESSAGE, messageLf);
		message.setTimeStamp(sdf.format(new Date()));
		// display message on console or GUI
	
		sg.appendRoom(message.getTimeStamp() + " " + message.getSender() +
				" : " + message.getMessage() + "\n");     // append in the room window
		
		// we loop in reverse order in case we would have to remove a Client
		// because it has disconnected
		for(int i = al.size(); --i >= 0;) {
			ClientThread ct = al.get(i);
			// try to write to the Client if it fails remove it from the list
			if(!ct.writeMsg(message)) {
				al.remove(i);
				display("Disconnected Client " + ct.username + " removed from list.");
			}
		}
	}//broadcast
	
	private synchronized void sendTo(String user, ChatMessage message) {
		// add HH:mm:ss and \n to the message
		String time = sdf.format(new Date());
//		String messageLf = /*time + " " + */message /*+ "\n"*/;
//		ChatMessage messageFinal = new ChatMessage(type, messageLf);
		message.setTimeStamp(time);
		message.setDest(user);
		
		if (message.getSender().equals("null"))
			message.setSender("Serveur");
		
		sg.appendRoom(message.getTimeStamp() + " " + message.getSender() +
				" --> " + message.getDest() + " :  " + message.getMessage() + "\n");     // append in the room window
		
		// we loop in reverse order in case we would have to remove a Client
		// because it has disconnected
		for(int i = al.size(); --i >= 0;) {
			ClientThread ct = al.get(i);
	
			if(ct.username.equals(user) && !ct.writeMsg(message)) {	//on envoit un message qu'au client dont le nom correspond
				al.remove(i);
				display("Disconnected Client " + ct.username + " removed from list.");
				break;
			}
		}
	}//sendTo

	// for a client who logoff using the LOGOUT message
	synchronized void remove(int id) {
		// scan the array list until we found the Id
		for(int i = 0; i < al.size(); ++i) {
			ClientThread ct = al.get(i);
			// found it
			if(ct.id == id) {
				al.remove(i);
				return;
			}
		}
	}
	
	/*
	 *  To run as a console application just open a console window and: 
	 * > java Server
	 * > java Server portNumber
	 * If the port number is not specified 1500 is used
	 */ 
	public static void main(String[] args) {
		// start server on port 1500 unless a PortNumber is specified 
		int portNumber = 6969;
		// create a server object and start it
		Server server = new Server(portNumber);
		server.start();
	}

	/** One instance of this thread will run for each client */
	class ClientThread extends Thread {
		// the socket where to listen/talk
		Socket socket;
		ObjectInputStream sInput;
		ObjectOutputStream sOutput;
		// my unique id (easier for deconnection)
		int id;
		// the Username of the Client
		String username;
		// the only type of message a will receive
		ChatMessage cm;
		// the date I connect
		String date;

		// Constructore
		ClientThread(Socket socket) {
			// a unique id
			id = ++uniqueId;
			this.socket = socket;
			clientKeys = new Cryptage();
			/* Creating both Data Stream */
			System.out.println("Thread trying to create Object Input/Output Streams");
			try
			{
				// create output first
				sOutput = new ObjectOutputStream(socket.getOutputStream());
				sInput  = new ObjectInputStream(socket.getInputStream());
				// read the username
				username = (String) sInput.readObject();
				try {
					cm = (ChatMessage) sInput.readObject();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(!cm.getMessage().equals(sg.getPassword())) {					//v�rification du mot de passe
					display(username + "Mot de passe incorrect petit con !");
					remove(id);
					close();
				}
				display(username + " just connected.");
			}
			catch (IOException e) {
				display("Exception creating new Input/output Streams: " + e);
				return;
			}
			// have to catch ClassNotFoundException
			// but I read a String, I am sure it will work
			catch (ClassNotFoundException e) {
			}
            date = new Date().toString() + "\n";
		}

		// what will run forever
		public void run() {
			// to loop until LOGOUT
			boolean keepGoing = true;
			while(keepGoing) {
				// read a String (which is an object)
				try {
					cm = (ChatMessage) sInput.readObject();
				}
				catch (IOException e) {
					display(username + " Exception reading Streams: " + e);
					break;				
				}
				catch(ClassNotFoundException e2) {
					break;
				}
				// the messaage part of the ChatMessage
				String message = cm.getMessage();

				// Switch on the type of message receive
				switch(cm.getType()) {

				case ChatMessage.MESSAGE:
					broadcast(cm);
					break;
				case ChatMessage.LOGOUT:
					display(username + " disconnected with a LOGOUT message.");
					keepGoing = false;
					break;
				case ChatMessage.KEYCommon:
					sendKey(ChatMessage.KEYCommon, message);
					break;
				case ChatMessage.KEYPublic:
					sendKey(ChatMessage.KEYPublic, message);
					break;
				}
				
			}
			// remove myself from the arrayList containing the list of the
			// connected Clients
			remove(id);
			close();
		}
		
		// try to close everything
		private void close() {
			// try to close the connection
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

		/*
		 * Write a String to the Client output stream
		 */
		private boolean writeMsg(ChatMessage msg) {
			// if Client is still connected send the message to it
			if(!socket.isConnected()) {
				close();
				return false;
			}
			// write the message to the stream
			try {
				sOutput.writeObject(msg);
			}
			// if an error occurs, do not abort just inform the user
			catch(IOException e) {
				display("Error sending message to " + username);
				display(e.toString());
			}
			return true;
		}
		
		private void sendKey(int type, String key){
			if (type == ChatMessage.KEYCommon){
				clientKeys.setCommonKey(new BigInteger(key));
				clientCommonKeyGiven = true;
			}
			else if (type == ChatMessage.KEYPublic){
				clientKeys.setPublicKey(new BigInteger(key));
				clientPublicKeyGiven = true;
			}
			
			if (clientCommonKeyGiven && clientPublicKeyGiven){
				sendTo(this.username, new ChatMessage(ChatMessage.KEYCommon, clientKeys.encrypt(serverKeys.getCommonKey()).toString()));
				sendTo(this.username, new ChatMessage(ChatMessage.KEYPublic, clientKeys.encrypt(serverKeys.getPublicKey()).toString()));
				sendTo(this.username, new ChatMessage(ChatMessage.KEYPrivate, clientKeys.encrypt(serverKeys.getPrivateKey()).toString()));
				clientCommonKeyGiven = false;
				clientPublicKeyGiven = false;
			}
		}
	}
}

