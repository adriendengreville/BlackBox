package blackBox;
import java.io.*;
/*
 * Cette classe défini les différents types de messages qui seront échangés entre 
 * les clients et le serveur.
 */
public class ChatMessage implements Serializable {

	protected static final long serialVersionUID = 1112122200L;

	// Les différents types de messages:
	// PASSWORD pour un envois de mot de passe
	// MESSAGE pour un message normal transmit à tout le canal
	// LOGOUT pour signifier une déconnexion
	// KEY(Common/Public/Private) pour l'échange de clés
	// ConnectERR pour signifier une erreur de connexion à un client
	// ConnectOK pour signifier la réussite de la connexion au client
	// MP pour un message perso destiné à un client en particulier
	
	static final int PASSWORD = 0, MESSAGE = 1, LOGOUT = 2, KEYCommon = 3, KEYPublic = 4, KEYPrivate = 5, ConnectERR = 6, ConnectOK = 7, MP = 8;
	private int type;
	private String message;
	private String dest;
	private String timeStamp;
	private String sender;
	
	ChatMessage(int type, String message) {	//constructeur des messages génériques
		this.type = type;
		this.message = message;
		this.sender = "null";
	}
	
	public ChatMessage(int type, String message, String dest) { //constructeurs pour MP
		super();
		this.dest = dest;
	}
	
	// getters
	int getType() {
		return type;
	}
	
	void setType(int type){
		this.type = type;
	}
	
	String getMessage() {
		return message;
	}
	
	void setMessage(String msg){
		this.message = msg;
	}
	
	String getDest(){
		return this.dest;
	}
	
	void setDest(String dest){
		this.dest = dest;
	}
	
	void setTimeStamp(String time){
		this.timeStamp = time;
	}
	
	String getTimeStamp(){
		return this.timeStamp;
	}
	
	void setSender(String sender){
		this.sender = sender;
	}
	
	String getSender(){
		return this.sender;
	}
}

